package com.lemi.game;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.lemi.game.decoder.UrlDecoder;
import com.lemi.game.strategy.CheckRequestStrategy;
import com.lemi.idler.server.Constants;
import com.lemi.idler.server.RequestHeadersWapper;
import com.lemi.idler.server.TransferLifecycle;
import com.lemi.idler.server.util.ResponseUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.handler.codec.http.FullHttpRequest;

public abstract class FileTransferStateHandler implements TransferLifecycle, FileDownListener {
    private String                        uri;
    private RequestHeadersWapper          headers;
    private List<CheckRequestStrategy>    checkRequestStrategysChain = new ArrayList<CheckRequestStrategy>();

    private UrlDecoder<? extends UrlInfo> urlDecoder;

    public boolean verifyReauest(ChannelHandlerContext ctx,
                                 FullHttpRequest request) {
        if (!request.decoderResult().isSuccess()) {
            ResponseUtil.sendError( ctx, BAD_REQUEST );// 400
            return false;
        }

        if (request.method() != GET) {
            ResponseUtil.sendError( ctx, METHOD_NOT_ALLOWED );// 405
            return false;
        }

        if (!fireCheckRequestStrategysChain( ctx, request )) {
            return false;
        }

        uri = request.uri();
        if (this.urlDecoder != null) {
            try {
                UrlInfo urlInfo = urlDecoder.decoder( uri );
                request.setUri( urlInfo.getUrlPath() );
                uri = request.uri();
                request.headers().add( Constants.GameState.STATE_DISTRO_KEY, urlInfo.getDistro() );
                request.headers().add( Constants.GameState.STATE_URI_KEY, urlInfo.getUrlPath() );
                request.headers().add( Constants.GameState.STATE_APPKEY_KEY, urlInfo.getAppKey() );
            } catch (IllegalArgumentException e) {
                ResponseUtil.sendError( ctx, NOT_FOUND );// 404
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                ResponseUtil.sendError( ctx, NOT_FOUND );// 404
                return false;
            }
        }

        headers = new RequestHeadersWapper( request.headers(), ctx );

        return true;
    }

    private boolean fireCheckRequestStrategysChain(ChannelHandlerContext ctx,
                                                   FullHttpRequest request) {
        for (CheckRequestStrategy checkRequestStrategy : checkRequestStrategysChain) {
            if (!checkRequestStrategy.check( ctx, request )) {
                ResponseUtil.sendError( ctx, BAD_REQUEST ); // 400
                return false;
            }
        }
        return true;
    }

    public boolean transferProgressed(ChannelProgressiveFuture future,
                                      long progress,
                                      long total) {
        return true;
    }

    public void transferInterrupted(ChannelProgressiveFuture future,
                                    RequestHeadersWapper headers) {
        cancel( uri, headers );
    }

    public boolean transferCompleted(ChannelProgressiveFuture future,
                                     RequestHeadersWapper headers) {
        downloadSuccess( uri, headers );
        return true;
    }

    public boolean preTransfer(RandomAccessFile randomAccessFile) {
        click( uri, headers );
        return true;
    }

    public FileTransferStateHandler setUrlDecoder(UrlDecoder<? extends UrlInfo> urlDecoder) {
        this.urlDecoder = urlDecoder;
        return this;
    }

    public FileTransferStateHandler addCheckRequestStrategysLast(String strategyName,
                                                                 CheckRequestStrategy strategy) {
        checkRequestStrategysChain.add( strategy );
        return this;
    }
}
