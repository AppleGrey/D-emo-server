package com.studio314.d_emo.controller;

import com.studio314.d_emo.pojo.Result;
import com.studio314.d_emo.server.TodoServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RestController
public class TodoController {
    @Autowired
    TodoServer todoServer;

    // 获取用户的所有待办事项
    @GetMapping("/getTodoList")
    public Result getTodoList(int id) {
        return Result.success(todoServer.getTodoList(id));
    }

    // 添加待办事项
    @PostMapping("/addTodoList")
    public Result addTodoList(int userid, String name, int duration, String date,int isfinished) {
        Timestamp timestamp = null;
        try {
            long time = Long.parseLong(date);
            timestamp = new Timestamp(time);

        } catch (Exception e) {
            return Result.error("日期格式错误");
        }
        //将timestamp减少8小时
        timestamp.setTime(timestamp.getTime()-8*60*60*1000);
        return Result.success(todoServer.addTodoList(userid, name, duration,timestamp, isfinished));
    }
    @PostMapping("/markAsFinished")
    public Result markAsFinished(int id) {
        return Result.success(todoServer.markAsFinished(id));
    }
    @PostMapping("/deleteTodo")
    public Result deleteTodoList(int id) {
        return Result.success(todoServer.deleteTodo(id));
    }
    @PostMapping("/updateTodoList")
    public Result updateTodoList(int id, String name, int duration, String date,int isfinished) {
        Timestamp timestamp = null;
        try {
//            // 定义日期时间格式
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//            // 将字符串解析为日期
//            Date parsedDate = dateFormat.parse(date);
//
//            // 将日期转换为 Timestamp
//            timestamp = new Timestamp(parsedDate.getTime());
            long time = Long.parseLong(date);
            timestamp = new Timestamp(time);

        } catch (Exception e) {
            return Result.error("日期格式错误");
        }
        timestamp.setTime(timestamp.getTime()-8*60*60*1000);
        return Result.success(todoServer.updateTodoList(id, name, duration,timestamp, isfinished));
    }
}
