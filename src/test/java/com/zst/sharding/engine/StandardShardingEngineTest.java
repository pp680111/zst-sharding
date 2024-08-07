package com.zst.sharding.engine;

import com.alibaba.fastjson2.JSON;
import com.zst.sharding.config.ShardingProperties;
import com.zst.sharding.demo.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
public class StandardShardingEngineTest {
    @Autowired
    private StandardShardingEngine standardShardingEngine;

    @Test
    public void shardingTest_all() {
        String sql = "insert into t_user(id, name) values (?, ?)";
        Object[] args = new Object[]{1, "zst"};

        ShardingResult result = standardShardingEngine.sharding(sql, args);
        System.out.println(JSON.toJSONString(result));
    }
}
