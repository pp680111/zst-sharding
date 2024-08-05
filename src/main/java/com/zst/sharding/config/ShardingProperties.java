package com.zst.sharding.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Setter
@Getter
@ConfigurationProperties(prefix = "zst.sharding")
public class ShardingProperties {
    Map<String, DataSource> datasources = new LinkedHashMap<>();
    Map<String, TableProperties> tables = new LinkedHashMap<>();

    @Getter
    @Setter
    public static class DataSource {
        private String url;
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class TableProperties {
        private List<String> actualDataNodes;
        private Properties databaseStrategy;
        private Properties tableStrategy;

    }

}
