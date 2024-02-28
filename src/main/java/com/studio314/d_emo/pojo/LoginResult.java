package com.studio314.d_emo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResult {
    int ID;
    String uName;
    String mail;
    String password;
    String head;
    String token;
    String avatar;
    String signature;
}
