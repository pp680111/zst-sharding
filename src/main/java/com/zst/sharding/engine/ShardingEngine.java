package com.zst.sharding.engine;

public interface ShardingEngine {
    /**
     * 执行分库分表策略，根据sql和参数值计算最终sql实际执行的数据节点
     * @param sql
     * @param args
     * @return
     */
    ShardingResult sharding(String sql, Object[] args);
}
