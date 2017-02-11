package com.lemi.idler.server;

import java.util.Iterator;
import java.util.Map.Entry;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;

public class RequestHeadersWapper {
    private HttpHeaders           headers;
    private ChannelHandlerContext ctx;

    public RequestHeadersWapper(HttpHeaders headers,
                                ChannelHandlerContext ctx) {
        this.headers = headers;
        this.ctx = ctx;
    }

    public Iterator<Entry<CharSequence, CharSequence>> headers() {
        Iterator<Entry<CharSequence, CharSequence>> iterator = headers.iterator();
        return iterator;
    }

    public String getHeader(String parameter) {
        CharSequence charSequence = headers.get( parameter );
        if (charSequence != null) {
            return charSequence.toString();
        }
        return null;
    }

    public String remoteAddress() {
        String ip = getHeader( "X-Real-IP" );
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase( ip )) {
            ip = getHeader( "X-Forwarded-For" );
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase( ip )) {
            ip = getHeader( "Proxy-Client-IP" );
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase( ip )) {
            ip = getHeader( "WL-Proxy-Client-IP" );
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase( ip )) {
            ip = ctx.channel().remoteAddress().toString();
            ip = ip.substring( 1, ip.lastIndexOf( ":" ) );
        }
        return ip;
    }
}
