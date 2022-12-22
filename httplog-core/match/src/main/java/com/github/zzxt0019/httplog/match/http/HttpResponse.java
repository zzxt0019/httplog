package com.github.zzxt0019.httplog.match.http;

import com.github.zzxt0019.httplog.codec.http11.Http11Response;
import com.github.zzxt0019.httplog.match.IResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class HttpResponse extends Http11Response implements HttpPacket, IResponse<HttpRequest, HttpResponse> {
    protected Date packetTime;
    protected Long timeId;
    protected Long ackNum;
    protected Long seqNum;  // 和请求ackNum对应同一组
}
