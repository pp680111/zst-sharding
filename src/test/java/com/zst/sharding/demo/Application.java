package com.zst.sharding.demo;

import com.zst.sharding.config.ShardingConfiguration;
import com.zst.sharding.mybatis.plugins.ShardingMapperFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ShardingConfiguration.class)
@MapperScan(basePackages = "com.zst.sharding.demo.module", factoryBean = ShardingMapperFactoryBean.class)
public class Application {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(Application.class, args);
    }
}
