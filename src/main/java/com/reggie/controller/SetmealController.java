package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.dto.SetmealDto;
import com.reggie.entity.*;
import com.reggie.service.CategoryService;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {},pageSize = {}",page,pageSize);
        //构造分页构造器
        Page<Setmeal> pageInfo = new Page(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        setmealService.page(pageInfo,queryWrapper);
        //复制属性
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        //设置page内容为设置了套餐类型名称的records
        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);
    }

    @PostMapping
    @CacheEvict(value = "setMealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        //log.info("套餐信息:{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("保存成功！");
    }

    @DeleteMapping
    @CacheEvict(value = "setMealCache",allEntries = true)
    public R<String> remove(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("success");
    }

    @PostMapping("/status/{status}")
    @CacheEvict(value = "setMealCache",allEntries = true)
    public R<String> changeStatus(@PathVariable Integer status,@RequestParam List<Long> ids){
        setmealService.changeStatus(status,ids);
        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    @Transactional
    public R<SetmealDto> get(@PathVariable Long id){
        //获取套餐基本属性
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        //将套餐基本属性复制到DTO中
        BeanUtils.copyProperties(setmeal,setmealDto);
        //条件构造器
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,id);
        //获取套餐菜品关系表信息
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);

        //获取分类名称信息
        Category category = categoryService.getById(setmealDto.getCategoryId());
        setmealDto.setCategoryName(category.getName());

        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        return null;
    }

    @GetMapping("/list")
    @Cacheable(value = "setMealCache",key = "#setmeal.getCategoryId()+'_'+ #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime).orderByDesc(Setmeal::getName);
        queryWrapper.eq(Setmeal::getStatus,1);

        List<Setmeal> setmeals = setmealService.list(queryWrapper);
        return R.success(setmeals);
    }

}
