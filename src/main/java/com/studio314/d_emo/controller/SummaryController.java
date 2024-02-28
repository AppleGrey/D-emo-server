package com.studio314.d_emo.controller;

import com.studio314.d_emo.pojo.Result;
import com.studio314.d_emo.server.SummaryServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SummaryController {
    @Autowired
    SummaryServer summaryServer;

    @PostMapping("/getSummary")
    public Result getSummary(int userid) {
        return Result.success(summaryServer.getSummary(userid));
    }
}
