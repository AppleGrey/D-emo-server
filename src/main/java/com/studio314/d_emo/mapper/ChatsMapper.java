package com.studio314.d_emo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studio314.d_emo.pojo.Chats;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface ChatsMapper extends BaseMapper<Chats>{
    @Insert("insert into chats(senderID, message, type, isReceiver) values (#{senderID}, #{message}, #{type}, #{isReceiver})")
    void insertChat(int senderID, String message, int type, int isReceiver);

    @Insert("insert into chats(senderID, message, type, isReceiver, audioTime) values (#{senderID}, #{message}, #{type}, #{isReceiver}, #{audioTime})")
    void insertChatWithAudio(int senderID, String message, int type, int isReceiver, int audioTime);
}
