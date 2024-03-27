package com.studio314.d_emo.server.impl;

import com.studio314.d_emo.mapper.UserMapper;
import com.studio314.d_emo.mapper.VCodeMapper;
import com.studio314.d_emo.pojo.User;
import com.studio314.d_emo.pojo.VCode;
import com.studio314.d_emo.server.UserServer;
import com.studio314.d_emo.utils.EncodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserServerImpl implements UserServer {

    @Autowired
    UserMapper userMapper;

    @Autowired
    VCodeMapper vCodeMapper;

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

    @Override
    public int registerByCode(String mail, String name, String password, String code) {
        //判断是否已经注册
        User user = userMapper.getUser_mail(mail);
        if(user != null) { return -2; }

        //判断验证码是否正确
        List<VCode> codes = vCodeMapper.getCode(mail, code);
        boolean isOK = false;
        for(VCode s : codes) {
            LocalDateTime cTime = s.getCTime();
            LocalDateTime current = LocalDateTime.now();
            if (Duration.between(cTime, current).toMillis() <= 600000) {
                isOK = true;
                break;
            }
        }
        if(!isOK) { return -1; }

        //注册
        User user1 = new User();
        user1.setMail(mail);
        String cpw = EncodeUtils.bCryptEncode(password);
        user1.setPassword(cpw);
        user1.setUName(name);
        userMapper.register(user1);
        return user1.getID();
    }

    @Override
    public boolean isRegistered(String mail) {
        User user = userMapper.getUser_mail(mail);
        return user != null;
    }

}
