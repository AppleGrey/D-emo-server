package com.studio314.d_emo.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studio314.d_emo.mapper.ChatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import com.studio314.d_emo.pojo.Chats;
@Component
public class ChatServerImpl {
    @Autowired
    ChatsMapper chatsMapper;

    public List<Chats> getChats(int userId) {
        List<Chats> chats = chatsMapper.selectList(new LambdaQueryWrapper<>(Chats.class).eq(Chats::getSenderID, userId));
        return chats;
    }


}
