package com.reggie.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


@SpringBootTest
class CommonControllerTest {

    @Value("${reggie.path}")
    private String basePath;

    @Test
    void testBasePath(){
        System.out.println(basePath);
        String file =  "d://abc.txt";
        //获取原始文件名的后缀
        String suffix = file.substring(file.lastIndexOf("."));
        System.out.println(suffix);

    }
}