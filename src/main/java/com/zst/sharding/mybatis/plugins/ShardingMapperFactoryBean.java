package com.zst.sharding.mybatis.plugins;

import com.zst.sharding.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 生成Mapper接口的代理对象的工厂类
 *
 * 注意需要显式配置Mapper的FactoryBean，可以通过MapperScan注解或者配置文件
 * @param <T>
 */
@Slf4j
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {
    public ShardingMapperFactoryBean() {

    }

    public ShardingMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    /**
     * 获取当前FactoryBean对应的Mapper接口的代理对象
     * @return
     * @throws Exception
     */
    @Override
    public T getObject() throws Exception {
        T mapperProxyObj = super.getObject();
        SqlSession sqlSession = getSqlSession();
        Configuration configuration = sqlSession.getConfiguration();
        Class<?> mapperClazz = getMapperInterface();

        return (T) Proxy.newProxyInstance(mapperClazz.getClassLoader(), new Class[]{mapperClazz}, (proxy, method, args) -> {
            String mapperId = mapperClazz.getName() + "." + method.getName();
            MappedStatement mappedStatement = configuration.getMappedStatement(mapperId);
            BoundSql boundSql = mappedStatement.getBoundSql(args);

            // TODO 调用ShardingEngine计算分片结果

            return method.invoke(mapperProxyObj, args);
        });
    }

    /**
     * 处理封装为DTO传参的参数列表，将它们拆分为Object数组，按照sql中参数值使用的顺序
     * @param boundSql
     * @param args
     * @return
     */
    private Object[] processArgs(BoundSql boundSql, Object[] args) {
        List<Object> result = new ArrayList<>();

        // 排除掉参数确实只有一个时的情况（String呢？）
        if (args.length == 1 && !ClassUtils.isPrimitiveOrWrapper(args[0].getClass())) {
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

            // 从boundSql记录的参数mapping关系中，获取参数名称(应该是按照sql中参数出现的顺序的）
            List<String> columnNames = parameterMappings.stream().map(ParameterMapping::getProperty).toList();
            for (String columnName : columnNames) {
                result.add(ReflectionUtils.getFieldValue(args[0], columnName));
            }
        }

        return result.toArray();
    }
}
