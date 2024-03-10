package com.studio314.d_emo.server;

import com.alibaba.fastjson.JSONObject;
import com.studio314.d_emo.mapper.ChatsMapper;
import com.studio314.d_emo.pojo.Chats;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;
import java.net.SocketException;
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

    private static String pythonServer;

    @Value("${ip.pythonServer}") String setPythonServer(String pythonServer){
        return ChatServer.pythonServer = pythonServer;
    }

    /**
     * 是否第一次连接 ai 服务器
     */
    private boolean firstConnected = true;

    private Socket socket;

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

    @Autowired ChatsMapper setChatsMapper(ChatsMapper chatsMapper){
        return ChatServer.chatsMapper = chatsMapper;
    }

    static ChatsMapper chatsMapper;

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

//            socket = new Socket(pythonServer, 12345);
//            log.info("【websocket消息】 用户：" + userId + " 连接服务器成功...");
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
            if (socket == null) {
                socket = new Socket(pythonServer, 12345);
                log.info("【websocket消息】 用户：" + userId + " 连接服务器成功...");
            }
            //将Body解析
            JSONObject clientJsonObject = JSONObject.parseObject(body);
            //获取目标用户地址
            String targetUserId = clientJsonObject.getString("userId");
            //获取消息内容
            String clientMessage = clientJsonObject.getString("content");
            int audioTime = clientJsonObject.getIntValue("time");
            //获取消息类型
            int type =  clientJsonObject.getIntValue("type");
            if (type == 0){
                // 保存文本消息
                chatsMapper.insertChat(Integer.parseInt(userId), clientMessage, type, 0);
                //将消息封装成json格式
                JSONObject messageJsonObject = new JSONObject();
                messageJsonObject.put("type", "text");
                messageJsonObject.put("ID", Integer.parseInt(userId));
                messageJsonObject.put("data", clientMessage);
                messageJsonObject.put("firstConnect", firstConnected);

                //socket将clientMessage发送给服务器
                BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
                try {
                    bos.write(messageJsonObject.toJSONString().getBytes());
                    firstConnected = false;
                    bos.flush();
                } catch(SocketException e) {
                    log.info("服务器已断开");
                    socket = new Socket(pythonServer, 12345);
                    bos = new BufferedOutputStream(socket.getOutputStream());
                    log.info("重新连接成功");
                    messageJsonObject.put("firstConnect", true);
                    bos.write(messageJsonObject.toJSONString().getBytes());
                    firstConnected = false;
                    bos.flush();
                }
//                socket.shutdownOutput();

                // 读取服务器上的响应数据
                BufferedInputStream bis1 = new BufferedInputStream(socket.getInputStream());
                byte[] data = new byte[10240];
                int len = bis1.read(data);
                String rec = new String(data,0,len);
                JSONObject recJsonObject = JSONObject.parseObject(rec);
                String recData = recJsonObject.getString("data");
                //text为utf-8字符，解码
                String recText = new String(recData.getBytes("utf-8"), "utf-8");
                log.info("【websocket消息】 用户："+ userId + " 接收消息："+recText);

                //将获取到的信息保存到数据库
                chatsMapper.insertChat(Integer.parseInt(userId), recText, 0, 1);
                //将获取到的消息发送给接收端
                JSONObject serverJsonObject = new JSONObject();
                serverJsonObject.put("type", 0);
                serverJsonObject.put("message", recText);
                sendMoreMessage(new String[]{targetUserId} ,  JSONObject.toJSONString(serverJsonObject));

            } else if (type == 1) {
                // 保存图片消息
                chatsMapper.insertChat(Integer.parseInt(userId), clientMessage, type, 0);
                //将消息封装成json格式
                JSONObject messageJsonObject = new JSONObject();
                messageJsonObject.put("type", "img");
                messageJsonObject.put("ID", Integer.parseInt(userId));
                messageJsonObject.put("data", clientMessage);
                messageJsonObject.put("firstConnect", firstConnected);

                //socket将clientMessage发送给服务器
                BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
                try {
                    bos.write(messageJsonObject.toJSONString().getBytes());
                    firstConnected = false;
                    bos.flush();
                } catch(SocketException e) {
                    System.out.println("服务器已断开");
                    socket = new Socket(pythonServer, 12345);
                    bos = new BufferedOutputStream(socket.getOutputStream());
                    System.out.println("重新连接成功");
                    messageJsonObject.put("firstConnect", true);
                    bos.write(messageJsonObject.toJSONString().getBytes());
                    firstConnected = false;
                    bos.flush();
                }
//                socket.shutdownOutput();

                // 读取服务器上的响应数据
                BufferedInputStream bis1 = new BufferedInputStream(socket.getInputStream());
                byte[] data = new byte[10240];
                int len = bis1.read(data);
                String rec = new String(data,0,len);
                JSONObject recJsonObject = JSONObject.parseObject(rec);
                String recData = recJsonObject.getString("data");
                //text为utf-8字符，解码
                String recText = new String(recData.getBytes("utf-8"), "utf-8");
                log.info("【websocket消息】 用户："+ userId + " 接收消息："+recText);

                //将获取到的信息保存到数据库
                chatsMapper.insertChat(Integer.parseInt(userId), recText, 0, 1);
                //将获取到的消息发送给接收端
                JSONObject serverJsonObject = new JSONObject();
                serverJsonObject.put("type", 0);
                serverJsonObject.put("message", recText);
                sendMoreMessage(new String[]{targetUserId} ,  JSONObject.toJSONString(serverJsonObject));
            } else if (type == 2) {
                // 保存音频消息
                chatsMapper.insertChatWithAudio(Integer.parseInt(userId), clientMessage, type, 0, audioTime);
            }


        } catch (Exception e) {
            log.error("---------------WebSocket消息异常---------------");
            e.printStackTrace();
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
                e.printStackTrace();
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

