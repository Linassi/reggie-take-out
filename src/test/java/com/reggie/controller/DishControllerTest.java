package com.reggie.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;


@SpringBootTest
class DishControllerTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void setStringTest(){
        redisTemplate.opsForValue().set("distrit","guangming");
    }

    @Test
    public void getStringTest(){
        System.out.println(redisTemplate.opsForValue().get("distrit").toString());

    }

    @Test
    public void setCodeTest(){
        redisTemplate.opsForValue().set("code","5512",60L, TimeUnit.SECONDS);
    }

}