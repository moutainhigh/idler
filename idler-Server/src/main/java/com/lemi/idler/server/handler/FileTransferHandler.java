package com.lemi.idler.server.handler;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import com.lemi.idler.server.Constants;
import com.lemi.idler.server.RequestHeadersWapper;
import com.lemi.idler.server.ResourceManager;
import com.lemi.idler.server.TransferLifecycle;
import com.lemi.idler.server.listener.ChannelProgessListener;
import com.lemi.idler.server.util.ResponseUtil;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;

@SuppressWarnings("restriction")
public class FileTransferHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private TransferLifecycle    lifeCycle;
    private RequestHeadersWapper headers;

    /** the transfer default buffer size 8k */
    private final static int     DEFAULT_BUFFER_SIZE = 1 << 13;

    private final int            buffersize;

    public FileTransferHandler(TransferLifecycle lifeCycle,
                               int buffersize) {
        this.lifeCycle = lifeCycle;
        this.buffersize = buffersize;
    }

    public FileTransferHandler(TransferLifecycle lifeCycle) {
        this( lifeCycle, DEFAULT_BUFFER_SIZE );
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx,
                                FullHttpRequest request) throws Exception {
        if (lifeCycle != null && !lifeCycle.verifyReauest( ctx, request )) {
            return;
        }

        final File file = initialFile( ctx, request );
        if (file == null) {
            return;
        }

        transfer( ctx, request, file );
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
            ResponseUtil.sendError( ctx, INTERNAL_SERVER_ERROR );
            ctx.close();
        }
    }

    private void transfer(ChannelHandlerContext ctx,
                          FullHttpRequest request,
                          File file) throws IOException {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile( file, "r" );// read only
        } catch (FileNotFoundException fnfe) {
            ResponseUtil.sendError( ctx, NOT_FOUND );// 404
            return;
        }

        if (!lifeCycle.preTransfer( randomAccessFile )) {
            return;
        }

        installResponse( ctx, request, randomAccessFile, file );

        transferDo( randomAccessFile, ctx, request );
    }

    private void installResponse(ChannelHandlerContext ctx,
                                 FullHttpRequest request,
                                 RandomAccessFile randomAccessFile,
                                 File file) throws IOException {
        long fileLength = randomAccessFile.length();
        HttpResponse response = new DefaultHttpResponse( HTTP_1_1, OK );
        HttpHeaderUtil.setContentLength( response, fileLength );
        setContentTypeHeader( response, file );
        if (HttpHeaderUtil.isKeepAlive( request )) {
            response.headers().set( CONNECTION, HttpHeaderValues.KEEP_ALIVE );
        }
        ctx.write( response );

    }

    private void transferDo(RandomAccessFile randomAccessFile,
                            ChannelHandlerContext ctx,
                            FullHttpRequest request) throws IOException {
        final ChannelFuture sendFileFuture;
        sendFileFuture = ctx.write( new ChunkedFile( randomAccessFile, 0, randomAccessFile.length(), buffersize ), ctx.newProgressivePromise() );

        headers = new RequestHeadersWapper( request.headers(), ctx );

        sendFileFuture.addListener( new ChannelProgessListener( lifeCycle, headers ) );

        ChannelFuture lastContentFuture = ctx.writeAndFlush( LastHttpContent.EMPTY_LAST_CONTENT );

        if (!HttpHeaderUtil.isKeepAlive( request )) {
            lastContentFuture.addListener( ChannelFutureListener.CLOSE );
        }
    }

    private File initialFile(ChannelHandlerContext ctx,
                             FullHttpRequest request) {
        final String uri = request.uri();
        final String path = sanitizeUri( uri );
        if (path == null) {
            ResponseUtil.sendError( ctx, FORBIDDEN );// 403
            return null;
        }

        File file = new File( path );
        if (file.isHidden() || !file.exists()) {
            ResponseUtil.sendError( ctx, NOT_FOUND ); // 404
            return null;
        }

        if (!file.isFile()) {
            ResponseUtil.sendError( ctx, FORBIDDEN ); // 403
            return null;
        }

        return file;
    }

    private static final Pattern INSECURE_URI = Pattern.compile( ".*[<>&\"].*" );

    private String sanitizeUri(String uri) {
        try {
            uri = URLDecoder.decode( uri, "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode( uri, "ISO-8859-1" );
            } catch (UnsupportedEncodingException e1) {
                throw new Error();
            }
        }

        // step 1 basic check
        if (!uri.startsWith( "/" )) {
            return null;
        }
        // step 2 replace filesystem separatorChar
        uri = uri.replace( '/', File.separatorChar );

        // step 3 url check again
        if (uri.contains( File.separator + '.' ) || uri.contains( '.' + File.separator ) || uri.startsWith( "." ) || uri.endsWith( "." )
                || INSECURE_URI.matcher( uri ).matches()) {
            return null;
        }

        return ResourceManager.instance().valueOf( Constants.Server.SERVER_FILE_ROOT ) + File.separator + uri;
    }

    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile( "[A-Za-z0-9][-_A-Za-z0-9\\.]*" );

    private static void setContentTypeHeader(HttpResponse response,
                                             File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set( CONTENT_TYPE, mimeTypesMap.getContentType( file ) );
    }

}