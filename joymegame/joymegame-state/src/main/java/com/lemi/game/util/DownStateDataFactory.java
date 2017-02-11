package com.lemi.game.util;

import org.joda.time.DateTime;

import com.lemi.game.util.state.DownData;
import com.lemi.idler.server.Constants;
import com.lemi.idler.server.RequestHeadersWapper;

public class DownStateDataFactory {
    public static DownData build(int op,
                                 RequestHeadersWapper headers) {
        DownData data = new DownData();
        data.setAppkey( headers.getHeader( Constants.GameState.STATE_APPKEY_KEY ) );
        data.setDate( new DateTime().toString( "yyyy-MM-dd HH:mm:ss" ) );
        data.setDistro( headers.getHeader( Constants.GameState.STATE_DISTRO_KEY ) );
        data.setIp( headers.remoteAddress() );
        data.setOp( op );
        data.setRefer( headers.getHeader( Constants.GameState.HEADER_USER_REFERER_KEY ) );
        data.setUa( headers.getHeader( Constants.GameState.HEADER_USER_AGENT_KEY ) );
        data.setUrl( headers.getHeader( Constants.GameState.STATE_URI_KEY ) );
        return data;
    }

}
