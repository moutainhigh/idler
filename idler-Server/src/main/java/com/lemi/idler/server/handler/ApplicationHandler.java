package com.lemi.idler.server.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public abstract class ApplicationHandler extends ChannelInitializer<SocketChannel> {
    private ChannelPipeline pipeline;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        this.pipeline = ch.pipeline();
        installHttpPipleLine();
    }

    private void installHttpPipleLine() {
        // HTTP DECODER
        pipeline.addLast( "http-decoder", new HttpRequestDecoder() );

        // ObjectAggregator:The role is to convert multiple messages into a
        // single FullHttpRequest or FullHttpResponse
        pipeline.addLast( "http-aggregator", new HttpObjectAggregator( 65536 ) );

        // HTTP ENCODER
        pipeline.addLast( "http-encoder", new HttpResponseEncoder() );

        // The main role is to support asynchronous transmission of the code
        // (large file transfer), but not too much memory dedicated to prevent
        // JAVA memory overflow
        pipeline.addLast( "http-chunked", new ChunkedWriteHandler() );

        // Processing HTTP service requests
        bussinessHandle( pipeline );
    }

    /** Adding business logic to ChannelPipeline */
    public abstract void bussinessHandle(ChannelPipeline pipeline);
}
