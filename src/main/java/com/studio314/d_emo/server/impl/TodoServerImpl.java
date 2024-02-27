package com.studio314.d_emo.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studio314.d_emo.mapper.TodoMapper;
import com.studio314.d_emo.mapper.UserMapper;
import com.studio314.d_emo.pojo.Todo;
import com.studio314.d_emo.pojo.User;
import com.studio314.d_emo.server.TodoServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
@Component

public class TodoServerImpl implements TodoServer {
    @Autowired
    UserMapper userMapper;
    @Autowired
    TodoMapper todoMapper;

    @Override
    public List<Todo> getTodoList(int id) {
        //获取用户的所有待办事项
        LambdaQueryWrapper<Todo> wrapper = new LambdaQueryWrapper<Todo>();
        wrapper.eq(Todo::getUserid, id);
        List<Todo> todoList = todoMapper.selectList(wrapper);
        //找出所有未完成的待办事项并返回
        for (int i = 0; i < todoList.size(); i++) {
            if (todoList.get(i).getIsfinished() == 1) {
                todoList.remove(i);
                i--;
            }
        }
        return todoList;
    }

    @Override
    public int addTodoList(int userid, String name, int duration, Timestamp date,int isfinished) {
        todoMapper.insert(new Todo(userid, date, name, duration, 0));
        //返回新添加的待办事项的id
        return todoMapper.selectOne(new LambdaQueryWrapper<Todo>().eq(Todo::getUserid, userid).eq(Todo::getName, name).eq(Todo::getDate, date).eq(Todo::getDuration, duration)).getId();
    }

    @Override
    public int markAsFinished(int id) {
        Todo todo = todoMapper.selectById(id);
        todo.setIsfinished(1);
        return todoMapper.updateById(todo);
    }

    @Override
    public int deleteTodo(int id) {
        return todoMapper.deleteById(id);
    }

    @Override
    public int updateTodoList(int id, String name, int duration, Timestamp date,int isfinished) {
        Todo todo = todoMapper.selectById(id);
        todo.setName(name);
        todo.setDuration(duration);
        todo.setDate(date);
        todo.setIsfinished(isfinished);
        return todoMapper.updateById(todo);
    }
}
