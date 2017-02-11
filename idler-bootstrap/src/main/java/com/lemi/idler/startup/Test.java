package com.lemi.idler.startup;

import java.security.NoSuchAlgorithmException;

import com.lemi.game.util.security.SecurityFactory;
import com.lemi.idler.server.Constants;
import com.lemi.idler.server.ResourceManager;

public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        final String key = ResourceManager.instance().valueOf( Constants.Server.SERVER_URL_AES_KEY );
        final String decryptUrl = SecurityFactory.AES.encrypt( "20171011koZUb1tb100063", key );
        System.out.println( decryptUrl );
        final String de = SecurityFactory.AES.decrypt( "20600ebc94e37da6e5e927f928c5edd0cb592c8a46d90b11ccb2dc091db9ee79", key );
        System.out.println( de );
    }
}
