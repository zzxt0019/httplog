package com.github.zzxt0019.httplog.example.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.zzxt0019.httplog.example.api.service.HttpLogService;
import com.github.zzxt0019.httplog.example.common.bean.HttpLog;
import com.github.zzxt0019.httplog.example.common.bean.RetDTO;
import com.github.zzxt0019.httplog.example.common.utils.MybatisPlusUtil;
import com.github.zzxt0019.httplog.match.http.HttpDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/httpLog")
public class HttpLogController {
    @Autowired
    private HttpLogService httpLogService;
    @GetMapping("/select")
    public RetDTO<IPage<HttpLog>> select(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) List<String> reqMethod,
            @RequestParam(required = false) List<String> srcHost,
            @RequestParam(required = false) List<String> dstHost,
            @RequestParam(required = false) List<Integer> port,
            @RequestParam(required = false) List<Integer> resCode,
            @RequestParam(required = false) String reqTimeMin,
            @RequestParam(required = false) String reqTimeMax,
            @RequestParam(required = false) String resTimeMin,
            @RequestParam(required = false) String resTimeMax,
            @RequestParam(required = false) String reqPath,
            @RequestParam(required = false) Set<Integer> error) {
        QueryWrapper<HttpLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(HttpLog.class, col ->
                !col.getProperty().equals("reqBody")
                        && !col.getProperty().equals("resBody")
                        && !col.getProperty().equals("reqHeaders")
                        && !col.getProperty().equals("reqParameters")
                        && !col.getProperty().equals("resHeaders"));
        MybatisPlusUtil.in(queryWrapper,
                new String[]{"req_method", "src_host", "dst_host", "port", "res_code"},
                new List[]{reqMethod, srcHost, dstHost, port, resCode});
        MybatisPlusUtil.ge(queryWrapper,
                new String[]{"req_time", "res_time"},
                new Object[]{reqTimeMin, resTimeMin});
        MybatisPlusUtil.le(queryWrapper,
                new String[]{"req_time", "res_time"},
                new Object[]{reqTimeMax, resTimeMax});
        MybatisPlusUtil.like(queryWrapper,
                new String[]{"req_path"},
                new Object[]{reqPath});
        if (error != null) {
            queryWrapper.and(qw1 -> {
                if (error.contains(0)) {  //
                    qw1.or(qw2 -> qw2.lt("res_code", 400)
                            .or().ge("req_time", new Date(System.currentTimeMillis() - HttpDecoder.timeout)));
                }
                if (error.contains(1)) {
                    qw1.or(qw2 -> qw2.isNull("res_code")
                            .lt("req_time", new Date(System.currentTimeMillis() - HttpDecoder.timeout)));
                }
                if (error.contains(2)) {
                    qw1.or(qw2 -> qw2.ge("res_code", 400));
                }
            });
        }
        queryWrapper.orderByDesc("req_time");
        IPage<HttpLog> iPage = httpLogService.page(new Page<>(page, size), queryWrapper);
        return RetDTO.data(iPage);
    }

    @GetMapping("/selectById")
    public RetDTO<HttpLog> selectById(@RequestParam String id) {
        HttpLog httpLog = httpLogService.getById(id);
        return RetDTO.data(httpLog);
    }
}
