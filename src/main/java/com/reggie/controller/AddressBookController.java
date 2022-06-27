package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.R;
import com.reggie.entity.AddressBook;
import com.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook, HttpSession session){

        addressBook.setUserId((Long) session.getAttribute("user"));
        addressBookService.save(addressBook);
        return R.success("地址保存成功");
    }
    @GetMapping("list")
    public R<List<AddressBook>> list(HttpSession session){

        Long userId = (Long)session.getAttribute("user");

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AddressBook::getUserId,userId);

        List<AddressBook> addressBooks = addressBookService.list(queryWrapper);

        return R.success(addressBooks);

    }
    @PutMapping("/default")
    public R<String> setDefaultAddress(@RequestBody AddressBook addressBook,HttpSession session){
        //获取当前用户Session中用户id属性值
        Long user = (Long) session.getAttribute("user");
        //查询当前用户拥有的所有地址
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,user);
        //将当前用户的所有地址放到List中
        List<AddressBook> addressBooks = addressBookService.list(queryWrapper);
        //将当前用户所有其他地址的默认值全部设为0，传入id默认值设为1
        addressBooks.stream().map((item) ->{
            if(!(item.getId().equals(addressBook.getId())))
                item.setIsDefault(0);
            else
                item.setIsDefault(1);
            return item;
        }).collect(Collectors.toList());

        addressBookService.updateBatchById(addressBooks);

        return R.success("success");
    }

}
