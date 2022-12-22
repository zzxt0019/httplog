package com.github.zzxt0019.httplog.example.exporter.utils;


import com.github.zzxt0019.httplog.match.http.HttpRequest;

import java.util.List;

public class IpUtil {
    public static String getSrcHost(HttpRequest request) {
        String ip;
        if (request.getHeaders() != null) {
            ip = String.valueOf(request.getHeaders().get("x-forwarded-for"));
            if ("null".equals(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = String.valueOf(request.getHeaders().get("Proxy-Client-IP"));
            }
            if ("null".equals(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = String.valueOf(request.getHeaders().get("WL-Proxy-Client-IP"));
            }
            if ("null".equals(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = String.valueOf(request.getHeaders().get("HTTP_CLIENT_IP"));
            }
            if ("null".equals(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = String.valueOf(request.getHeaders().get("HTTP_X_FORWARDED_FOR"));
            }
            if ("null".equals(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getSrcHost();
            }
        } else {
            ip = request.getSrcHost();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip.split(",")[0];
    }

    public static String getDstHost(HttpRequest request) {
        if (request.getHeaders() != null && request.getHeaders().containsKey("Host")) {
            Object host = request.getHeaders().get("Host");
            if (host instanceof List) {
                List list = (List) host;
                for (Object o : list) {
                    String value = String.valueOf(o);
                    String[] split = value.split(":");
                    if (split.length == 2) {
                        return split[0];
                    }
                }
            } else {
                String[] split = String.valueOf(host).split(":");
                if (split.length == 2) {
                    return split[0];
                }
            }
        }
        return request.getDstHost();
    }
}