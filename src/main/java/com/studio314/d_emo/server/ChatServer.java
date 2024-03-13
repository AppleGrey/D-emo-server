package com.studio314.d_emo.server;

import com.alibaba.fastjson.JSONObject;
import com.studio314.d_emo.mapper.ChatsMapper;
import com.studio314.d_emo.mapper.TodoMapper;
import com.studio314.d_emo.pojo.Chats;
import com.studio314.d_emo.pojo.Todo;
import com.studio314.d_emo.server.impl.TodoServerImpl;
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
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
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

    public static Map<String, Integer> EMOTIONS = new LinkedHashMap<>();

    static {
        EMOTIONS.put("[高兴]", 2131230904);
        EMOTIONS.put("[生气]", 2131230915);
        EMOTIONS.put("[喜欢]", 2131230926);
        EMOTIONS.put("[失望]", 2131230928);
        EMOTIONS.put("[幸灾乐祸]", 2131230929);
        EMOTIONS.put("[调皮]", 2131230930);
        EMOTIONS.put("[折磨]", 2131230931);
        EMOTIONS.put("[感激]", 2131230932);
        EMOTIONS.put("[害羞]", 2131230933);
        EMOTIONS.put("[憎恨]", 2131230905);
        EMOTIONS.put("[痛苦]", 2131230906);
        EMOTIONS.put("[惊吓]", 2131230907);
        EMOTIONS.put("[害怕]", 2131230908);
        EMOTIONS.put("[懊悔]", 2131230909);
        EMOTIONS.put("[轻松]", 2131230910);
        EMOTIONS.put("[惊讶]", 2131230911);
        EMOTIONS.put("[希望]", 2131230912);
        EMOTIONS.put("[得意]", 2131230913);
        EMOTIONS.put("[同情]", 2131230914);
        EMOTIONS.put("[愤恨]", 2131230916);
        EMOTIONS.put("[可爱]", 2131230917);
        EMOTIONS.put("[责备]", 2131230918);
        EMOTIONS.put("[恐惧]", 2131230919);
        EMOTIONS.put("[厌恶]", 2131230920);
        EMOTIONS.put("[满足]", 2131230921);
        EMOTIONS.put("[傲慢]", 2131230922);
        EMOTIONS.put("[羞愧]", 2131230923);
        EMOTIONS.put("[爱]", 2131230924);
        EMOTIONS.put("[点赞]", 2131230925);
        EMOTIONS.put("[许愿]", 2131230927);
    }

    @Value("${ip.python" +
            "Server}") String setPythonServer(String pythonServer){
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

    @Autowired
    TodoMapper setTodoMapper(TodoMapper todoMapper){
        return ChatServer.todoMapper = todoMapper;
    }

    static TodoMapper todoMapper;

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
                String emotion = recJsonObject.getString("emotion");
                String operate = recJsonObject.getString("operate");
                JSONObject operateJsonObject = JSONObject.parseObject(operate);
                String operateType = operateJsonObject.getString("type");

                if (operateType.equals("todo")){
                    String date = operateJsonObject.getString("date");
                    String name = operateJsonObject.getString("name");
                    Todo todo = new Todo();
                    Timestamp timestamp = Timestamp.valueOf(date);
                    todo.setDate(timestamp);
                    todo.setName(name);
                    todo.setId(Integer.parseInt(userId));
                    todo.setIsfinished(0);
                    todoMapper.insert(todo);
                } else if (operateType.equals("diary")) {

                }
                //判断情绪是否在情绪列表中
                if (!EMOTIONS.containsKey(emotion)) {
                    emotion = "-1";
                }

                //text为utf-8字符，解码
                String recText = new String(recData.getBytes("utf-8"), "utf-8");
                log.info("【websocket消息】 用户："+ userId + " 接收消息："+recText);

                //将获取到的信息保存到数据库
                chatsMapper.insertChat(Integer.parseInt(userId), recText, 0, 1, null);
                //将获取到的消息发送给接收端
                JSONObject serverJsonObject = new JSONObject();
                serverJsonObject.put("type", 0);
                serverJsonObject.put("message", recText);
                sendMoreMessage(new String[]{targetUserId} ,  JSONObject.toJSONString(serverJsonObject));
                log.info("emotion:"+emotion);
                // 保存文本消息
                chatsMapper.insertChat(Integer.parseInt(userId), clientMessage, type, 0, emotion);


            } else if (type == 1) {

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
                String emotion = recJsonObject.getString("emotion");
                //text为utf-8字符，解码
                String recText = new String(recData.getBytes("utf-8"), "utf-8");
                log.info("【websocket消息】 用户："+ userId + " 接收消息："+recText);
                //判断情绪是否在情绪列表中
                if (!EMOTIONS.containsKey(emotion)) {
                    emotion = "-1";
                }
                //将获取到的信息保存到数据库
                chatsMapper.insertChat(Integer.parseInt(userId), recText, 0, 1, null);
                // 保存图片消息
                chatsMapper.insertChat(Integer.parseInt(userId), clientMessage, type, 0, emotion);
                //将获取到的消息发送给接收端
                JSONObject serverJsonObject = new JSONObject();
                serverJsonObject.put("type", 0);
                serverJsonObject.put("message", recText);
                sendMoreMessage(new String[]{targetUserId} ,  JSONObject.toJSONString(serverJsonObject));
            } else if (type == 2) {

                //将消息封装成json格式
                JSONObject messageJsonObject = new JSONObject();
                messageJsonObject.put("type", "voice");
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

                // 读取服务器上的响应数据
                BufferedInputStream bis1 = new BufferedInputStream(socket.getInputStream());
                byte[] data = new byte[10240];
                int len = bis1.read(data);
                String rec = new String(data,0,len);
                JSONObject recJsonObject = JSONObject.parseObject(rec);
                String recData = recJsonObject.getString("data");
                String emotion = recJsonObject.getString("emotion");
                //text为utf-8字符，解码
                String recText = new String(recData.getBytes("utf-8"), "utf-8");
                log.info("【websocket消息】 用户："+ userId + " 接收消息："+recText);

                //判断情绪是否在情绪列表中
                if (!EMOTIONS.containsKey(emotion)) {
                    emotion = "-1";
                }
                //将获取到的信息保存到数据库
                chatsMapper.insertChat(Integer.parseInt(userId), recText, 0, 1, null);
                // 保存图片消息
                chatsMapper.insertChat(Integer.parseInt(userId), clientMessage, type, 0, emotion);
                //将获取到的消息发送给接收端
                JSONObject serverJsonObject = new JSONObject();
                serverJsonObject.put("type", 0);
                serverJsonObject.put("message", recText);
                sendMoreMessage(new String[]{targetUserId} ,  JSONObject.toJSONString(serverJsonObject));
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

