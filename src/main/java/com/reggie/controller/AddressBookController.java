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

    @GetMapping("{id}")
    public R<AddressBook> get(@PathVariable Long id){

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getId,id);
        AddressBook address = addressBookService.getOne(queryWrapper);
        return R.success(address);

    }
    @GetMapping("/default")
    public R<AddressBook> getDefault(HttpSession session){
        Long userId = (Long)session.getAttribute("user");
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null,AddressBook::getUserId,userId);
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook address = addressBookService.getOne(queryWrapper);

        return R.success(address);
    }

    @DeleteMapping()
    public R<String> delete(Long ids){

        Boolean removed = addressBookService.removeById(ids);
        if (removed)
            return R.success("删除成功");
        else
            return R.error("删除失败");
    }

    @PutMapping()
    public R<String> update(@RequestBody AddressBook addressBook){
        Boolean updateSuccess = addressBookService.updateById(addressBook);
        if (updateSuccess)
            return R.success("更新成功");
        else
            return R.error("更新失败");
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
        //将当前用户所有其他地址的默认值全部设为0，传入id对应地址默认值设为1
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
