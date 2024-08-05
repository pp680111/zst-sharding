package com.zst.sharding.engine;

import com.zst.sharding.config.ShardingProperties;
import com.zst.sharding.engine.strategy.ShardingStrategy;
import com.zst.sharding.engine.strategy.ShardingStrategyFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个是每个需要分表的schema的独享的吗？还是所有的schema共享的
 */
public class StandardShardingEngine implements ShardingEngine {
    private ShardingProperties properties;
    /**
     * schema名称与这个schema内有多少要使用到的table名称的map，比如ds0->[t_user_0,t_user_1,t_item_0,t_item_1],ds1->[t_user_0,t_user_1,t_item_0,t_item_1]
     */
    private final MultiValueMap<String, String> actualDatabaseNames = new LinkedMultiValueMap<>();
    /**
     * table名称
     */
    private final MultiValueMap<String, String> actualTableNames = new LinkedMultiValueMap<>();
    private final Map<String, ShardingStrategy> databaseStrategy = new HashMap<>();
    private final Map<String, ShardingStrategy> tableStrategy = new HashMap<>();
    public StandardShardingEngine(ShardingProperties properties) {
        this.properties = properties;
        init();
    }

    @Override
    public ShardingResult sharding(String sql, Object[] args) {
        return null;
    }

    private void init() {
        Map<String, ShardingProperties.TableProperties> tables = properties.getTables();
        if (tables == null || tables.isEmpty()) {
            return;
        }

        tables.forEach((tableName, tableProperties) -> {
            tableProperties.getActualDataNodes().forEach(actualDataNode -> {
                // actualDataNode的值格式为ds0.t_user_00
                String[] split = actualDataNode.split("\\.");
                // 这里这两个map添加进去的值没看懂
                actualDatabaseNames.add(split[0], split[1]);
                actualTableNames.add(split[1], split[0]);
            });

            databaseStrategy.put(tableName, ShardingStrategyFactory.getShardingStrategy(tableProperties.getDatabaseStrategy()));
            tableStrategy.put(tableName, ShardingStrategyFactory.getShardingStrategy(tableProperties.getTableStrategy()));
        });


    }
}
