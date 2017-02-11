package com.lemi.idler.server;

import java.io.RandomAccessFile;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.handler.codec.http.FullHttpRequest;

public interface TransferLifecycle {
    /**
     * when the server Receive the Request . we should check Whether legitimate.
     *
     * @return <code>true</code> verify success;<code>false</code> verify failed
     */
    boolean verifyReauest(ChannelHandlerContext ctx,
                          FullHttpRequest request);

    /** This method is called before the file transfer is started */
    boolean preTransfer(RandomAccessFile randomAccessFile);

    /**
     * every times transfer.the method would be call. notice:This method will be
     * called multiple times,even if (progress == total) ,does not mean the
     * progress has been completed.
     *
     * @param total
     *            : the file total size (bits)
     * @param progress
     *            :current transfer complete size
     * @param future:
     *            channel future {@link ChannelFuture}
     */
    boolean transferProgressed(ChannelProgressiveFuture future,
                               long progress,
                               long total);

    /**
     * When the download is complete, this method will be called.
     *
     * @param future:
     *            channel future {@link ChannelProgressiveFuture}
     * @param headers:
     *            HTTP protocol headers {@link RequestHeadersWapper}
     */
    boolean transferCompleted(ChannelProgressiveFuture future,
                              RequestHeadersWapper headers);

    /** This method is called when the transmission is interrupted */
    void transferInterrupted(ChannelProgressiveFuture future,
                             RequestHeadersWapper headers);
}
