package com.studio314.d_emo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studio314.d_emo.pojo.TreeHoleCard;
import com.studio314.d_emo.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 通过邮箱获取用户
     * @param mail 邮箱
     * @return 用户类实例
     */
    @Select("select * from user where mail=#{mail}")
    User getUser_mail(String mail);

    /**
     * 通过电话获取用户
     * @param phone 电话
     * @return 用户类实例
     */
    @Select("select * from user where phone=#{phone}")
    User getUser_phone(String phone);

    /**
     * 新建用户
     * @param user 用户类实例
     */
    @Options(keyProperty = "ID", useGeneratedKeys = true)
    @Insert("insert into user(uName, mail, password) values (#{uName}, #{mail}, #{password})")
    void register(User user);
}
