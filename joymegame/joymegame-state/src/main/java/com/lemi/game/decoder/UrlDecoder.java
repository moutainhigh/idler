package com.lemi.game.decoder;

public interface UrlDecoder<T> {
    T decoder(String uri) throws Exception;
}
