package com.dynamicdb.mapper;

import com.dynamicdb.pojo.TestUser;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author whh
 */
@Component
public interface TestUserMapper {

    List<TestUser> findAll();

    void update(TestUser testUser);

    void insert(TestUser testUser);
}
