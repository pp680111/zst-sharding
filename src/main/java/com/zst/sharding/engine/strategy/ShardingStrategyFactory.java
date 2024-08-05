package com.zst.sharding.engine.strategy;

import java.util.Properties;

public class ShardingStrategyFactory {
    public static ShardingStrategy getShardingStrategy(Properties properties) {
        String strategy = properties.getProperty("type");
        if (strategy == null) {
            return null;
        }

        if (strategy.equalsIgnoreCase("hash")) {
            return new HashShardingStrategy(properties);
        } else if (strategy.equalsIgnoreCase("round")) {
            return new RoundShardingStrategy(properties);
        }
        return null;
    }
}
