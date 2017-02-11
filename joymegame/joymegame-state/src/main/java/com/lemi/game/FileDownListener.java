package com.lemi.game;

import com.lemi.idler.server.RequestHeadersWapper;

public interface FileDownListener {
    void click(String url,
               RequestHeadersWapper headers);

    void cancel(String url,
                RequestHeadersWapper headers);

    void downloadSuccess(String url,
                         RequestHeadersWapper headers);
}
