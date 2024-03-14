package com.studio314.d_emo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SleepCard {
    int sid;
    int uid;
    String date;
    float sleepTime;
    float deepSleepTime;
    float lightSleepTime;
    int score;
}
