package com.studio314.d_emo.controller;

import com.studio314.d_emo.pojo.LoginResult;
import com.studio314.d_emo.pojo.Result;
import com.studio314.d_emo.pojo.User;
import com.studio314.d_emo.server.UserServer;
import com.studio314.d_emo.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关控制类
 */

@Slf4j
@RestController
public class UserController {

    @Autowired
    UserServer userServer;

    /**
     * 用户登录
     * @param mail 邮箱或电话
     * @param password 密码
     * @return 登录成功：token
     *         登录失败：失败原因
     */
    @PostMapping("/login")
    public Result login(String mail, String password) {
        User user = userServer.login(mail, password);
        if(user != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("ID", user.getID());
            String jwt = JwtUtils.generateJwt(claims);
            log.info("用户:" + user.getUName() + "，ID:" + user.getID() + "登录成功");
            LoginResult loginResult = new LoginResult();
            loginResult.setID(user.getID());
            loginResult.setUName(user.getUName());
            loginResult.setMail(user.getMail());
            loginResult.setPassword(user.getPassword());
            loginResult.setHead(user.getHead());
            loginResult.setSignature(user.getSignature());
            loginResult.setToken(jwt);
            return Result.success(loginResult);
        }
        log.info("用户尝试登录失败，邮箱为:" + mail);
        return Result.error("login fail");
    }

    /**
     * 用户注册
     * @param mail 邮箱或电话
     * @param name 昵称
     * @param password 密码
     * @param confirm 确认密码
     * @return 注册成功：jwt
     *         注册失败：失败原因
     */
    @PostMapping("/register")
    public Result register(String mail, String name, String password, String confirm) {
        if(name.length() > 32) { return Result.error("昵称过长"); }
        if(password.length() > 48) { return Result.error("密码过长"); }
        if(!password.equals(confirm)) { return Result.error("两次密码不一致"); }
        
        User user = userServer.register(mail, name, password);
        if(user == null) { return Result.error("用户已存在"); }

        Map<String, Object> claims = new HashMap<>();
        claims.put("ID", user.getID());
        String jwt = JwtUtils.generateJwt(claims);
        log.info("用户:" + user.getUName() + "，ID:" + user.getID() + "注册成功");
        return Result.success(jwt);
    }
}
