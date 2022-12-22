package com.github.zzxt0019.httplog.match.http;

import com.github.zzxt0019.httplog.match.IDecoder;
import com.github.zzxt0019.httplog.codec.http11.Http11Decoder;
import com.github.zzxt0019.httplog.codec.http11.Http11Packet;
import com.github.zzxt0019.httplog.codec.http11.Http11Request;
import com.github.zzxt0019.httplog.codec.http11.Http11Response;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

public class HttpDecoder implements IDecoder<HttpPacket> {
    public static final long timeout = 100_000;  // ms
    private final ConcurrentLinkedDeque<HttpMessage> messages = new ConcurrentLinkedDeque<>();
    private final Http11Decoder http11Decoder = new Http11Decoder();

    public HttpPacket decode(Packet packet) {
        IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
        TcpPacket tcpPacket = packet.get(TcpPacket.class);
        if (tcpPacket != null && tcpPacket.getPayload() != null && tcpPacket.getPayload().getRawData() != null && tcpPacket.getPayload().getRawData().length > 0) {
            HttpPacket httpPacket = match(tcpPacket, ipV4Packet);
            if (httpPacket == null) {
                ByteBuf rawData = Unpooled.wrappedBuffer(tcpPacket.getPayload().getRawData());
                Http11Packet http11Packet = http11Decoder.decode(rawData);
                if (http11Packet == null) {
                    return new HttpNull();
                }
                if (http11Packet instanceof Http11Request) {
                    Http11Request http11Request = (Http11Request) http11Packet;
                    HttpRequest httpRequest = new HttpRequest();
                    httpRequest.setPacketTime(new Date());
                    httpRequest.setAckNum(tcpPacket.getHeader().getAcknowledgmentNumberAsLong());
                    httpRequest.setPath(http11Request.getPath());
                    httpRequest.setHttpMethod(http11Request.getHttpMethod());
                    httpRequest.setParameters(http11Request.getParameters());
                    httpRequest.setHeaders(http11Request.getHeaders());
                    httpRequest.setBody(http11Request.getBody());
                    httpRequest.setComplete(http11Request.isComplete());

                    if (ipV4Packet != null) {
                        httpRequest.setSrcHost(ipV4Packet.getHeader().getSrcAddr().getHostAddress());
                        httpRequest.setDstHost(ipV4Packet.getHeader().getDstAddr().getHostAddress());
                    }
                    httpRequest.setPort(tcpPacket.getHeader().getDstPort().valueAsInt());
                    match(httpRequest, tcpPacket, ipV4Packet);
                    if (httpRequest.isComplete()) {
                        return httpRequest;
                    }
                } else if (http11Packet instanceof Http11Response) {
                    Http11Response http11Response = (Http11Response) http11Packet;
                    HttpResponse httpResponse = new HttpResponse();
                    httpResponse.setPacketTime(new Date());
                    httpResponse.setAckNum(tcpPacket.getHeader().getAcknowledgmentNumberAsLong());
                    httpResponse.setSeqNum(tcpPacket.getHeader().getSequenceNumberAsLong());
                    httpResponse.setResCode(http11Response.getResCode());
                    httpResponse.setHeaders(http11Response.getHeaders());
                    httpResponse.setBody(http11Response.getBody());
                    httpResponse.setComplete(http11Response.isComplete());
                    HttpPacket resultPacket = match(httpResponse, tcpPacket, ipV4Packet);
                    if (resultPacket instanceof HttpResponse && ((HttpResponse) resultPacket).isComplete()) {
                        return resultPacket;
                    }
                }
            } else if (!(httpPacket instanceof HttpNull)) {
                return httpPacket;
            }
        }
        return new HttpNull();
    }

