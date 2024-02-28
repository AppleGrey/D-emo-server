package com.studio314.d_emo.controller;


import com.studio314.d_emo.pojo.Result;
import com.studio314.d_emo.server.MoodPlanetServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MoodPlanetController {

    @Autowired
    MoodPlanetServer moodPlanetServer;

    @GetMapping("/getMoodPlanets")
    public Result getMoodPlanet(int userId) {
        return Result.success(moodPlanetServer.getMoodPlanets(userId));
    }


}
