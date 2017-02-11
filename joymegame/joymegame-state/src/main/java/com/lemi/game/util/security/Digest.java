package com.lemi.game.util.security;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Digest {
    public static String hexMd5(String input) throws NoSuchAlgorithmException {
        return hexDigest( input, "MD5" );
    }

    public static String hexSha1(String input) throws NoSuchAlgorithmException {
        return hexDigest( input, "SHA-1" );
    }

    public static String hexDigest(String input,
                                   String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance( algorithm );
        byte[] utf8byte = input.getBytes( Charset.forName( "UTF-8" ) );
        byte[] result = digest.digest( utf8byte );
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append( Integer.toString( (result[ i ] & 0xff) + 0x100, 16 ).substring( 1 ) );
        }
        return sb.toString();
    }

    public static byte[] aesEncrypt(String content,
                                    String password) {
        if (content == null || password == null)
            return null;
        try {
            Cipher cipher = Cipher.getInstance( "AES/ECB/PKCS5Padding" );
            cipher.init( Cipher.ENCRYPT_MODE, new SecretKeySpec( password.getBytes(), "AES" ) );
            return cipher.doFinal( content.getBytes( "UTF-8" ) );
        } catch (Exception e) {
            return null;
        }
    }

    public static String aesDecrypt(byte[] content,
                                    String password) {
        if (content == null || password == null)
            return null;
        try {
            Cipher cipher = Cipher.getInstance( "AES/ECB/PKCS5Padding" );
            cipher.init( Cipher.DECRYPT_MODE, new SecretKeySpec( password.getBytes(), "AES" ) );
            byte[] bytes = cipher.doFinal( content );
            return new String( bytes, "UTF-8" );
        } catch (Exception e) {
            return null;
        }
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString( buf[ i ] & 0xFF );
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append( hex.toUpperCase() );
        }
        return sb.toString();
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt( hexStr.substring( i * 2, i * 2 + 1 ), 16 );
            int low = Integer.parseInt( hexStr.substring( i * 2 + 1, i * 2 + 2 ), 16 );
            result[ i ] = (byte) (high * 16 + low);
        }
        return result;
    }

}
