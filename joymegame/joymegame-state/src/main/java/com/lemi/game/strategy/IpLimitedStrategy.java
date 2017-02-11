package com.lemi.game.strategy;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.lemi.game.util.cache.JedisClientSingel;
import com.lemi.idler.server.Constants;
import com.lemi.idler.server.RequestHeadersWapper;
import com.lemi.idler.server.ResourceManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class IpLimitedStrategy implements CheckRequestStrategy {
    private static final Logger log                   = Logger.getLogger( IpLimitedStrategy.class );

    private final static String IP_LIMITED            = ResourceManager.instance().valueOf( Constants.IpLimited.IP_LIMITED );
    private final static String IP_LIMITED_TIMES      = ResourceManager.instance().valueOf( Constants.IpLimited.IP_LIMITED_TIMES );
    private final static int    IP_LIMITED_FREQ_TTIME = Integer
            .parseInt( ResourceManager.instance().valueOf( Constants.IpLimited.IP_LIMITED_FREQ_TIME ) );
    private final static int    IP_LIMITED_FREQ_COUNT = Integer
            .parseInt( ResourceManager.instance().valueOf( Constants.IpLimited.IP_LIMITED_FREQ_COUNT ) );

    public boolean check(ChannelHandlerContext ctx,
                         FullHttpRequest request) {
        final String ip = new RequestHeadersWapper( request.headers(), ctx ).remoteAddress();
        return !checkIpIsLimited( ip );
    }

    /**
     * First, check whether IP is in the blacklist.Then the IP each request,
     * within the specified time({@value IP_LIMITED_FREQ_TTIME}) plus 1.If
     * greater than the specified number({@value IP_LIMITED_FREQ_COUNT}) of
     * times:return false;
     */
    private boolean checkIpIsLimited(String ip) {
        final JedisClientSingel jedis = JedisClientSingel.instance();
        String isLimited = jedis.hget( IP_LIMITED, ip );
        if (StringUtils.isNotBlank( isLimited ) && !StringUtils.equalsIgnoreCase( "null", isLimited )) {
            if (log.isInfoEnabled()) {
                log.info( String.format( "ip:[ %s ] is limited", ip ) );
            }
            return true;
        }

        final String key = IP_LIMITED_TIMES + ip;
        Long incrTotal = jedis.incr( key );
        if (log.isInfoEnabled()) {
            log.info( String.format( "ip [ %s ] : %d ", ip, incrTotal ) );
        }
        if (incrTotal <= 1) {
            jedis.expire( key, IP_LIMITED_FREQ_TTIME );
            return false;
        } else if (incrTotal > IP_LIMITED_FREQ_COUNT) {
            jedis.hset( IP_LIMITED, ip, "0" );
            return true;
        }
        return false;
    }
}
