package com.github.zzxt0019.httplog.codec.http11;

import com.github.zzxt0019.httplog.codec.http11.utils.Http11Util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Http11Decoder {
    private static final ByteBuf HTTP = Unpooled.wrappedBuffer("HTTP/1.1".getBytes(StandardCharsets.UTF_8));
    private static final ByteBuf line1 = Unpooled.wrappedBuffer("\r\n".getBytes(StandardCharsets.UTF_8));
    private static final ByteBuf line2 = Unpooled.wrappedBuffer("\r\n\r\n".getBytes(StandardCharsets.UTF_8));

    public Http11Packet decode(ByteBuf buf) {
        List<ByteBuf> lineBody = Http11Util.split(buf, line2, 2);  // [0] lines [1] body
        List<ByteBuf> lines = Http11Util.split(lineBody.get(0), line1, 2);
        Http11Packet http11Packet = readStatusLine(lines.get(0));
        if (http11Packet != null) {
            if (lines.size() == 2) {
                http11Packet.setHeaders(readHeaderLines(Http11Util.split(lines.get(1), line1)));
            }
            if (lineBody.size() == 2) {
                append(http11Packet, lineBody.get(1));
            }
            return http11Packet;
        }
        return null;
    }

    private Http11Packet readStatusLine(ByteBuf statusLine) {
        if (statusLine != null) {
            List<ByteBuf> split = Http11Util.split(statusLine, Unpooled.wrappedBuffer(new byte[]{' '}));

            if (split.size() == 3 && ByteBufUtil.equals(split.get(2), HTTP)) {
                Http11Request httpRequest = new Http11Request();
                httpRequest.setHttpMethod(split.get(0).toString(StandardCharsets.UTF_8));
                List<ByteBuf> urlSplit = Http11Util.split(split.get(1), Unpooled.wrappedBuffer(new byte[]{'?'}), 2);
                httpRequest.setPath(urlSplit.get(0).toString(StandardCharsets.UTF_8));
                if (urlSplit.size() == 2) {
                    Map<String, Object> parameters = new HashMap<>();
                    for (ByteBuf keyEqValue : Http11Util.split(urlSplit.get(1), Unpooled.wrappedBuffer(new byte[]{'&'}))) {
                        List<ByteBuf> keyValueSplit = Http11Util.split(keyEqValue, Unpooled.wrappedBuffer(new byte[]{'='}), 2);
                        String key = Http11Util.urlDecode(keyValueSplit.get(0).toString(StandardCharsets.UTF_8));
                        if (keyValueSplit.size() == 2) {
                            for (ByteBuf value : Http11Util.split(keyValueSplit.get(1), Unpooled.wrappedBuffer(new byte[]{','}))) {
                                Http11Util.mapPut(parameters, key, Http11Util.urlDecode(value.toString(StandardCharsets.UTF_8)));
                            }
                        }
                    }
                    httpRequest.setParameters(parameters);
                }
                return httpRequest;
            } else if (split.size() >= 2 && ByteBufUtil.equals(split.get(0), (HTTP))) {
                Http11Response httpResponse = new Http11Response();
                httpResponse.setResCode(Integer.parseInt(split.get(1).toString(StandardCharsets.UTF_8)));
                return httpResponse;
            }
        }
        return null;
    }

    private Map<String, Object> readHeaderLines(List<ByteBuf> headerLines) {
        if (headerLines != null && headerLines.size() > 0) {
            Map<String, Object> headers = new HashMap<>();
            for (ByteBuf headerLine : headerLines) {
                List<ByteBuf> split = Http11Util.split(headerLine, Unpooled.wrappedBuffer(new byte[]{':', ' '}), 2);
                if (split.size() == 2) {
                    Http11Util.mapPut(headers, split.get(0).toString(StandardCharsets.UTF_8), split.get(1).toString(StandardCharsets.UTF_8));
                }
            }
            return headers;
        }
        return null;
    }

    public void append(Http11Packet packet, ByteBuf append) {
        if (checkTransferEncodingChunked(packet)) {  // Transfer-Encoding: chunked
            ByteBuf cacheBuf = (ByteBuf) packet.getHeaders().getOrDefault("Body-Cache", ByteBufAllocator.DEFAULT.buffer());
            cacheBuf = ByteBufAllocator.DEFAULT.compositeBuffer().addComponents(true, cacheBuf, append);
            while (cacheBuf.readableBytes() > 0) {
                int splitIndex = ByteBufUtil.indexOf(line1, cacheBuf);
                int length = Integer.parseInt(cacheBuf.slice(0, splitIndex).toString(StandardCharsets.UTF_8), 0x10);
                if (length == 0) {
                    packet.getHeaders().remove("Body-Cache");
                    ByteBuf dataBuf = (ByteBuf) packet.getHeaders().remove("Body-Data");
                    packet.setBody(dataBuf.toString(StandardCharsets.UTF_8));
                    ReferenceCountUtil.safeRelease(cacheBuf);
                    packet.setComplete(true);
                    return;
                } else if (cacheBuf.readableBytes() - splitIndex - line1.readableBytes() > length) {
                    ByteBuf dataBuf = cacheBuf.slice(splitIndex + line1.readableBytes(), length);
                    cacheBuf = cacheBuf.slice(splitIndex + line1.readableBytes() * 2 + length,
                            cacheBuf.readableBytes() - splitIndex - line1.readableBytes() * 2 - length);
                    packet.getHeaders().put("Body-Cache", cacheBuf);
                    packet.getHeaders().put("Body-Data", packet.getHeaders().containsKey("Body-Data") ?
                            ByteBufAllocator.DEFAULT.compositeBuffer().addComponents(true, (ByteBuf) packet.getHeaders().get("Body-Data"), dataBuf) : dataBuf);
                } else {
                    packet.getHeaders().put("Body-Cache", cacheBuf);
                    break;
                }
            }
            return;
        }
        int length = checkContentLengthInt(packet);
        if (length > 0) {  // 是Content-Length类型
            ByteBuf dataBuf = (ByteBuf) packet.getHeaders().getOrDefault("Body", ByteBufAllocator.DEFAULT.buffer());
            dataBuf = ByteBufAllocator.DEFAULT.compositeBuffer().addComponents(true, dataBuf, append);
            if (dataBuf.readableBytes() < length) {
                packet.getHeaders().put("Body", dataBuf);
            } else {
                packet.setComplete(true);
                packet.setBody(dataBuf.toString(StandardCharsets.UTF_8));
                ReferenceCountUtil.safeRelease(dataBuf);
                packet.getHeaders().remove("Body");
            }
            return;
        }
        packet.setComplete(true);
    }

    private boolean checkTransferEncodingChunked(Http11Packet httpPacket) {
        if (httpPacket.getHeaders() != null && httpPacket.getHeaders().containsKey("Transfer-Encoding")) {
            Object obj = httpPacket.getHeaders().get("Transfer-Encoding");
            if (obj instanceof List) {
                for (Object value : (List) obj) {
                    if (value.equals("chunked")) {
                        return true;
                    }
                }
            } else {
                return obj.equals("chunked");
            }
        }
        return false;
    }

    private int checkContentLengthInt(Http11Packet httpPacket) {
        if (httpPacket.getHeaders() != null && httpPacket.getHeaders().containsKey("Content-Length")) {
            Object obj = httpPacket.getHeaders().get("Content-Length");
            if (obj instanceof List) {
                for (Object value : (List) obj) {
                    try {
                        int length = Integer.parseInt(String.valueOf(value));
                        if (length >= 0) {
                            return length;
                        }
                    } catch (NumberFormatException ignore) {
                    }
                }
            } else {
                try {
                    return Integer.parseInt(String.valueOf(obj));
                } catch (NumberFormatException ignore) {
                }
            }
        }
        return -1;
    }
}
