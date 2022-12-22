package com.github.zzxt0019.httplog.example.exporter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zzxt0019.httplog.example.common.bean.HttpLog;
import com.github.zzxt0019.httplog.example.common.mapper.HttpLogMapper;
import com.github.zzxt0019.httplog.example.exporter.utils.IpUtil;
import com.github.zzxt0019.httplog.match.http.HttpDecoder;
import com.github.zzxt0019.httplog.match.http.HttpPacket;
import com.github.zzxt0019.httplog.match.http.HttpRequest;
import com.github.zzxt0019.httplog.match.http.HttpResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.pcap4j.core.PacketListener;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

public class HttpListener implements PacketListener {
    private final HttpDecoder httpDecoder = new HttpDecoder();
    private final SqlSession sqlSession;

    public HttpListener(SqlSessionFactory sqlSessionFactory) {
        this.sqlSession = sqlSessionFactory.openSession(true);
    }

    @Override
    public void gotPacket(Packet packet) {
        HttpPacket httpPacket = null;
        try {
            httpPacket = httpDecoder.decode(packet);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try {
            if (httpPacket instanceof HttpRequest) {
                HttpRequest httpRequest = (HttpRequest) httpPacket;
                HttpLog httpLog = new HttpLog();
                IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                httpLog.setId(httpRequest.getPacketTime().getTime() + "-"
                        + ((ipV4Packet != null) ? ipToHex(ipV4Packet.getHeader().getSrcAddr().getHostAddress()) : "") + String.format("%04x", packet.get(TcpPacket.class).getHeader().getSrcPort().valueAsInt())
                        + ((ipV4Packet != null) ? ipToHex(ipV4Packet.getHeader().getDstAddr().getHostAddress()) : "") + String.format("%04x", packet.get(TcpPacket.class).getHeader().getDstPort().valueAsInt())
                        + "-" + httpRequest.getAckNum());
                httpLog.setSrcHost(IpUtil.getSrcHost(httpRequest));
                httpLog.setDstHost(IpUtil.getDstHost(httpRequest));
                httpLog.setReqPath(httpRequest.getPath());
                httpLog.setPort(httpRequest.getPort());
                httpLog.setReqTime(httpRequest.getPacketTime());
                httpLog.setReqHeaders(httpRequest.getHeaders());
                httpLog.setReqParameters(httpRequest.getParameters());
                httpLog.setReqMethod(httpRequest.getHttpMethod());
                httpLog.setReqBody(httpRequest.getBody());
                sqlSession.getMapper(HttpLogMapper.class).insert(httpLog);
            } else if (httpPacket instanceof HttpResponse) {
                HttpResponse httpResponse = (HttpResponse) httpPacket;
                IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                HttpLog httpLog = sqlSession.getMapper(HttpLogMapper.class).selectById(httpResponse.getTimeId() + "-"
                        + ((ipV4Packet != null) ? ipToHex(ipV4Packet.getHeader().getDstAddr().getHostAddress()) : "") + String.format("%04x", packet.get(TcpPacket.class).getHeader().getDstPort().valueAsInt())
                        + ((ipV4Packet != null) ? ipToHex(ipV4Packet.getHeader().getSrcAddr().getHostAddress()) : "") + String.format("%04x", packet.get(TcpPacket.class).getHeader().getSrcPort().valueAsInt())
                        + "-" + httpResponse.getSeqNum());
                httpLog.setResTime(httpResponse.getPacketTime());
                httpLog.setResHeaders(httpResponse.getHeaders());
                httpLog.setResCode(httpResponse.getResCode());
                httpLog.setResBody(httpResponse.getBody());
                sqlSession.getMapper(HttpLogMapper.class).updateById(httpLog);
            }
        } catch (Exception e) {
            try {
                System.out.println("error " + new ObjectMapper().writeValueAsString(httpPacket));
                e.printStackTrace();
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String ipToHex(String ip) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : ip.split("\\.")) {
            stringBuilder.append(String.format("%02x", Integer.parseInt(s)));
        }
        return stringBuilder.toString();
    }
}
