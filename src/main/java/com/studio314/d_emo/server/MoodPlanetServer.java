package com.studio314.d_emo.server;

import com.studio314.d_emo.pojo.TreeHoleCard;

import java.util.ArrayList;
import java.util.List;

public interface MoodPlanetServer {
    List<TreeHoleCard> getMoodPlanets(int userId);
}
