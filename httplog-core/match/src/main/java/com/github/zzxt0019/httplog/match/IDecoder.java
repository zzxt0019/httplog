package com.github.zzxt0019.httplog.match;

import org.pcap4j.packet.Packet;

public interface IDecoder<IPacket> {
    IPacket decode(Packet packet);
}
