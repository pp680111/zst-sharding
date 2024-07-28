package com.zst.sharding;

import com.zst.sharding.module.user.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Tests {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void tr() {
        userMapper.selectById(1L);
    }
}
