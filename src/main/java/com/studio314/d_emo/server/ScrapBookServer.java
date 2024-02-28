package com.studio314.d_emo.server;

import com.studio314.d_emo.pojo.TreeHoleCard;

import java.util.List;

/**
 * @ClassName ScrapBookServer
 * @description: 对应前端日记类的服务接口
 * @author 钱波
 * @date 2024年02月26日
 * @version: 1.0
 */
public interface ScrapBookServer {
    /**
     * 新建树洞卡片
     * @param imageURL 图片url
     * @param text 文本
     * @param emotionId 情感id
     */
    void insertTreeHoleCard(String imageURL, String text, int emotionId);

    List<TreeHoleCard> getAllEmotionId(int userId);
}
