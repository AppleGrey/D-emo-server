package com.studio314.d_emo.server.impl;

import com.studio314.d_emo.mapper.TreeHoleCardMapper;
import com.studio314.d_emo.pojo.TreeHoleCard;
import com.studio314.d_emo.server.ScrapBookServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ScrapBookImpl implements ScrapBookServer {
    @Autowired
    TreeHoleCardMapper treeHoleCardMapper;

    @Override
    public void insertTreeHoleCard(String imageURL, String text, int emotionId, int isPersonal, int userID) {
        TreeHoleCard treeHoleCard = new TreeHoleCard();
        treeHoleCard.setImageURL(imageURL);
        treeHoleCard.setText(text);
        treeHoleCard.setEmotionId(emotionId);
        treeHoleCard.setIsPersonal(isPersonal);
        treeHoleCard.setUserId(userID);
        treeHoleCardMapper.insert(treeHoleCard);
    }

    @Override
    public TreeHoleCard getTreeHoleCard(int cardId) {
        TreeHoleCard treeHoleCard = treeHoleCardMapper.selectById(cardId);
        return treeHoleCard;
    }


}
