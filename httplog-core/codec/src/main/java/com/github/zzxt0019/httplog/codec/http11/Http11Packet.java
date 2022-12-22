package com.github.zzxt0019.httplog.codec.http11;

import lombok.Data;

import java.util.Map;

@Data
public abstract class Http11Packet {
    protected boolean complete;
    protected Map<String, Object> headers;
    protected String body;
}
