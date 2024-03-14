package com.studio314.d_emo.server.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obs.services.ObsClient;
import com.studio314.d_emo.mapper.ChatsMapper;
import com.studio314.d_emo.mapper.SummaryMapper;
import com.studio314.d_emo.mapper.TreeHoleCardMapper;
import com.studio314.d_emo.pojo.Chats;
import com.studio314.d_emo.pojo.SleepCard;
import com.studio314.d_emo.pojo.Summary;
import com.studio314.d_emo.pojo.TreeHoleCard;
import com.studio314.d_emo.server.SummaryServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Slf4j
@Component
public class SummaryServerImpl implements SummaryServer {
    @Autowired
    private SummaryMapper summaryMapper;
    @Autowired
    private TreeHoleCardMapper treeHoleCardMapper;
    @Autowired
    private ChatsMapper chatsMapper;

//    @Value("${python.server}")
//    private String pythonServer;
    private static Boolean firstConnected;

//    @Override
//    public Summary getSummary(int userid) {
//        // 取出该用户所有的 summary，按日期倒序排列，并限制返回结果数量为1
//        LambdaQueryWrapper<Summary> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Summary::getUserid, userid);
//        wrapper.orderByDesc(Summary::getDate);
//        wrapper.last("limit 1");
//        return summaryMapper.selectOne(wrapper);
//    }

    @Override
    public Summary getSummary(List<SleepCard> sleepCards) throws IOException {
        // 获取用户最近七天树洞卡片的文本内容
        LocalDate currentDate = LocalDate.now();
        LocalDate before7Day = currentDate.minusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String before7DayStr = before7Day.format(formatter);
        LambdaQueryWrapper<TreeHoleCard> wrapper = new LambdaQueryWrapper<TreeHoleCard>();
        wrapper.eq(TreeHoleCard::getUserId, sleepCards.get(0).getUid());
        wrapper.ge(TreeHoleCard::getDate, before7DayStr);

        // 获取当前时间的前20条聊天记录
        LambdaQueryWrapper<Chats> wrapper1 = new LambdaQueryWrapper<Chats>();
        wrapper1.eq(Chats::getSenderID, sleepCards.get(0).getUid());
//        wrapper1.eq(Chats::getType, 0);
        wrapper1.orderByDesc(Chats::getSendTime);
        wrapper1.last("limit 20");
        List<Chats> chats = chatsMapper.selectList(wrapper1);
//        log.info("chat消息：");
        for (Chats chat : chats) {
            log.info(chat.toString());
        }
//        return "success";
        List<TreeHoleCard> treeHoleCards = treeHoleCardMapper.selectList(wrapper);
        // 获取日记内容和日期 并写入文件
        String fileName = sleepCards.get(0).getUid() + "summary.txt";
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("对话内容:[");
            bufferedWriter.newLine();
            for (int i = chats.size() -1; i >= 0; i--) {
                Chats chat = chats.get(i);
                if (chat.getIsReceiver() == 0){
                    bufferedWriter.write("{user: " + chat.getMessage() + ",");
//                    log.info("{user: " + chat.getMessage() + ",");
                } else {
                    bufferedWriter.write("小D: " + chat.getMessage() + "},");
//                    log.info("AI: " + chat.getMessage() + "},");
                }
                bufferedWriter.newLine();
            }
            bufferedWriter.write("]");
            bufferedWriter.newLine();

            bufferedWriter.write("日记内容:[");
            bufferedWriter.newLine();
            for (int i = treeHoleCards.size() - 1; i >= 0; i--) {
                TreeHoleCard treeHoleCard = treeHoleCards.get(i);
                bufferedWriter.write("{时间:" + treeHoleCard.getDate() + ",");
                bufferedWriter.newLine();
                bufferedWriter.write( "内容:" + treeHoleCard.getText() + "},");
                bufferedWriter.newLine();
            }
            bufferedWriter.write("]");
            bufferedWriter.newLine();

            bufferedWriter.write("最近七天的睡眠情况:[");
            bufferedWriter.newLine();
            for (int i = sleepCards.size() - 1; i >= 0; i--) {
                SleepCard sleep = sleepCards.get(i);
                bufferedWriter.write("{时间:" + sleep.getDate() + ",");
                bufferedWriter.newLine();
                bufferedWriter.write("睡眠总时长:" + sleep.getSleepTime() + ",");
                bufferedWriter.newLine();
                bufferedWriter.write("深度睡眠时长:" + sleep.getDeepSleepTime() + ",");
                bufferedWriter.newLine();
                bufferedWriter.write("浅睡时长:" + sleep.getLightSleepTime() + ",");
                bufferedWriter.newLine();
                bufferedWriter.write("睡眠得分:" + sleep.getScore() + "},");
                bufferedWriter.newLine();
            }
            bufferedWriter.write("]");
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 上传到桶
        // 创建ObsClient实例
        ObsClient obsClient = new ObsClient("NGJ1E1NK3Z9UZRVU89XG", "8wSYoKMP4lrYpdYUIbJGZVblpmEUrshXZsOVi2A2", "https://obs.cn-north-4.myhuaweicloud.com");
        // localfile为待上传的本地文件路径，需要指定到具体的文件名
        obsClient.putObject("d-emo", "summary/" + fileName, file);
        try {
            obsClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Socket socket;
        // 通知python服务器
        try {
            socket = new Socket("59.110.126.104", 12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String url = "https://d-emo.obs.cn-north-4.myhuaweicloud.com/summary/" + fileName;
        //将消息封装成json格式
        firstConnected = true;
        JSONObject messageJsonObject = new JSONObject();
        messageJsonObject.put("type", "summary");
        messageJsonObject.put("ID", sleepCards.get(0).getUid());
        messageJsonObject.put("data", url);
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
            socket = new Socket("59.110.126.104", 12345);
            bos = new BufferedOutputStream(socket.getOutputStream());
            log.info("重新连接成功");
            messageJsonObject.put("firstConnect", true);
            bos.write(messageJsonObject.toJSONString().getBytes());
            firstConnected = false;
            bos.flush();
        }

        // 读取服务器上的响应数据
        BufferedInputStream bis1 = new BufferedInputStream(socket.getInputStream());
        byte[] data = new byte[10240];
        int len = bis1.read(data);
        String rec = new String(data,0,len);
//        log.info("rec:"+rec);
        JSONObject recJsonObject = JSONObject.parseObject(rec);
        String recData = recJsonObject.getString("data");
        String emotion = recJsonObject.getString("emotion");
        //text为utf-8字符，解码
        String recText = new String(recData.getBytes("utf-8"), "utf-8");
        //关闭流
        bos.close();
        bis1.close();
        socket.close();
        log.info("recText:"+recText);

        // 将情绪写入数据库
        Summary summary = new Summary();
        summary.setUserid(sleepCards.get(0).getUid());
        log.info("emotion:"+emotion);
        if (emotion.equals("['消极']")) {
            summary.setMood(2131230914);
        } else if (emotion.equals("['积极']")) {
            summary.setMood(2131230932);
        } else {
            summary.setMood(2131230912);
        }
        summary.setText(recText);
//        summaryMapper.insert(summary);

        return summary;
    }
}
