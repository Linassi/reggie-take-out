package com.reggie.service.impl;

import com.reggie.utils.EmailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailUtilTest {
    @Autowired
    private EmailUtil emailService;
    @Test
    public void sendMailTest(){
        //emailService.sendSimpleMail("huang_wujian@126.com","测试邮件",new Date().toString());
        emailService.sendHtmlMail("huang_wujian@126.com","HTML_Test",
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>Page Title</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "<h1>This is a Heading</h1>\n" +
            "<p>This is a paragraph.</p>\n" +
            "\n" +
            "</body>\n" +
            "</html>");
    }
}