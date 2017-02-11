package com.lemi.game.util.security;

import java.security.NoSuchAlgorithmException;

public enum SecurityFactory {
                             MD5(new Security() {

                                 public String encrypt(String input,
                                                       String key) {
                                     try {
                                         return Digest.hexMd5( input );
                                     } catch (NoSuchAlgorithmException e) {
                                     }
                                     return null;
                                 }

                                 public String decrypt(String input,
                                                       String key) throws NoSuchAlgorithmException {
                                     throw new NoSuchAlgorithmException( "MD5 cannot be decryped" );
                                 }

                             }),

                             AES(new Security() {

                                 public String encrypt(String input,
                                                       String key) {
                                     return Digest.parseByte2HexStr( Digest.aesEncrypt( input, key ) ).toLowerCase();
                                 }

                                 public String decrypt(String input,
                                                       String key) throws NoSuchAlgorithmException {
                                     return Digest.aesDecrypt( Digest.parseHexStr2Byte( input.toUpperCase() ), key );
                                 }

                             });

    private Security security;

    private SecurityFactory(Security security) {
        this.security = security;
    }

    public String encrypt(String input,
                          String key) {
        return security.encrypt( input, key );
    }

    public String decrypt(String input,
                          String key) throws NoSuchAlgorithmException {
        return security.decrypt( input, key );
    }
}
