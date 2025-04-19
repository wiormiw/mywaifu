package com.silvermaiden.mywaifu.configurations.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.silvermaiden.mywaifu.common.constants.SecurityConstant.REFRESH_EXPIRATION_TIME;

@Service
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

//    public void storeRefreshToken(String token, String username) {
//        redisTemplate.opsForValue().set("refresh:" + token, username, REFRESH_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
//        redisTemplate.opsForSet().add("user_refresh_tokens:" + username, token);
//    }
//
//    public String getUsernameFromRefreshToken(String token) {
//        return redisTemplate.opsForValue().get("refresh:" + token);
//    }
//
//    public boolean isTokenValid(String token) {
//        return redisTemplate.hasKey("refresh:" + token);
//    }
//
//    public void invalidateRefreshToken(String token) {
//        redisTemplate.delete("access:" + token);
//    }
//
//    public void invalidateRefreshTokenFromUsername(String username) {
//        Set<String> tokens = redisTemplate.opsForSet().members("user_refresh_tokens:" + username);
//
//        if (tokens != null) {
//            for (String token : tokens) {
//                redisTemplate.delete("refresh:" + token);
//            }
//
//            redisTemplate.delete("user_refresh_tokens:" + username);
//        }
//    }
}
