package com.studio314.d_emo;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class TCPClient {

    private static boolean firstConnected = true;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Socket socket = new Socket("192.168.10.51", 12345);//创建Socket对象
        System.out.println("连接成功");
        //输出流
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        //写入
        while(true) {
            String message = scanner.nextLine();
            //将消息封装成json格式
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", message);
            jsonObject.put("ID", 1);
            jsonObject.put("type", "text");
            jsonObject.put("firstConnect", firstConnected);

            if(socket.isClosed()) {
                socket = new Socket("192.168.10.51", 12345);
                bos = new BufferedOutputStream(socket.getOutputStream());
                System.out.println("重新连接成功");
            }
            try {
                System.out.println(jsonObject.toJSONString());
                bos.write(jsonObject.toJSONString().getBytes());
                firstConnected = false;
                bos.flush();
            } catch(SocketException e) {
                System.out.println("服务器已断开");
                //将json中的firstConnect改为true
                jsonObject.put("firstConnect", true);
                socket = new Socket("192.168.10.51", 12345);
                bos = new BufferedOutputStream(socket.getOutputStream());
                System.out.println("重新连接成功");
                bos.write(jsonObject.toJSONString().getBytes());
                firstConnected = false;
                bos.flush();
            }

            System.out.println("写入成功");
//            socket.shutdownOutput();
//        读取服务器上的响应数据
            BufferedInputStream bis1 = new BufferedInputStream(socket.getInputStream());
            byte[] data = new byte[10240];
            int len = bis1.read(data);
            String str = new String(data, 0, len);
            //解析str
            JSONObject rec = JSONObject.parseObject(str);
            String text = rec.getString("data");
            //text为utf-8字符，解码
            String text1 = new String(text.getBytes("utf-8"), "utf-8");
            System.out.println("收到服务器的消息：" + text1);
        }

    }
}
