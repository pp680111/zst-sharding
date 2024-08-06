package com.zst.sharding.engine.strategy;

import com.zst.sharding.engine.InlineExpressionParser;
import groovy.lang.Closure;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class HashShardingStrategy implements ShardingStrategy {
    private String shardingColumnName;
    private String algorithmExpression;

    public HashShardingStrategy(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("properties is null");
        }
        if (!StringUtils.hasLength(properties.getProperty("shardingColumn"))) {
            throw new IllegalArgumentException("shardingColumn is null");
        }
        if (!StringUtils.hasLength(properties.getProperty("algorithmExpression"))) {
            throw new IllegalArgumentException("algorithmExpression is null");
        }

        this.shardingColumnName = properties.getProperty("shardingColumn");
        this.algorithmExpression = properties.getProperty("algorithmExpression");
    }

    @Override
    public List<String> getShardingColumns() {
        return Arrays.asList(shardingColumnName);
    }

    @Override
    public String doSharding(List<String> targetItems, String sourceItem, Map<String, Object> sqlParams) {
        // 这部分的运行机制待仔细研究
        String exp = InlineExpressionParser.handlePlaceHolder(algorithmExpression);
        InlineExpressionParser parser = new InlineExpressionParser(exp);
        Closure closure = parser.evaluateClosure();
        closure.setProperty(this.shardingColumnName, sqlParams.get(this.shardingColumnName));
        return closure.call().toString();
    }
}
