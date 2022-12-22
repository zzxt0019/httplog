package com.github.zzxt0019.httplog.codec.http11.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Http11Util {
    public static String urlDecode(String url) {
        try {
            return URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    public static void mapPut(Map<String, Object> map, String key, String value) {
        if (map.containsKey(key)) {
            if (map.get(key) instanceof List) {
                ((List) map.get(key)).add(value);
            } else {
                List<String> values = new ArrayList<>();
                values.add(String.valueOf(map.get(key)));
                values.add(value);
                map.put(key, values);
            }
        } else {
            map.put(key, value);
        }
    }

    public static List<ByteBuf> split(ByteBuf longData, ByteBuf shortData, int count) {
        int indexOf;
        List<ByteBuf> bufList = new ArrayList<>();
        while ((indexOf = ByteBufUtil.indexOf(shortData, longData)) != -1) {
            if (count != -1 && bufList.size() == count - 1) {
                break;
            }
            bufList.add(longData.slice(0, indexOf));
            longData = longData.slice(indexOf + shortData.readableBytes(), longData.readableBytes() - indexOf - shortData.readableBytes());
        }
        bufList.add(longData);
        return bufList;
    }

    public static List<ByteBuf> split(ByteBuf longData, ByteBuf shortData) {
        return split(longData, shortData, -1);
    }
}
