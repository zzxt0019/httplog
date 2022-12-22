package com.github.zzxt0019.httplog.match.http;

import com.github.zzxt0019.httplog.match.IRequest;
import com.github.zzxt0019.httplog.codec.http11.Http11Request;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class HttpRequest extends Http11Request implements HttpPacket, IRequest<HttpRequest, HttpResponse> {
    protected Date packetTime;
    protected Long ackNum;
    protected String srcHost;  // 请求地址
    protected String dstHost;  // 目标地址
    protected Integer port;  // 端口
}
