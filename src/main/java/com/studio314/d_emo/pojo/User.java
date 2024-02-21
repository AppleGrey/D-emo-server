package com.studio314.d_emo.pojo;

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
    private String uName;

}
