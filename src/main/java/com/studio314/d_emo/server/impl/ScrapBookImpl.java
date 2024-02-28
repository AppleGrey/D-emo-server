package com.studio314.d_emo.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studio314.d_emo.mapper.TreeHoleCardMapper;
import com.studio314.d_emo.pojo.TreeHoleCard;
import com.studio314.d_emo.server.ScrapBookServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TODO
 *
 * @Description
 * @Author 钱波
 * @Date 2024/2/26 14:16
 **/
@Component
public class ScrapBookImpl implements ScrapBookServer {
    @Autowired
    private TreeHoleCardMapper treeHoleCardMapper;

    @Override
    public void insertTreeHoleCard(String imageURL, String text, int emotionId) {
        TreeHoleCard treeHoleCard = new TreeHoleCard();
        treeHoleCard.setImageURL(imageURL);
        treeHoleCard.setText(text);
        treeHoleCard.setEmotionId(emotionId);
        treeHoleCardMapper.insert(treeHoleCard);
    }

    @Override
    public List<TreeHoleCard> getAllEmotionId(int userId) {
        //取出该用户所有的情感id
        LambdaQueryWrapper<TreeHoleCard> wrapper = new LambdaQueryWrapper<TreeHoleCard>();
        wrapper.eq(TreeHoleCard::getUserId, userId);
        List<TreeHoleCard> treeHoleCards = treeHoleCardMapper.selectList(wrapper);
        return treeHoleCards;

    }

}
