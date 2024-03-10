package com.studio314.d_emo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VCode {

    private String mail;
    private String code;
    private LocalDateTime cTime;
}
