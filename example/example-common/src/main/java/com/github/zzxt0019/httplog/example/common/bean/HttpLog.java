package com.github.zzxt0019.httplog.example.common.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.zzxt0019.httplog.match.http.HttpDecoder;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@TableName(autoResultMap = true)
public class HttpLog {
    @TableId
    private String id;

    private String srcHost;
    private String dstHost;
    private Integer port;
    private String reqPath;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Date reqTime;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> reqHeaders;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> reqParameters;
    private String reqMethod;
    private String reqBody;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Date resTime;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> resHeaders;
    private Integer resCode;
    private String resBody;
    @TableField(exist = false)
    private Integer error;  // 0 无错误   1 超时   2 响应码错误

    public Integer getError() {
        if (error == null) {
            error = 0;
            if (resCode != null && resCode >= 400) {
                error = 2;
            }
            if (resCode == null && System.currentTimeMillis() - reqTime.getTime() > HttpDecoder.timeout) {
                error = 1;
            }
        }
        return error;
    }
}
