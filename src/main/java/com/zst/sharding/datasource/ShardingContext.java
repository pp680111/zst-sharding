package com.zst.sharding.datasource;

public class ShardingContext {
    private static final ThreadLocal<ShardingResult> context = new ThreadLocal<>();

    public static ShardingResult get() {
        return context.get();
    }

    public static void set(ShardingResult shardingResult) {
        context.set(shardingResult);
    }
}
