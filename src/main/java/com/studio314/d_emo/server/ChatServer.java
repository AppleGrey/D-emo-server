package com.studio314.d_emo.server;

import com.alibaba.fastjson.JSONObject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
@ServerEndpoint("/webSocketServer/{myUserId}")
public class ChatServer {

    /**
     * 与客户端的连接会话，需要通过他来给客户端发消息
     */
    private Session session;

    /**
     * 当前用户ID
     */
    private String userId;

    /**
     *  concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     *  虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
     */
    private static CopyOnWriteArraySet<ChatServer> webSockets =new CopyOnWriteArraySet<>();

    /**
     *用来存在线连接用户信息
     */
    private static ConcurrentHashMap<String,Session> sessionPool = new ConcurrentHashMap<String,Session>();

    /**
     * 连接成功方法
     * @param session 连接会话
     * @param userId 用户编号
     */
    @OnOpen
    public void onOpen(Session session , @PathParam("myUserId") String userId){
        try {
            this.session = session;
            this.userId = userId;
            webSockets.add(this);
            sessionPool.put(userId, session);
            log.info("【websocket消息】 用户：" + userId + " 加入连接...");
        } catch (Exception e) {
            log.error("---------------WebSocket连接异常---------------");
        }
    }

    /**
     * 关闭连接
     */
    @OnClose
    public void onClose(){
        try {
            webSockets.remove(this);
            sessionPool.remove(this.userId);
            log.info("【websocket消息】 用户："+ this.userId + " 断开连接...");
        } catch (Exception e) {
            log.error("---------------WebSocket断开异常---------------");
        }
    }

    @OnMessage
    public void onMessage(@PathParam("myUserId") String userId, String body){
        try {
            //将Body解析
            JSONObject jsonObject = JSONObject.parseObject(body);
            //获取目标用户地址
            String targetUserId = jsonObject.getString("targetUserId");
            //获取需要发送的消息
            String message = jsonObject.getString("message");
            jsonObject.put("userId" , userId);
            if(userId.equals(targetUserId)){
                sendMoreMessage(new String[]{targetUserId} ,  JSONObject.toJSONString(jsonObject));
            }else{
                sendMoreMessage(new String[]{userId , targetUserId} ,  JSONObject.toJSONString(jsonObject));
            }
        } catch (Exception e) {
            log.error("---------------WebSocket消息异常---------------");
        }
    }


    /**
     * 此为广播消息
     * @param message
     */
    public void sendAllMessage(String message) {
        log.info("【websocket消息】广播消息:"+message);
        for(ChatServer webSocket : webSockets) {
            try {
                if(webSocket.session.isOpen()) {
                    webSocket.session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                log.error("---------------WebSocket消息广播异常---------------");
            }
        }
    }

    /**
     * 单点消息
     * @param userId
     * @param message
     */
    public void sendOneMessage(String userId, String message) {
        Session session = sessionPool.get(userId);
        if (session != null&&session.isOpen()) {
            try {
                log.info("【websocket消息】 单点消息:"+message);
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                log.error("---------------WebSocket单点消息发送异常---------------");
            }
        }
    }

    /**
     * 发送多人单点消息
     * @param userIds
     * @param message
     */
    public void sendMoreMessage(String[] userIds, String message) {
        for(String userId:userIds) {
            Session session = sessionPool.get(userId);
            if (session != null&&session.isOpen()) {
                try {
                    log.info("【websocket消息】 单点消息:"+message);
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    log.error("---------------WebSocket多人单点消息发送异常---------------");
                }
            }
        }
    }
}

