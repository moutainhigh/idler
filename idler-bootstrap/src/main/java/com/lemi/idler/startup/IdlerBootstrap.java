package com.lemi.idler.startup;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.lemi.idler.server.Constants;
import com.lemi.idler.server.IdlerServer;
import com.lemi.idler.server.ResourceManager;
import com.lemi.idler.server.handler.ApplicationHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;

public class IdlerBootstrap {
    private static final Logger         log          = Logger.getLogger( IdlerBootstrap.class );

    private volatile static Object      serverDaemon = null;
    private static IdlerBootstrap       daemon       = null;

    private volatile ApplicationHandler handler;

    public static void main(String[] args) {
        final IdlerBootstrap bootstrap = new IdlerBootstrap();
        try {
            bootstrap.init();
        } catch (Throwable t) {
            handleThrowable( t );
            t.printStackTrace();
            return;
        }
        daemon = bootstrap;

        try {
            String command = "start";
            if (args.length > 0) {
                command = args[ args.length - 1 ];
            }

            if (command.equals( "start" )) {
                daemon.start();
            } else if (command.equals( "stop" )) {
                daemon.stop();
            } else {
                log.warn( String.format( "IdlerBootstrap: command %s does not exist.", command ) );
            }
        } catch (Throwable t) {
            if (t instanceof InvocationTargetException && t.getCause() != null) {
                t = t.getCause();
            }
            handleThrowable( t );
            t.printStackTrace();
            System.exit( 1 );
        }
    }

    private void stop() {
    }

    private void start() throws Exception {
        if (serverDaemon == null)
            init();
        Method method = serverDaemon.getClass().getMethod( "bindServer" );
        method.invoke( serverDaemon );
    }

    private void init() throws Exception {
        PropertyConfigurator.configure( ResourceManager.instance().confPath() + File.separator + "log4j.properties" );

        this.handler = (ApplicationHandler) Class.forName( ResourceManager.instance().valueOf( Constants.Server.SERVER_BUSSINESS_HANLDER ) )
                .newInstance();

        serverDaemon = new IdlerServer( getConfPort(), handler ) {
            private volatile long startMillies;

            @Override
            public void preBind(EventLoopGroup boosGroup,
                                EventLoopGroup workGroup,
                                ServerBootstrap bootstrap) {
                System.err.println( "server starting..." );
                startMillies = System.currentTimeMillis();
                super.preBind( boosGroup, workGroup, bootstrap );
            }

            @Override
            public void postBind(EventLoopGroup boosGroup,
                                 EventLoopGroup workGroup,
                                 ServerBootstrap bootstrap) {
                System.err.println( String.format( "Server startup in %d ms.", (System.currentTimeMillis() - startMillies) ) );
                super.postBind( boosGroup, workGroup, bootstrap );
            }
        };

        Method method = serverDaemon.getClass().getMethod( "initialServer" );
        method.invoke( serverDaemon );
    }

    private int getConfPort() {
        final int port;
        try {
            port = Integer.parseInt( ResourceManager.instance().valueOf( Constants.Server.SERVER_HTTP_PORT ) );
            return port;
        } catch (Exception e) {
            e.printStackTrace();
            log.error( String.format( "cant't get server port[%s] from conf file.", Constants.Server.SERVER_HTTP_PORT ) );
            System.exit( -1 );
        }
        return -1;
    }

    /**
     * Copied from ExceptionUtils since that class is not visible during start
     */
    private static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath) t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError) t;
        }
    }

}
