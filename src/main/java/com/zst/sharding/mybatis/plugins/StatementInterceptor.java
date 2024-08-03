package com.zst.sharding.mybatis.plugins;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

/**
 * StatementHandler是MyBatis中负责代理执行jdbc中的Statement的各种接口的类，
 *
 * 通过拦截StatementHandler的prepare方法，即可在将sql发送到db进行预编译之前拦截sql语句
 *
 * ps:经测试，Interceptor的执行在ShardingDataSource.determindCurrentLookupKey()之后，因此这里无法影响对数据源的选择
 */
@Slf4j
@Component
@Intercepts(
    @Signature(type = StatementHandler.class, method = "prepare", args = {java.sql.Connection.class, Integer.class})
)
public class StatementInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        // boundSql里面包含了已经预编译的sql语句，以及本次调用sql的参数的映射关系和参数值，只需要在拦截prepare的时候
        // 替换成一个实际要查询的数据表的名称的sql，就实现了分表的功能
        // TODO 实现sql对应的库和表的修改
        BoundSql boundSql = statementHandler.getBoundSql();
        return invocation.proceed();
    }
}
