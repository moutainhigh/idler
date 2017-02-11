package com.lemi.idler.server.util;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.LOCATION;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

public class ResponseUtil {
    public static void sendError(ChannelHandlerContext ctx,
                                 HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse( HTTP_1_1, status,
                Unpooled.copiedBuffer( "Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8 ) );
        response.headers().set( CONTENT_TYPE, "text/plain; charset=UTF-8" );
        ctx.writeAndFlush( response ).addListener( ChannelFutureListener.CLOSE );
    }

    public static void sendRedirect(ChannelHandlerContext ctx,
                                    String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse( HTTP_1_1, FOUND );
        response.headers().set( LOCATION, newUri );
        ctx.writeAndFlush( response ).addListener( ChannelFutureListener.CLOSE );
    }
}
