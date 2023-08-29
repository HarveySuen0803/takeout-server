package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/addressBook")
public class AddressBookController {
    @Autowired
    AddressBookService addressBookService;

    @PostMapping
    public Result<String> insert(@RequestBody AddressBook addressBook) {
        log.info("insert AddressBook, param is {}", addressBook);
        addressBookService.insert(addressBook);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<AddressBook> selectById(@PathVariable Long id) {
        log.info("select AddressBook by id, param is {}", id);
        AddressBook addressBook = addressBookService.selectById(id);
        return Result.success(addressBook);
    }

    @GetMapping("/list")
    public Result<List<AddressBook>> selectByUserid() {
        log.info("select AddressBook by userId, param is userId={}", BaseContext.getCurrentId());
        List<AddressBook> addressBookList = addressBookService.selectByUserId();
        return Result.success(addressBookList);
    }

    @GetMapping("/default")
    public Result<AddressBook> selectDefaultByUserId() {
        log.info("select default AddressBook by userId, param is userId={}", BaseContext.getCurrentId());
        AddressBook addressBook = addressBookService.selectDefaultByUserId();

        if (addressBook == null) {
            return Result.error("cant not find the default address");
        }

        return Result.success(addressBook);
    }

    @PutMapping
    public Result<String> updateById(@RequestBody AddressBook addressBook) {
        log.info("update AddressBook by id, param is {}", addressBook);
        addressBookService.updateById(addressBook);
        return Result.success();
    }

    @PutMapping("/default")
    public Result<String> updateDefaultByid(Long id) {
        log.info("update AddressBook by id, param is id={}", id);
        addressBookService.updateDefaultById(id);
        return Result.success();
    }

    @DeleteMapping
    public Result<String> deleteById(Long id) {
        log.info("delete AddressBook by id, param is id={}", id);
        addressBookService.deleteById(id);
        return Result.success();
    }
}
