package com.studio314.d_emo.controller;

import com.studio314.d_emo.pojo.Chats;
import com.studio314.d_emo.pojo.Result;
import com.studio314.d_emo.server.impl.ChatServerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class ChatsController {
    @Autowired
    ChatServerImpl chatServer;

    @GetMapping("/getChats")
    public Result getChats(int userId) {
        List<Chats> chatsList = chatServer.getChats(userId);
        for (Chats chat : chatsList) {
            log.info(chat.toString());
        }
        return Result.success(chatServer.getChats(userId));
    }

}
