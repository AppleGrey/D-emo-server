package com.studio314.d_emo.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@TableName("todo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo {
    int id;
    int userid;
    Timestamp date;
    String name;
    int duration;
    int isfinished;

    public Todo(int userid, Timestamp date, String name, int duration, int isfinished) {
        this.userid = userid;
        this.date = date;
        this.name = name;
        this.duration = duration;
        this.isfinished = isfinished;
    }
}
