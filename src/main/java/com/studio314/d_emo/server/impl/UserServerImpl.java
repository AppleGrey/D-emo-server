package com.studio314.d_emo.server.impl;

import com.studio314.d_emo.mapper.UserMapper;
import com.studio314.d_emo.pojo.User;
import com.studio314.d_emo.server.UserServer;
import com.studio314.d_emo.utils.EncodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserServerImpl implements UserServer {

    @Autowired
    UserMapper userMapper;

    /**
     *
     * @param mail 邮箱或电话
     * @param password 密码
     * @return user实例
     */
    @Override
    public User login(String mail, String password) {
        //先通过邮箱匹配用户
        User user = userMapper.getUser_mail(mail);
        if(user != null && EncodeUtils.bCryptMatch(password, user.getPassword())) { //密码匹配
            return user;
        } else {
            //通过电话匹配用户
            user = userMapper.getUser_phone(mail);
            if(user != null && EncodeUtils.bCryptMatch(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    /**
     *
     * @param mail 邮箱或电话
     * @param name 姓名
     * @param password 密码
     * @return user实例
     */
    @Override
    public User register(String mail, String name, String password) {
        //判断邮箱是否被注册
        User u = userMapper.getUser_mail(mail);
        if(u != null) {
            return null;
        }

        //新建用户
        User user = new User();
        user.setMail(mail);
        String cpw = EncodeUtils.bCryptEncode(password);
        user.setPassword(cpw);
        user.setUName(name);

        //注册到数据库
        userMapper.register(user);

        return user;
    }

}
