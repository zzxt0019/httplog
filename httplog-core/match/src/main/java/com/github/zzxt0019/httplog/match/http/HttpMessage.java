package com.github.zzxt0019.httplog.match.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpMessage {
    private Long time;
    private Integer reqPort;
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
}
