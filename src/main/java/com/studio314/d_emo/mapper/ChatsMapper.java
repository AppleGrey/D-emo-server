package com.studio314.d_emo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studio314.d_emo.pojo.Chats;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface ChatsMapper {
    @Insert("insert into chats(senderID, message, type) values (#{senderID}, #{message}, #{type})")
    void insertChat(int senderID, String message, int type);
}
