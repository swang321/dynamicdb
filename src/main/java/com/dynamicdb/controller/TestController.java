package com.dynamicdb.controller;

import com.dynamicdb.aop.MyDataSource;
import com.dynamicdb.db.DynamicDbContextHolder;
import com.dynamicdb.mapper.TestUserMapper;
import com.dynamicdb.pojo.TestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author whh
 */
@RestController
public class TestController {

    @Autowired
    private TestUserMapper userMapper;

    @RequestMapping("test1")
    @MyDataSource(name = "slave")
    public List<TestUser> test1() {
        List<TestUser> all = userMapper.findAll();
        String type = DynamicDbContextHolder.getDbSourceType();
        System.out.println(type);
        return all;
    }

    @RequestMapping("test2")
    public List<TestUser> test2() {
        String type = DynamicDbContextHolder.getDbSourceType();
        System.out.println(type);
        return userMapper.findAll();
    }

}
