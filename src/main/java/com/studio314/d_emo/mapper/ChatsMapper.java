package com.studio314.d_emo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studio314.d_emo.pojo.Chats;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface ChatsMapper extends BaseMapper<Chats>{
    @Insert("insert into chats(senderID, message, type, isReceiver, emotion) values (#{senderID}, #{message}, #{type}, #{isReceiver}, #{emotion})")
    void insertChat(int senderID, String message, int type, int isReceiver, String emotion);

    @Insert("insert into chats(senderID, message, type, isReceiver, audioTime) values (#{senderID}, #{message}, #{type}, #{isReceiver}, #{audioTime})")
    void insertChatWithAudio(int senderID, String message, int type, int isReceiver, int audioTime);


    @Select("select emotion from chats where senderID = #{senderID} and DATE(sendTime) = CURDATE();")
    List<Chats> getCurDayEmotion(int senderID);

}
