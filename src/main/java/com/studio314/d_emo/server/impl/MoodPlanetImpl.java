package com.studio314.d_emo.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studio314.d_emo.mapper.TreeHoleCardMapper;
import com.studio314.d_emo.pojo.TreeHoleCard;
import com.studio314.d_emo.server.MoodPlanetServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MoodPlanetImpl implements MoodPlanetServer {
    @Autowired
    TreeHoleCardMapper treeHoleCardMapper;

    @Override
    public List<TreeHoleCard> getMoodPlanets(int userId) {
        List<TreeHoleCard> treeHoleCards = treeHoleCardMapper.selectList(new LambdaQueryWrapper<TreeHoleCard>().eq(TreeHoleCard::getUserId, userId));
        return treeHoleCards;
    }
}
