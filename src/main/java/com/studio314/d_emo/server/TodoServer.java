package com.studio314.d_emo.server;

import com.studio314.d_emo.pojo.Todo;

import java.sql.Timestamp;
import java.util.List;

public interface TodoServer {

    // 获取用户的所有待办事项
    List<Todo> getTodoList(int id);

    // 添加待办事项
    int addTodoList(int userid, String name, int duration, Timestamp date,int isfinished);
    int markAsFinished(int id);
    int deleteTodo(int id);
    int updateTodoList(int id, String name, int duration, Timestamp date,int isfinished);

//    Todo get
}
