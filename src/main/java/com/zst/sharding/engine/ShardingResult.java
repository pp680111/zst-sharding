package com.zst.sharding.engine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShardingResult {
    private String targetDataSource;
    private String targetSchema;
    private String targetTable;
}
