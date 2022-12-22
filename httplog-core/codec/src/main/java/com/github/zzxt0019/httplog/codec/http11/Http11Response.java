package com.github.zzxt0019.httplog.codec.http11;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Http11Response extends Http11Packet {
    protected Integer resCode;
}
