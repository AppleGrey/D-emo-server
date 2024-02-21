package com.studio314.d_emo.server;

import com.studio314.d_emo.pojo.User;

/**
 * 用户相关服务类
 */

public interface UserServer {

    User login(String mail, String password);


    User register(String mail, String name, String password);
}
