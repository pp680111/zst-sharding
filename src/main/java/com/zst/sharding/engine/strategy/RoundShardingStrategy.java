package com.zst.sharding.engine.strategy;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class RoundShardingStrategy implements ShardingStrategy {
    private Properties properties;

    public RoundShardingStrategy(Properties properties) {
        this.properties = properties;
    }

    @Override
    public List<String> getShardingColumns() {
        return null;
    }

    @Override
    public String doSharding(List<String> targetItems, String sourceItem, Map<String, Object> sqlParams) {
        return null;
    }
}
