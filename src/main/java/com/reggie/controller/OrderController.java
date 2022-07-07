package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.entity.Orders;
import com.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order,HttpSession session){

        orderService.submit(order);
        return R.success("下单成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        log.info("page = {},pageSize = {}",page,pageSize);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询
        orderService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize, HttpSession session){
        Long user = (Long) session.getAttribute("user");
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.eq(user != null,Orders::getUserId,user);
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询
        orderService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);

    }

}
