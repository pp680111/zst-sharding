实现分库分表的思路：
1. 自行实现jdbc协议定义的接口，在实现中封装处理分库分表的逻辑（这种方式代码量非常大，因为jdbc api的接口非常的多）
2. 基于orm提供的interceptor类的机制来实现对sql的拦截，比如MyBatis提供的plugin机制

在MapperFactoryBean中插入调用ShardingEngine的逻辑，在StatementINterceptor中完成sql的替换

TODO:
* 交由数据库自增的处理
* 复杂查询sql的处理
* 跨表的数据联合处理
* 整个解析和替换sql语句的流程，还可以细化成多个engine的组合
* 检测到如果sql不支持路由到单个表运行的话，那就需要拆分成多个sql取不同的数据源执行
* 对于复杂的函数，还需要进行该写
    > 比如对于avg的count，在遇到多表的查询结果的聚合时，就会因为每个表的数据的不平均，导致无法直接把多个表的avg值直接简单求平均，因此需要改写sql，把每个表中的数据的数量也一起返回，最终在程序中按数据权重计算平均值

分表分页的实现思路：
比如说语句为`SELECT * FROM user LIMIT 10,10 ORDER BY id asc`
1. 对每张表打开一个cursor
2. 按照归并排序的思路，从每张表中取出一部分数据，按照id从小到大的顺序排序，如果满足翻页限制则返回，未满足则继续从cursor中取出数据