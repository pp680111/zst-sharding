package com.zst.sharding.plugins;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Proxy;

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

            return method.invoke(mapperProxyObj, args);
        });
    }
}
