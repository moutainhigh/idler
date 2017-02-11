package com.lemi.game.util.cache;

import java.util.List;

public interface JedisClient {
    String get(String key);

    String set(String key,
               String value);

    Long hset(String key,
              String field,
              String value);

    String hget(String key,
                String field);

    Long incr(String key);

    Long expire(String key,
                int seconds);

    Long ttl(String key);

    Long lpush(String key,
               String... strings);

    String rpop(String key);

    String rpoplpush(String key,
                     String destkey);

    Long llen(String key);

    Long hdel(String key,
              String... fields);

    public List<String> lrange(String key,
                               int start,
                               int end);

    public Long hincrBy(String key,
                        String filed,
                        Long value);
}
