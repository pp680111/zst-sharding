package com.zst.sharding.engine;

import com.alibaba.fastjson2.JSON;
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

        String sql2 = "insert into t_u(id, name) values (?, ?)";
        Object[] args2 = new Object[]{1, "zst"};

        ShardingResult result2 = standardShardingEngine.sharding(sql2, args2);
        System.out.println(JSON.toJSONString(result2));

        String sql3 = "insert into t_order(id, uid) values (?, ?)";
        Object[] args3 = new Object[]{1, 2};

        ShardingResult result3 = standardShardingEngine.sharding(sql3, args3);
        System.out.println(JSON.toJSONString(result3));
    }
}
