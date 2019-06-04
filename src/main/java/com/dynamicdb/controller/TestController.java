package com.dynamicdb.controller;

import com.dynamicdb.aop.MyDataSource;
import com.dynamicdb.mapper.TestUserMapper;
import com.dynamicdb.pojo.TestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("test1")
    public List<TestUser> test1() {
        List<TestUser> all = userMapper.findAll();
        System.out.println(all);

        userMapper.insert(new TestUser("2", "2"));

        List<TestUser> all1 = userMapper.findAll();
        System.out.println(all1);


        TestUser testUser = new TestUser();
        testUser.setId("2");
        testUser.setName(null);
        userMapper.update(testUser);

        List<TestUser> all3 = userMapper.findAll();
        System.out.println(all3);

        return userMapper.findAll();
    }

    @RequestMapping("test2")
    @MyDataSource(name = "slave")
    public List<TestUser> test2() {
        return null;
    }

}
