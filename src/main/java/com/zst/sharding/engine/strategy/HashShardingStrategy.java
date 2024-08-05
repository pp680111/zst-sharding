package com.zst.sharding.engine.strategy;

import java.util.Properties;

public class HashShardingStrategy implements ShardingStrategy {
    private Properties properties;

    public HashShardingStrategy(Properties properties) {
        this.properties = properties;
    }
}
