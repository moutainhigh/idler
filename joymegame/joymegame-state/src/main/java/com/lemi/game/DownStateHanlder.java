package com.lemi.game;

import org.joda.time.DateTime;

import com.lemi.game.decoder.AESUrlDecoder;
import com.lemi.game.strategy.IpLimitedStrategy;
import com.lemi.game.util.DownStateDataFactory;
import com.lemi.game.util.cache.JedisClientSingel;
import com.lemi.game.util.state.DownData;
import com.lemi.idler.server.Constants;
import com.lemi.idler.server.RequestHeadersWapper;
import com.lemi.idler.server.ResourceManager;
import com.lemi.idler.server.handler.ApplicationHandler;
import com.lemi.idler.server.handler.FileTransferHandler;

import io.netty.channel.ChannelPipeline;

public class DownStateHanlder extends ApplicationHandler {
    private final static String DOWN_DATA_STATE = ResourceManager.instance().valueOf( Constants.GameState.DOWN_DATA );
    private final static int    OP_CLICK        = 0;
    private final static int    OP_SUCCESS      = 1;

    @Override
    public void bussinessHandle(ChannelPipeline pipeline) {
        pipeline.addLast( "file-handler", new FileTransferHandler( new FileTransferStateHandler() {

            public void click(String url,
                              RequestHeadersWapper headers) {
                addToRedis( OP_CLICK, headers );

                System.out.println( String.format( "click - [%s] - [%s]", headers.remoteAddress(), url ) );
            }

            public void downloadSuccess(String url,
                                        RequestHeadersWapper headers) {
                addToRedis( OP_SUCCESS, headers );
                System.out.println( String.format( "success - [%s] - [%s]", headers.remoteAddress(), url ) );
            }

            public void cancel(String url,
                               RequestHeadersWapper headers) {
                System.out.println( String.format( "cancel - [%s] - [%s]", headers.remoteAddress(), url ) );
            }

            private void addToRedis(int op,
                                    RequestHeadersWapper headers) {
                DownData build = DownStateDataFactory.build( op, headers );
                String dataJson = build.toString();
                if (dataJson != null) {
                    String key = builderCurrentDataKey();
                    JedisClientSingel.instance().lpush( key, dataJson );
                }
            }

            private String builderCurrentDataKey() {
                String date = new DateTime().toString( "yyyy-MM-dd" );
                return DOWN_DATA_STATE + date;
            }

        }.setUrlDecoder( new AESUrlDecoder() ).addCheckRequestStrategysLast( "ip", new IpLimitedStrategy() ) ) );
    }

}
