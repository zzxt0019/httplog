package com.github.zzxt0019.httplog.example.test;

import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.util.NifSelector;

public class Test {
    public static void main(String[] args) throws Exception {
        PcapNetworkInterface nif = new NifSelector().selectNetworkInterface();
        PcapHandle pcapHandle = nif.openLive(99999,
                PcapNetworkInterface.PromiscuousMode.PROMISCUOUS,
                10);
        pcapHandle.loop(-1, (PacketListener) System.out::println);
        pcapHandle.close();
    }
}
