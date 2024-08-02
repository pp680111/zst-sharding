package com.zst.sharding.demo.module.user;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("insert into t_user (id, name) values (#{id}, #{name})")
    int insert(User user);

    @Select("select * from t_user where id = #{id}")
    User selectById(Long id);
}
