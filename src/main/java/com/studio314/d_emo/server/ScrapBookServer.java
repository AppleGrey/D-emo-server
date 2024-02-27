package com.studio314.d_emo.server;


public interface ScrapBookServer {
    /**
     * 新建树洞卡片
     * @param imageURL 图片url
     * @param text 文本
     * @param emotionId 情感id
     */
    void insertTreeHoleCard(String imageURL, String text, int emotionId, int isPersonal);
}
