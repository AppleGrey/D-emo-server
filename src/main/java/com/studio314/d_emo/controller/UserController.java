package com.studio314.d_emo.controller;

import com.studio314.d_emo.mapper.VCodeMapper;
import com.studio314.d_emo.pojo.LoginResult;
import com.studio314.d_emo.pojo.Result;
import com.studio314.d_emo.pojo.User;
import com.studio314.d_emo.server.UserServer;
import com.studio314.d_emo.utils.JwtUtils;
import com.studio314.d_emo.utils.ValidateCodeUtils;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
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

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private VCodeMapper vCodeMapper;

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
    public Result register(String mail, String name, String password, String confirm, String code) {
        if(name.length() > 32) { return Result.error("昵称过长"); }
        if(password.length() > 48) { return Result.error("密码过长"); }
        if(password.length() < 6) { return Result.error("密码过短，至少为6位"); }
        if(!password.equals(confirm)) { return Result.error("两次密码不一致"); }
        
        int res = userServer.registerByCode(mail, name, password, code);
        if(res == -2) { return Result.error("邮箱已被注册"); }
        if(res == -1) { return Result.error("验证码错误"); }
        Map<String, Object> claims = new HashMap<>();
        claims.put("mail", mail);
        claims.put("uID", res);
        String jwt = JwtUtils.generateJwt(claims);
        log.info("用户:" + name + "，ID:" + res + "注册成功");
        return Result.success(jwt);
    }

    @PostMapping("/code")
    public Result getCode(String mail) throws UnsupportedEncodingException, AddressException {
        log.info("用户尝试获取验证码: " + mail);

        //判断邮箱是否已被注册
        boolean isRegistered = userServer.isRegistered(mail);
        if(isRegistered) {
            log.info("邮箱已被注册");
            return Result.error("邮箱已被注册");
        }

        //随机生成6位验证码
        String code = ValidateCodeUtils.generateValidateCode4String(6);
        // 邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("D-emo邮箱验证码");
        message.setText("尊敬的用户您好!\n\n感谢您使用D-emo。\n\n" + "您的校验验证码为: "+code+", 有效期10分钟，请不要把验证码信息泄露给其他人,如非本人请勿操作");
        message.setTo(mail);
        // 对方看到的发送人
        message.setFrom(new InternetAddress(MimeUtility.encodeText("可莉不知道哦")+"															<2982437139@qq.com>").toString());
        //发送邮件
        try {
            javaMailSender.send(message);
            //valueOperations.set(key,code,5L, TimeUnit.MINUTES);
            vCodeMapper.insert(mail, code);
            log.info("邮件发送成功");
        }catch (Exception e){
            log.error("邮件发送出现异常");
            log.error("异常信息为"+e.getMessage());
            log.error("异常堆栈信息为-->");
            e.printStackTrace();
            return Result.error("邮件发送失败，请重试");
        }
        return Result.success("验证码已发送");

    }
}
