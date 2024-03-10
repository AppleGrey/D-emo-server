package com.studio314.d_emo.server;


import com.studio314.d_emo.Other.Cards;
import com.studio314.d_emo.Other.Statistic;
import com.studio314.d_emo.pojo.TreeHoleCard;

import java.util.List;

public interface ScrapBookServer {


    List<TreeHoleCard> getAllEmotionId(int userId);
    /**
     * 新建树洞卡片
     * @param imageURL 图片url
     * @param text 文本
     * @param emotionId 情感id
     */
    void insertTreeHoleCard(String imageURL, String text, int emotionId, int isPersonal, int userID);

    Statistic getStatistic(int userId, int flag);

    Cards getTreeHoleCard(int cardId);

    TreeHoleCard getOneTreeHoleCard(int cardId);

    List<TreeHoleCard> getADayTreeHoleCard(int userId);
}
