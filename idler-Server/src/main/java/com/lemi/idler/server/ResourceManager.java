package com.lemi.idler.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ResourceManager {
    private static final Logger log                              = Logger.getLogger( ResourceManager.class );

    /** conf files directory name */
    private final static String CONF_DIR_NAME                    = "/conf";

    /** idler server conf file name */
    private final static String IDLER_SERVER_CONF_FILENAME       = "server.conf";

    /**
     * the prefix of conf file(server.properties) each property key name.is
     * would be append before it's origin key name.then eache conf value add to
     * system properties
     */
    private final static String IDLER_SERVER_PROPERTY_KEY_PREFIX = "idler.server.";

    private ResourceManager() {
        loadConf();
    }

    /** load user conf into system */
    private void loadConf() {
        Properties props = loadProperties( IDLER_SERVER_CONF_FILENAME );
        for (Object key : props.keySet()) {
            System.getProperties().put( IDLER_SERVER_PROPERTY_KEY_PREFIX + key, props.get( key ) );
        }
    }

    /** get conf conf value by property key name */
    public String valueOf(String keyName) {
        return System.getProperties().getProperty( IDLER_SERVER_PROPERTY_KEY_PREFIX + keyName );
    }

    public static ResourceManager instance() {
        return Nested.INSTANCE;
    }

    /** nestd class for Singleton instance */
    private static class Nested {
        private final static ResourceManager INSTANCE = new ResourceManager();
    }

    /** load conf file into Properties through file name */
    private Properties loadProperties(String propertiesFileName) {
        final String confFile = getConfPath() + File.separator + propertiesFileName;

        if (log.isDebugEnabled()) {
            log.debug( String.format( "load server configuration: %s", confFile ) );
        }

        Properties props = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream( new File( confFile ) );
            props.load( in );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return props;
    }

    /** get server conf files path */
    private String getConfPath() {
        final String userDir = System.getProperty( "user.dir" );
        return userDir.substring( 0, userDir.lastIndexOf( "/" ) ) + CONF_DIR_NAME;
    }

    public String confPath() {
        return getConfPath();
    }
}
