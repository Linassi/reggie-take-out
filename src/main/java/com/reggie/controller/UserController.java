package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.R;
import com.reggie.entity.User;
import com.reggie.service.UserService;
import com.reggie.utils.EmailUtil;
import com.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailUtil emailService;

    /**
     *
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> send(@RequestBody User user, HttpSession session){

        //获取接收邮箱
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //生成随机验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            //调用发送邮件工具类完成发送邮件
            //emailService.sendSimpleMail(phone,"【瑞吉外卖】验证信息","您的验证码为:" + code + ",请注意保密!");
            log.info("验证码：{}",code);
            //需要将生成的验证码保存到Session
            session.setAttribute(phone,code);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        Object codeInSession = session.getAttribute(phone);

        if (codeInSession != null && codeInSession.equals(code)){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败！");
    }

}
