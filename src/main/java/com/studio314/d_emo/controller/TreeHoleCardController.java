package com.studio314.d_emo.controller;

import com.studio314.d_emo.pojo.Result;
import com.studio314.d_emo.server.ScrapBookServer;
import com.studio314.d_emo.server.UserServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


@Slf4j
@RestController
public class TreeHoleCardController {
    @Autowired
    ScrapBookServer scrapBookServer;

    @PostMapping("/insertTreeHoleCard")
    public Result insertTreeHoleCard(String imageURL, String text, int emotionId, int isPersonal, int userID) {
        scrapBookServer.insertTreeHoleCard(imageURL, text, emotionId, isPersonal,userID);
        log.info("插入树洞卡片成功");
        return Result.success();
    }
    @PostMapping("/getAllEmotionId")
    public Result getAllEmotionId(int userId) {
        return Result.success(scrapBookServer.getAllEmotionId(userId));
    }

    @PostMapping("/getStatistic")
    public Result getStatistic(int userId,int flag) {
        return Result.success(scrapBookServer.getStatistic(userId,flag));
    }

    @PostMapping("/getTreeHoleCard")
    public Result getTreeHoleCard(int cardID) {
        return Result.success(scrapBookServer.getTreeHoleCard(cardID));
    }
    @GetMapping("/getOneTreeHoleCard")
    public Result getOneTreeHoleCard(int cardId) {
        return Result.success(scrapBookServer.getOneTreeHoleCard(cardId));
    }

    @GetMapping("/getADayTreeHoleCard")
    public Result getADayTreeHoleCard(int userId, String date) {
        return Result.success(scrapBookServer.getADayTreeHoleCard(userId, date));
    }

    @GetMapping("/getScrapBookEmotion")
    public Result getScrapBookEmotion(int userId) {
        return Result.success(scrapBookServer.getScrapBookEmotion(userId));
    }
}
