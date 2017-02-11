package com.lemi.game.strategy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface CheckRequestStrategy {
    boolean check(ChannelHandlerContext ctx,
                  FullHttpRequest request);
}
