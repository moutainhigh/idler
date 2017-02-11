package com.lemi.idler.server;

public final class Constants {

    public static class Server {
        public static final String SERVER_HTTP_PORT         = "idler.work.http.port";
        public static final String SERVER_FILE_ROOT         = "idler.file.root";
        public static final String SERVER_BUSSINESS_HANLDER = "idler.bussiness.hanlder";
        public static final String SERVER_URL_AES_KEY       = "idler.bussiness.url.aes.key";
    }

    public static class Redis {
        public static final String HOST     = "redis.host";
        public static final String PASSEORD = "redis.password";
        public static final String DB_INDEX = "redis.db.index";
    }

    public static class IpLimited {
        public static final String IP_LIMITED            = "game.limited.ip";
        public static final String IP_LIMITED_TIMES      = "game.limited.times";
        public static final String IP_LIMITED_FREQ_TIME  = "game.limited.freq.time";
        /** IP blacklist key in redis */
        public static final String IP_LIMITED_FREQ_COUNT = "game.limited.freq.count";
    }

    public static class GameState {
        public static final String DOWN_DATA               = "game.app.down.data";

        public static final String STATE_DISTRO_KEY        = "distro";
        public static final String STATE_URI_KEY           = "down_uri";
        public static final String STATE_APPKEY_KEY        = "appkey";
        public static final String HEADER_USER_AGENT_KEY   = "User-Agent";
        public static final String HEADER_USER_REFERER_KEY = "Referer";
    }
}
