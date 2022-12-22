package com.github.zzxt0019.httplog.example.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.zzxt0019.httplog.example.common.bean.HttpLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HttpLogMapper extends BaseMapper<HttpLog> {
}
