package com.studio314.d_emo.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@TableName("summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Summary {
    int id;
    int userid;
    String text;
    int mood;
    Timestamp date;
}
