package com.lemi.game.util.cache;

import java.util.List;

import com.lemi.idler.server.Constants;
import com.lemi.idler.server.ResourceManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisClientSingel implements JedisClient {
    private final static String HOST           = ResourceManager.instance().valueOf( Constants.Redis.HOST );
    private final static String REDIS_PASSWORD = ResourceManager.instance().valueOf( Constants.Redis.PASSEORD );
    private final static int    REDIS_DB_INDEX = Integer.parseInt( ResourceManager.instance().valueOf( Constants.Redis.DB_INDEX ) );
    private static JedisPool    jedisPool;

    {
        jedisPool = new JedisPool( HOST );
    }

    private JedisClientSingel() {
    }

    public static JedisClientSingel instance() {
        return Nested.INSTANCE;
    }

    static class Nested {
        private static final JedisClientSingel INSTANCE = new JedisClientSingel();
    }

    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        jedis.auth( REDIS_PASSWORD );
        jedis.select( REDIS_DB_INDEX );
        String string = jedis.get( key );
        jedis.close();
        return string;
    }

    public String set(String key,
                      String value) {
        Jedis jedis = jedisPool.getResource();
        jedis.auth( REDIS_PASSWORD );
        jedis.select( REDIS_DB_INDEX );
        String res = jedis.set( key, value );
        jedis.close();
        return res;
    }

    public Long hset(String key,
                     String field,
                     String value) {
        Jedis jedis = jedisPool.getResource();
        jedis.auth( REDIS_PASSWORD );
        jedis.select( REDIS_DB_INDEX );
        Long res = jedis.hset( key, field, value );
        jedis.close();
        return res;
    }

    public String hget(String key,
                       String field) {
        Jedis jedis = jedisPool.getResource();
        jedis.auth( REDIS_PASSWORD );
        jedis.select( REDIS_DB_INDEX );
        jedis.select( REDIS_DB_INDEX );
        String res = jedis.hget( key, field );
        jedis.close();
        return res;
    }

    public Long incr(String key) {
        Jedis jedis = jedisPool.getResource();
        jedis.auth( REDIS_PASSWORD );
        jedis.select( REDIS_DB_INDEX );
        Long res = jedis.incr( key );
        jedis.close();
        return res;
    }

    public Long expire(String key,
                       int seconds) {
        Jedis jedis = jedisPool.getResource();
        jedis.auth( REDIS_PASSWORD );
        jedis.select( REDIS_DB_INDEX );
        Long res = jedis.expire( key, seconds );
        jedis.close();
        return res;
    }

    public Long ttl(String key) {
        Jedis jedis = jedisPool.getResource();
        jedis.auth( REDIS_PASSWORD );
        jedis.select( REDIS_DB_INDEX );
        Long res = jedis.ttl( key );
        jedis.close();
        return res;
    }

    public Long lpush(String key,
                      String... strings) {
        Jedis jedis = jedisPool.getResource();
        jedis.auth( REDIS_PASSWORD );
        jedis.select( REDIS_DB_INDEX );
        Long res = jedis.lpush( key, strings );
        jedis.close();
        return res;
    }

    public String rpop(String key) {
        Jedis jedis = jedisPool.getResource();
        jedis.auth( REDIS_PASSWORD );
        jedis.select( REDIS_DB_INDEX );
        String res = jedis.rpop( key );
        jedis.close();
        return res;
    }

    public String rpoplpush(String key,
                            String destkey) {
        Jedis jedis = jedisPool.getResource();
        jedis.auth( REDIS_PASSWORD );
        jedis.select( REDIS_DB_INDEX );
        String res = jedis.rpoplpush( key, destkey );
        jedis.close();
        return res;
    }

    public Long llen(String key) {
        Jedis jedis = jedisPool.getResource();
        jedis.auth( REDIS_PASSWORD );
        jedis.select( REDIS_DB_INDEX );
        Long res = jedis.llen( key );
        jedis.close();
        return res;
    }

    public List<String> lrange(String key,
                               int start,
                               int end) {
        Jedis jedis = jedisPool.getResource();
        jedis.auth( REDIS_PASSWORD );
        jedis.select( REDIS_DB_INDEX );
        List<String> list = jedis.lrange( key, start, end );
        jedis.close();
        return list;
    }

    public Long hdel(String key,
                     String... fields) {
        Jedis jedis = jedisPool.getResource();
        jedis.auth( REDIS_PASSWORD );
        jedis.select( REDIS_DB_INDEX );
        Long result = jedis.hdel( key, fields );
        jedis.close();
        return result;
    }

    public Long hincrBy(String key,
                        String field,
                        Long value) {
        Jedis jedis = jedisPool.getResource();
        jedis.auth( REDIS_PASSWORD );
        jedis.select( REDIS_DB_INDEX );
        Long res = jedis.hincrBy( key, field, value );
        jedis.close();
        return res;
    }

}
