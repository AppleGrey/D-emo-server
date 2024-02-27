package com.studio314.d_emo.controller;

import com.studio314.d_emo.pojo.Result;
import com.studio314.d_emo.server.ScrapBookServer;
import com.studio314.d_emo.server.UserServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Result insertTreeHoleCard(String imageURL, String text, int emotionId, int isPersonal) {
        scrapBookServer.insertTreeHoleCard(imageURL, text, emotionId, isPersonal);
        log.info("插入树洞卡片成功");
        return Result.success();
    }
}
