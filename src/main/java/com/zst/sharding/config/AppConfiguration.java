package com.zst.sharding.config;

import com.zst.sharding.datasource.ShardingDataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(ShardingProperties.class)
public class AppConfiguration {
    @Bean
    public DataSource shardingDataSource(ShardingProperties properties) {
        return new ShardingDataSource(properties);
    }
}
