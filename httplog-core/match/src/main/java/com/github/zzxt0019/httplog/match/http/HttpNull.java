package com.github.zzxt0019.httplog.match.http;

import java.util.Date;

public class HttpNull implements HttpPacket {
    @Override
    public Date getPacketTime() {
        return null;
    }

    @Override
    public void setPacketTime(Date packetTime) {

    }

    @Override
    public Long getAckNum() {
        return null;
    }

    @Override
    public void setAckNum(Long askNum) {

    }
}
