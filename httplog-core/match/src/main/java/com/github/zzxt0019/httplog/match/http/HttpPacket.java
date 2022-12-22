package com.github.zzxt0019.httplog.match.http;

import java.util.Date;

public interface HttpPacket {

    Date getPacketTime();

    void setPacketTime(Date packetTime);

    Long getAckNum();

    void setAckNum(Long askNum);
}
