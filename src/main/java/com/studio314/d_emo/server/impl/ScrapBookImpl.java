package com.studio314.d_emo.server.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obs.services.ObsClient;
import com.studio314.d_emo.Other.Cards;
import com.studio314.d_emo.mapper.ChatsMapper;
import com.studio314.d_emo.mapper.TreeHoleCardMapper;
import com.studio314.d_emo.mapper.UserMapper;
import com.studio314.d_emo.pojo.Chats;
import com.studio314.d_emo.pojo.TreeHoleCard;
import com.studio314.d_emo.server.ScrapBookServer;
import com.studio314.d_emo.Other.Statistic;
import com.studio314.d_emo.utils.PADAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ScrapBookImpl implements ScrapBookServer {
    @Autowired
    TreeHoleCardMapper treeHoleCardMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ChatsMapper chatsMapper;

    @Override
    public void insertTreeHoleCard(String imageURL, String text, int emotionId, int isPersonal, int userID) {
        Socket socket;
        // 通知python服务器
        try {
            socket = new Socket("59.110.126.104", 12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //将消息封装成json格式
        boolean firstConnected = true;
        JSONObject messageJsonObject = new JSONObject();
        messageJsonObject.put("ID", userID);
        messageJsonObject.put("type", "respond");
        messageJsonObject.put("data", imageURL);
        messageJsonObject.put("other", text);
        messageJsonObject.put("firstConnect", firstConnected);
        //socket将clientMessage发送给服务器
        BufferedOutputStream bos;
        try {
            bos = new BufferedOutputStream(socket.getOutputStream());
            bos.write(messageJsonObject.toJSONString().getBytes());
            firstConnected = false;
            bos.flush();
        } catch(Exception e) {
            log.info("服务器已断开");
            try {
                socket = new Socket("59.110.126.104", 12345);
                bos = new BufferedOutputStream(socket.getOutputStream());
                log.info("重新连接成功");
                messageJsonObject.put("firstConnect", true);
                bos.write(messageJsonObject.toJSONString().getBytes());
                firstConnected = false;
                bos.flush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }

        // 读取服务器上的响应数据
        BufferedInputStream bis1 = null;
        byte[] data;
        int len;
        try {
            bis1 = new BufferedInputStream(socket.getInputStream());
            data = new byte[10240];
            len = bis1.read(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String rec = new String(data,0,len);
//        log.info("rec:"+rec);
        JSONObject recJsonObject = JSONObject.parseObject(rec);
        String recData = recJsonObject.getString("data");


        TreeHoleCard treeHoleCard = new TreeHoleCard();
        treeHoleCard.setImageURL(imageURL);
        treeHoleCard.setText(text);
        treeHoleCard.setEmotionId(emotionId);
        treeHoleCard.setIsPersonal(isPersonal);
        treeHoleCard.setUserId(userID);
        treeHoleCard.setComment(recData);
        treeHoleCardMapper.insert(treeHoleCard);
    }

    @Override
    public Statistic getStatistic(int userId, int flag) {
        if(flag==1){
            // 获取当前日期
            LocalDate currentDate = LocalDate.now();
            // 获取当前日期的前7天
            LocalDate before7Day = currentDate.minusDays(7);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String before7DayStr = before7Day.format(formatter);
            //返回一周之内最多的6个情感，如果不足6个则返回全部
            LambdaQueryWrapper<TreeHoleCard> wrapper = new LambdaQueryWrapper<TreeHoleCard>();
            wrapper.eq(TreeHoleCard::getUserId, userId);
            wrapper.ge(TreeHoleCard::getDate, before7DayStr);
            wrapper.groupBy(TreeHoleCard::getEmotionId);
            wrapper.orderByDesc(TreeHoleCard::getEmotionId);
            wrapper.select(TreeHoleCard::getEmotionId);
            wrapper.last("limit 6");
            List<TreeHoleCard> treeHoleCards = treeHoleCardMapper.selectList(wrapper);
            Statistic statistic = new Statistic();
            statistic.setTreeHoleCards(treeHoleCards);
            //返回每个情感的数量
            List<Integer> count = new ArrayList<>();
            for (TreeHoleCard treeHoleCard : treeHoleCards) {
                LambdaQueryWrapper<TreeHoleCard> wrapper1 = new LambdaQueryWrapper<TreeHoleCard>();
                wrapper1.eq(TreeHoleCard::getUserId, userId);
                wrapper1.ge(TreeHoleCard::getDate, before7DayStr);
                wrapper1.eq(TreeHoleCard::getEmotionId, treeHoleCard.getEmotionId());
                count.add(Math.toIntExact(treeHoleCardMapper.selectCount(wrapper1)));
            }
            statistic.setCount(count);
            return statistic;
        } else if (flag == 2) {
            LocalDate currentDate = LocalDate.now();
            LocalDate before30Day = currentDate.minusDays(30);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String before30DayStr = before30Day.format(formatter);
            //返回一个月之内最多的6个情感，如果不足6个则返回全部
            LambdaQueryWrapper<TreeHoleCard> wrapper = new LambdaQueryWrapper<TreeHoleCard>();
            wrapper.eq(TreeHoleCard::getUserId, userId);
            wrapper.ge(TreeHoleCard::getDate, before30DayStr);
            wrapper.groupBy(TreeHoleCard::getEmotionId);
            wrapper.orderByDesc(TreeHoleCard::getEmotionId);
            wrapper.last("limit 6");
            List<TreeHoleCard> treeHoleCards = treeHoleCardMapper.selectList(wrapper);
            Statistic statistic = new Statistic();
            statistic.setTreeHoleCards(treeHoleCards);
            //返回每个情感的数量
            List<Integer> count = new ArrayList<>();
            for (TreeHoleCard treeHoleCard : treeHoleCards) {
                LambdaQueryWrapper<TreeHoleCard> wrapper1 = new LambdaQueryWrapper<TreeHoleCard>();
                wrapper1.eq(TreeHoleCard::getUserId, userId);
                wrapper1.ge(TreeHoleCard::getDate, before30DayStr);
                wrapper1.eq(TreeHoleCard::getEmotionId, treeHoleCard.getEmotionId());
                count.add(Math.toIntExact(treeHoleCardMapper.selectCount(wrapper1)));
            }
        }else {
            LocalDate currentDate = LocalDate.now();
            LocalDate before365Day = currentDate.minusDays(365);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String before365DayStr = before365Day.format(formatter);

            //返回一年之内最多的6个情感，如果不足6个则返回全部
            LambdaQueryWrapper<TreeHoleCard> wrapper = new LambdaQueryWrapper<TreeHoleCard>();
            wrapper.eq(TreeHoleCard::getUserId, userId);
            wrapper.ge(TreeHoleCard::getDate, before365DayStr);
            wrapper.groupBy(TreeHoleCard::getEmotionId);
            wrapper.orderByDesc(TreeHoleCard::getEmotionId);
            wrapper.last("limit 6");
            List<TreeHoleCard> treeHoleCards = treeHoleCardMapper.selectList(wrapper);
            Statistic statistic = new Statistic();
            statistic.setTreeHoleCards(treeHoleCards);
            //返回每个情感的数量
            List<Integer> count = new ArrayList<>();
            for (TreeHoleCard treeHoleCard : treeHoleCards) {
                LambdaQueryWrapper<TreeHoleCard> wrapper1 = new LambdaQueryWrapper<TreeHoleCard>();
                wrapper1.eq(TreeHoleCard::getUserId, userId);
                wrapper1.ge(TreeHoleCard::getDate, before365DayStr);
                wrapper1.eq(TreeHoleCard::getEmotionId, treeHoleCard.getEmotionId());
                count.add(Math.toIntExact(treeHoleCardMapper.selectCount(wrapper1)));
            }
            statistic.setCount(count);
            return statistic;
        }
        return null;
    }

    @Override
    public Cards getTreeHoleCard(int cardID) {
        LambdaQueryWrapper<TreeHoleCard> wrapper = new LambdaQueryWrapper<TreeHoleCard>();
        wrapper.gt(TreeHoleCard::getCardID, cardID);
        wrapper.last("limit 10");
        List<TreeHoleCard> treeHoleCards = treeHoleCardMapper.selectList(wrapper);
        Cards cards = new Cards();
        cards.treeHoleCards = treeHoleCards;
        //遍历treeHoleCards，取出每个treeHoleCard的userId，然后根据userId取出username
        for (TreeHoleCard treeHoleCard : treeHoleCards) {
            int userId = treeHoleCard.getUserId();
            String username = userMapper.selectById(userId).getUName();
            cards.usernames.put(userId, username);
        }
        return cards;
    }

    @Override
    public TreeHoleCard getOneTreeHoleCard(int cardId) {
        LambdaQueryWrapper<TreeHoleCard> wrapper = new LambdaQueryWrapper<TreeHoleCard>();
        wrapper.eq(TreeHoleCard::getCardID, cardId);
        TreeHoleCard treeHoleCard = treeHoleCardMapper.selectOne(wrapper);
        return treeHoleCard;
    }

    @Override
    public List<TreeHoleCard> getADayTreeHoleCard(int userId, String date) {
//        //获取当天的树洞卡片
//        LocalDate currentDate = LocalDate.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        String currentDateStr = currentDate.format(formatter);
//        LambdaQueryWrapper<TreeHoleCard> wrapper = new LambdaQueryWrapper<TreeHoleCard>();
//        wrapper.eq(TreeHoleCard::getUserId, userId);
//        // 时间大于等于当天的0点
//        wrapper.ge(TreeHoleCard::getDate, currentDateStr);
//        List<TreeHoleCard> treeHoleCards = treeHoleCardMapper.selectList(wrapper);
//        log.info("【ScrapBookImpl】getADayTreeHoleCard: " + treeHoleCards);
        // 获取date时间的树洞卡片
        LambdaQueryWrapper<TreeHoleCard> wrapper = new LambdaQueryWrapper<TreeHoleCard>();
        wrapper.eq(TreeHoleCard::getUserId, userId);
        // 时间大于等于date的0点
        wrapper.ge(TreeHoleCard::getDate, date);
        // 小于date的24点 满足Timestamp格式
        wrapper.lt(TreeHoleCard::getDate, date + " 23:59:59");
        List<TreeHoleCard> treeHoleCards = treeHoleCardMapper.selectList(wrapper);
        log.info("【ScrapBookImpl】getADayTreeHoleCard: " + treeHoleCards);
        return treeHoleCards;
    }


    @Override
    public List<TreeHoleCard> getAllEmotionId(int userId) {
        //取出该用户所有的情感id
        LambdaQueryWrapper<TreeHoleCard> wrapper = new LambdaQueryWrapper<TreeHoleCard>();
        wrapper.eq(TreeHoleCard::getUserId, userId);
        List<TreeHoleCard> treeHoleCards = treeHoleCardMapper.selectList(wrapper);
        return treeHoleCards;
    }

    @Override
    public String getScrapBookEmotion(int userId) {
//        //获取用户最近的一条聊天的emotion
//        LambdaQueryWrapper<Chats> wrapper = new LambdaQueryWrapper<Chats>();
//        wrapper.eq(Chats::getSenderID, userId);
//        wrapper.eq(Chats::getIsReceiver, 0);
//        // emotion 不为空
//        wrapper.isNotNull(Chats::getEmotion);
//        // emotion 不为 -1
//        wrapper.ne(Chats::getEmotion, "-1");
//        wrapper.orderByDesc(Chats::getSendTime);
//        wrapper.last("limit 1");
//        Chats chats = chatsMapper.selectOne(wrapper);
//        if (chats == null || chats.getEmotion() == null){
//            return "-1";
//        }
//        return chats.getEmotion();

        //先获取当天情绪
        List<Chats> emoList = chatsMapper.getCurDayEmotion(userId);
        Map<String,Integer> counts = new HashMap<>();
        for(Chats chats:emoList){
            if(!counts.containsKey(chats.getEmotion())){
                counts.put(chats.getEmotion(),1);
            }else {
                counts.replace(chats.getEmotion(),counts.get(chats.getEmotion())+1);
            }
        }
        // 找到众数
        int maxCount = 0;
        String maxEmotion = null;
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxEmotion = entry.getKey();
            }
        }
        int type = PADAlgorithm.emojiIDMap.get(maxEmotion);



    }




}

