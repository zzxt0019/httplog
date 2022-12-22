package com.github.zzxt0019.httplog.codec.http11;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class Http11Request extends Http11Packet {
    protected String path;
    protected Map<String, Object> parameters;
    protected String httpMethod;
}
