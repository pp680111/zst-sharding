package com.zst.sharding.config;

import com.zst.sharding.datasource.ShardingDataSource;
import com.zst.sharding.engine.ShardingEngine;
import com.zst.sharding.engine.StandardShardingEngine;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(ShardingProperties.class)
public class ShardingConfiguration {
    @Bean
    public DataSource shardingDataSource(ShardingProperties properties) {
        return new ShardingDataSource(properties);
    }

    @Bean
    public ShardingEngine shardingEngine(ShardingProperties properties) {
        return new StandardShardingEngine(properties);
    }

}
