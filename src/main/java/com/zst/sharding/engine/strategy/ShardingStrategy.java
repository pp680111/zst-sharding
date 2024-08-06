package com.zst.sharding.engine.strategy;

import java.util.List;
import java.util.Map;

public interface ShardingStrategy {
    List<String> getShardingColumns();

    /**
     * 执行分片策略
     * @param targetItems 待选择的分片item，分片结果将从这些item中产生（比如t_use_00|01|02)
     * @param sourceItem 分片的原始item（比图t_user)
     * @param sqlParams sql参数
     * @return
     */
    String doSharding(List<String> targetItems, String sourceItem, Map<String, Object> sqlParams);
}
