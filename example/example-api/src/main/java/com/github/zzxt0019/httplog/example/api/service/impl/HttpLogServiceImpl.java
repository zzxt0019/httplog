package com.github.zzxt0019.httplog.example.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.zzxt0019.httplog.example.api.service.HttpLogService;
import com.github.zzxt0019.httplog.example.common.bean.HttpLog;
import com.github.zzxt0019.httplog.example.common.mapper.HttpLogMapper;
import org.springframework.stereotype.Service;

@Service
public class HttpLogServiceImpl extends ServiceImpl<HttpLogMapper, HttpLog> implements HttpLogService {
}
