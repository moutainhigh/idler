package com.lemi.idler.server;

import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class IdlerServer {
    private static final Logger                   log          = Logger.getLogger( IdlerServer.class );

    /** server default port */
    private final static int                      DEFAULT_PORT = 8080;
    /** the server bind port */
    private final int                             port;

    private volatile EventLoopGroup               bossGroup;
    private volatile EventLoopGroup               workerGroup;

    private volatile ServerBootstrap              bootstrap;

    private ChannelInitializer<? extends Channel> channelInitializer;

    /** use default server port */
    public IdlerServer(ChannelInitializer<? extends Channel> channelInitializer) {
        this( DEFAULT_PORT, channelInitializer );
    }

    /** constructors to set port */
    public IdlerServer(int port,
                       ChannelInitializer<? extends Channel> channelInitializer) {
        this.port = port;
        this.channelInitializer = channelInitializer;
    }

    /**
     * initial the server. instantiation NioEventLoopGroup,ServerBootstrap; then
     * group the channel
     */
    public void initialServer() {
        try {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            bootstrap = new ServerBootstrap();
            bootstrap.group( bossGroup, workerGroup ).channel( NioServerSocketChannel.class ).childHandler( channelInitializer );
        } catch (Exception e) {
            log.error( " inital server error: " + e.getMessage() );
            e.printStackTrace();
            System.exit( -1 );
        }
    }

    public void bindServer() {
        try {
            preBind( bossGroup, workerGroup, bootstrap );

            System.err.println( String.format( "server try bind :0.0.0.0:%d", this.port ) );
            ChannelFuture future = bootstrap.bind( "0.0.0.0", this.port ).sync();
            System.err.println( String.format( "IdlerServer bind [http-nio-%d]", this.port ) );

            postBind( bossGroup, workerGroup, bootstrap );

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            System.err.println( "  binding wrong...  " );
            e.printStackTrace();
            System.exit( -1 );
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /** before bind(start) the server. the method can be Override */
    public void preBind(EventLoopGroup boosGroup,
                        EventLoopGroup workGroup,
                        ServerBootstrap bootstrap) {
    }

    /** after the server started. the method can be Override */
    public void postBind(EventLoopGroup boosGroup,
                         EventLoopGroup workGroup,
                         ServerBootstrap bootstrap) {
    }
}
