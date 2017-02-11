package com.lemi.game.decoder;

import java.io.File;

import com.lemi.game.UrlInfo;
import com.lemi.game.util.security.SecurityFactory;
import com.lemi.idler.server.Constants;
import com.lemi.idler.server.ResourceManager;

public class AESUrlDecoder implements UrlDecoder<UrlInfo> {
    /** 20171011koZUb1tb100063 */
    private final static int URL_DATA_LENGTH = 22;

    public UrlInfo decoder(String uri) throws Exception {
        if (uri.length() <= 1) {
            throw new IllegalArgumentException( "error request url:" + uri );
        }
        String extension = uri.substring( uri.lastIndexOf( "." ) );
        uri = uri.substring( 1, uri.lastIndexOf( "." ) );
        final String key = ResourceManager.instance().valueOf( Constants.Server.SERVER_URL_AES_KEY );
        final String decryptUrl = SecurityFactory.AES.decrypt( uri, key );
        if (decryptUrl.length() != URL_DATA_LENGTH) {
            throw new IllegalArgumentException( "error request url:" + decryptUrl );
        }

        return installUriInfo( decryptUrl, extension );
    }

    private static UrlInfo installUriInfo(String decryptUrl,
                                          String extension) {
        UrlInfo ui = new UrlInfo();
        ui.setUrlPath( new StringBuilder( File.separator ).append( decryptUrl.substring( 0, 4 ) ).append( File.separator )
                .append( decryptUrl.substring( 4, 6 ) ).append( File.separator ).append( decryptUrl.substring( 6, 8 ) ).append( File.separator )
                .append( decryptUrl.substring( 8, 16 ) ).append( extension ).toString() );
        ui.setAppKey( decryptUrl.substring( 8, 16 ) );
        ui.setDistro( decryptUrl.substring( 16 ) );
        return ui;
    }

}
