package com.zst.sharding.datasource;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.fastjson2.JSON;
import com.zst.sharding.config.ShardingProperties;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class ShardingDataSource extends AbstractRoutingDataSource {
    private ShardingProperties properties;

    public ShardingDataSource(ShardingProperties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("properties is null");
        }

        this.properties = properties;
    }

    /**
     * 用来计算数据源的key的方法，返回值将会作为key去targetDataSourcesMap中获取对应的datasource
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        ShardingResult result = ShardingContext.get();
        return result == null ? null : result.getTargetDataSource();
    }

    private void prepareDataSource() {
        Map<Object, Object> dsMap = new LinkedHashMap<>();

        Map<String, ShardingProperties.DataSource> dsProperties = properties.getDatasources();
        dsProperties.forEach((name, dsProp) -> {
            Properties prop = new Properties();
            prop.setProperty("url", dsProp.getUrl());
            prop.setProperty("username", dsProp.getUsername());
            prop.setProperty("password", dsProp.getPassword());
            // TODO 补一个检查配置项正确性的逻辑
            try {
                DataSource dataSource = DruidDataSourceFactory.createDataSource(prop);
                dsMap.put(name, dataSource);
            } catch (Exception e) {
                throw new RuntimeException("create data source error, properties=" + JSON.toJSONString(dsProp));
            }
        });

        if (dsMap.isEmpty()) {
            throw new RuntimeException("no avaliable dataSource");
        }

        setTargetDataSources(dsMap);
        setDefaultTargetDataSource(dsMap.values().iterator().next());
    }
}
