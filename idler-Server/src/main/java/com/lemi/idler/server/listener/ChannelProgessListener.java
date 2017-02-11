package com.lemi.idler.server.listener;

import org.apache.log4j.Logger;

import com.lemi.idler.server.RequestHeadersWapper;
import com.lemi.idler.server.TransferLifecycle;

import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;

public class ChannelProgessListener implements ChannelProgressiveFutureListener {
    private static final Logger  log = Logger.getLogger( ChannelProgessListener.class );

    private TransferLifecycle    lifecycle;
    private RequestHeadersWapper headers;

    public ChannelProgessListener(TransferLifecycle lifecycle,
                                  RequestHeadersWapper headers) {
        this.lifecycle = lifecycle;
        this.headers = headers;
    }

    private volatile long total;
    private volatile long transfered;

    public void operationProgressed(ChannelProgressiveFuture future,
                                    long progress,
                                    long total) throws InterruptedException {
        if (total < 0) {
            log.error( "unknow file total size!" );
        } else {
            this.total = total;
            this.transfered = progress;
            if (lifecycle != null && !lifecycle.transferProgressed( future, progress, total )) {
                future.channel().close().sync();
            }
        }
    }

    public void operationComplete(ChannelProgressiveFuture future) throws Exception {
        if (total > 0 && total == transfered) {
            if (log.isInfoEnabled()) {
                log.info( "Transfer complete." );
            }
            if (lifecycle != null && lifecycle.transferCompleted( future, headers )) {
                // shut down avoid recount!
                try {
                    future.channel().closeFuture().sync();
                } catch (Exception e) {
                }
            }
        } else {
            lifecycle.transferInterrupted( future, headers );
            if (log.isInfoEnabled()) {
                log.info( "Transfer interrupted." );
            }
            try {
                future.channel().closeFuture().sync();
            } catch (Exception e) {
            }
        }
    }

}
