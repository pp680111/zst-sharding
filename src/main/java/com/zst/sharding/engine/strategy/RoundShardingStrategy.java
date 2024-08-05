package com.zst.sharding.engine.strategy;

import java.util.Properties;

public class RoundShardingStrategy implements ShardingStrategy {
    private Properties properties;

    public RoundShardingStrategy(Properties properties) {
        this.properties = properties;
    }
}
