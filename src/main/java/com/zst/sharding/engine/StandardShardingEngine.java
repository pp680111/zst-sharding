package com.zst.sharding.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.zst.sharding.config.ShardingProperties;
import com.zst.sharding.engine.strategy.ShardingStrategy;
import com.zst.sharding.engine.strategy.ShardingStrategyFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 这个是每个需要分表的schema的独享的吗？还是所有的schema共享的
 */
public class StandardShardingEngine implements ShardingEngine {
    private ShardingProperties properties;
    /**
     * schema名称与这个schema内有多少要使用到的table名称的map，比如db0->[t_user_0,t_user_1,t_item_0,t_item_1],db1->[t_user_0,t_user_1,t_item_0,t_item_1]
     */
    private final MultiValueMap<String, String> actualDatabaseNames = new LinkedMultiValueMap<>();
    /**
     * table
     */
    private final MultiValueMap<String, String> actualTableNames = new LinkedMultiValueMap<>();
    private final Map<String, ShardingStrategy> databaseStrategy = new HashMap<>();
    private final Map<String, ShardingStrategy> tableStrategy = new HashMap<>();
    public StandardShardingEngine(ShardingProperties properties) {
        this.properties = properties;
        init();
    }

    @Override
    public ShardingResult sharding(String sql, Object[] args) {
        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        String tableName = "";
        Map<String, Object> columnParams = new HashMap<>();

        // insert语句的解析逻辑有所不同
        if (sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
            tableName = sqlInsertStatement.getTableName().getSimpleName();

            // 转换一个columnName->args的map
            List<SQLExpr> columns = sqlInsertStatement.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                SQLIdentifierExpr column = (SQLIdentifierExpr) columns.get(i);
                columnParams.put(column.getSimpleName(), args[i]);
            }
        } else {
            // TODO select update delete的处理
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            visitor.setParameters(Arrays.asList(args));
            sqlStatement.accept(visitor);

            List<SQLName> sqlNames = visitor.getOriginalTables();
            if (sqlNames.size() > 1) {
                throw new RuntimeException("不支持同时sql语句中多个表的分表");
            }
            tableName = sqlNames.get(0).getSimpleName();

            for (int i = 0; i < visitor.getParameters().size(); i++) {
                TableStat.Condition condition = visitor.getConditions().get(i);
                columnParams.put(condition.getColumn().getName(), condition.getValues().get(0));
            }
        }

        if (!checkIsNeedSharding(tableName)) {
            return new ShardingResult(null, null, null, sql);
        }

        // TODO actualDatabaseName的key是schema名称不是table名称吧，这怎么拿到数据的
        ShardingStrategy dbStrategy = databaseStrategy.get(tableName);
        String targetDatabaseName = dbStrategy.doSharding(actualDatabaseNames.get(tableName), tableName, columnParams);

        // TODO 这里也有一样的问题，难不成是因为现在用的ShardingStrategy里面用不到前两个参数就乱来了？
        ShardingStrategy tableStrategy = this.tableStrategy.get(tableName);
        String targetTableName = tableStrategy.doSharding(actualTableNames.get(tableName), tableName, columnParams);

        String resultSql = handleExecuteSql(targetDatabaseName, targetDatabaseName, tableName, targetTableName, sql);
        return new ShardingResult(null, targetDatabaseName, targetTableName, resultSql);
    }

    private void init() {
        Map<String, ShardingProperties.TableProperties> tables = properties.getTables();
        if (tables == null || tables.isEmpty()) {
            return;
        }

        tables.forEach((tableName, tableProperties) -> {
            tableProperties.getActualDataNodes().forEach(actualDataNode -> {
                // actualDataNode的值格式为db0.t_user_00
                String[] split = actualDataNode.split("\\.");
                // 这里这两个map添加进去的值没看懂
                actualDatabaseNames.add(split[0], split[1]);
                actualTableNames.add(split[1], split[0]);
            });

            databaseStrategy.put(tableName, ShardingStrategyFactory.getShardingStrategy(tableProperties.getDatabaseStrategy()));
            tableStrategy.put(tableName, ShardingStrategyFactory.getShardingStrategy(tableProperties.getTableStrategy()));
        });
    }

    private boolean checkIsNeedSharding(String tableName) {
        return databaseStrategy.containsKey(tableName) && tableStrategy.containsKey(tableName);
    }

    private String handleExecuteSql(String databaseName, String targetDataBaseName,
                                    String tableName, String targetTableName, String sql) {
        if (sql.contains(databaseName + "." + tableName)) {
            return sql.replace(databaseName + "." + tableName, targetDataBaseName + "." + targetTableName);
        } else {
            return sql.replace(tableName, targetDataBaseName + "." + targetTableName);
        }
    }
}
