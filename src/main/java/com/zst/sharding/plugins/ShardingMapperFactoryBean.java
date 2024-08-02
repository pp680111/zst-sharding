package com.zst.sharding.plugins;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;

/**
 * 生成Mapper接口的代理对象的工厂类
 * @param <T>
 */
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

    }
}
