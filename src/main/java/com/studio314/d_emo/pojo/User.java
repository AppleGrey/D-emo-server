package com.studio314.d_emo.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息实体类
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int ID;
    private String mail;
    private String phone;
    private String password;
    private String head; //头像url
    @TableField("uName")
    private String uName;
    private String signature; //个性签名

}
