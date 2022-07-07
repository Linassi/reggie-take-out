package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.entity.ShoppingCart;
import com.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     *
     * @param session
     * @return
     */
    @GetMapping("list")
    public R<List<ShoppingCart>> list(HttpSession session){
        Long user = (Long) session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,user);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        return R.success(shoppingCartList);
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart,HttpSession session){
        //获取当前用户id
        Long user = (Long) session.getAttribute("user");
        //传如的购物信息需包含当前用户信息
        shoppingCart.setUserId(user);

        //查询条件
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //如果添加的是菜品，设置查询条件
        if (shoppingCart.getDishId() != null && shoppingCart.getSetmealId() == null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            queryWrapper.eq(ShoppingCart::getUserId,user);
        //如果添加的是套餐，设置查询条件
        }else if (shoppingCart.getDishId() == null && shoppingCart.getSetmealId() != null){
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            queryWrapper.eq(ShoppingCart::getUserId,user);
        }
        //数据库中查询该用户的购物车中该套餐或菜品信息
        ShoppingCart shoppingCartInSQL = shoppingCartService.getOne(queryWrapper);
        if (shoppingCartInSQL != null && shoppingCartInSQL.getNumber() > 0){
            shoppingCartInSQL.setNumber(shoppingCartInSQL.getNumber() + 1);
            shoppingCartService.updateById(shoppingCartInSQL);
        }else {
            shoppingCartService.save(shoppingCart);
        }

        return R.success("插入成功");
    }

    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart,HttpSession session){
        //获取当前用户id
        Long user = (Long) session.getAttribute("user");
        //传入的购物信息需包含当前用户信息
        shoppingCart.setUserId(user);

        //查询条件
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //如果添加的是菜品，设置查询条件
        if (shoppingCart.getDishId() != null && shoppingCart.getSetmealId() == null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            queryWrapper.eq(ShoppingCart::getUserId,user);
        //如果添加的是套餐，设置查询条件
        }else if (shoppingCart.getDishId() == null && shoppingCart.getSetmealId() != null){
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            queryWrapper.eq(ShoppingCart::getUserId,user);
        }

        //数据库中查询该用户的购物车中该套餐或菜品信息
        ShoppingCart shoppingCartInSQL = shoppingCartService.getOne(queryWrapper);
        if (shoppingCartInSQL != null && shoppingCartInSQL.getNumber() == 1){
            shoppingCartService.removeById(shoppingCartInSQL.getId());
        }else if (shoppingCartInSQL != null && shoppingCartInSQL.getNumber() > 1){
            shoppingCartInSQL.setNumber(shoppingCartInSQL.getNumber() - 1);
            shoppingCartService.updateById(shoppingCartInSQL);
        }else{
            return R.error("数量错误");
        }

        return R.success("成功！");
    }

    @DeleteMapping("/clean")
    public R<String> remove(HttpSession session){
        Long user = (Long) session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,user);
        shoppingCartService.remove(queryWrapper);
        return R.success("删除成功");
    }
}
