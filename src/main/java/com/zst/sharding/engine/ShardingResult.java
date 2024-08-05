package com.zst.sharding.engine;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShardingResult {
    private String targetDataSource;
    private String targetSchema;
    private String targetTable;
}