    private HttpPacket match(TcpPacket tcpPacket, IpV4Packet ipV4Packet) {
        long time = System.currentTimeMillis();
        Iterator<HttpMessage> iterator = messages.iterator();
        HttpMessage message;
        while ((message = iteratorNext(iterator)) != null) {
            if (time - message.getTime() < timeout) {
                if (message.getHttpRequest() != null
                        && Objects.equals(tcpPacket.getHeader().getSrcPort().valueAsInt(), message.getReqPort())
                        && Objects.equals(tcpPacket.getHeader().getDstPort().valueAsInt(), message.getHttpRequest().getPort())
                        && Objects.equals(tcpPacket.getHeader().getAcknowledgmentNumberAsLong(), message.getHttpRequest().getAckNum())) {
                    if (ipV4Packet != null
                            && Objects.equals(ipV4Packet.getHeader().getSrcAddr().getHostAddress(), message.getHttpRequest().getSrcHost())
                            && Objects.equals(ipV4Packet.getHeader().getDstAddr().getHostAddress(), message.getHttpRequest().getDstHost())) {
                        synchronized (message.getHttpRequest()) {
                            http11Decoder.append(message.getHttpRequest(), Unpooled.wrappedBuffer(tcpPacket.getPayload().getRawData()));
                        }
                        if (message.getHttpRequest().isComplete()) {
                            return message.getHttpRequest();
                        } else {
                            return new HttpNull();
                        }
                    }
                } else if (message.getHttpResponse() != null
                        && Objects.equals(tcpPacket.getHeader().getSrcPort().valueAsInt(), message.getHttpRequest().getPort())
                        && Objects.equals(tcpPacket.getHeader().getDstPort().valueAsInt(), message.getReqPort())
                        && Objects.equals(tcpPacket.getHeader().getAcknowledgmentNumberAsLong(), message.getHttpResponse().getAckNum())) {
                    if (ipV4Packet != null
                            && Objects.equals(ipV4Packet.getHeader().getSrcAddr().getHostAddress(), message.getHttpRequest().getDstHost())
                            && Objects.equals(ipV4Packet.getHeader().getDstAddr().getHostAddress(), message.getHttpRequest().getSrcHost())) {
                        synchronized (message.getHttpResponse()) {
                            http11Decoder.append(message.getHttpResponse(), Unpooled.wrappedBuffer(tcpPacket.getPayload().getRawData()));
                        }
                        if (message.getHttpResponse().isComplete()) {
                            iterator.remove();
                            return message.getHttpResponse();
                        } else {
                            return new HttpNull();
                        }
                    }
                }
            } else {
                iterator.remove();
            }
        }
        return null;
    }

    private HttpPacket match(HttpPacket httpPacket, TcpPacket tcpPacket, IpV4Packet ipV4Packet) {
        long time = System.currentTimeMillis();
        if (httpPacket instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) httpPacket;
            messages.addLast(new HttpMessage(time, tcpPacket.getHeader().getSrcPort().valueAsInt(), (HttpRequest) httpPacket, null));
            if (httpRequest.isComplete()) {
                return httpRequest;
            }
        } else if (httpPacket instanceof HttpResponse) {
            HttpResponse httpResponse = (HttpResponse) httpPacket;
            Iterator<HttpMessage> iterator = messages.iterator();
            HttpMessage message;
            while ((message = iteratorNext(iterator)) != null) {
                if (time - message.getTime() < timeout) {
                    if (message.getHttpRequest() != null
                            && Objects.equals(tcpPacket.getHeader().getSrcPort().valueAsInt(), message.getHttpRequest().getPort())
                            && Objects.equals(tcpPacket.getHeader().getDstPort().valueAsInt(), message.getReqPort())
                            && Objects.equals(httpResponse.getSeqNum(), message.getHttpRequest().getAckNum())) {
                        if (ipV4Packet != null
                                && Objects.equals(ipV4Packet.getHeader().getSrcAddr().getHostAddress(), message.getHttpRequest().getDstHost())
                                && Objects.equals(ipV4Packet.getHeader().getDstAddr().getHostAddress(), message.getHttpRequest().getSrcHost())) {
                            httpResponse.setTimeId(message.getHttpRequest().getPacketTime().getTime());
                            if (httpResponse.isComplete()) {
                                iterator.remove();
                                return httpResponse;
                            } else {
                                message.setHttpResponse(httpResponse);
                            }
                        }
                    }
                } else {
                    iterator.remove();
                }
            }
        }
        return new HttpNull();
    }

    private synchronized <T> T iteratorNext(Iterator<T> iterator) {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
}