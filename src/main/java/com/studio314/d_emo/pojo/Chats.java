package com.studio314.d_emo.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chats {
    @TableField("senderID")
    int senderID;
    @TableField("sendTime")
    String sendTime;
    String message;
    int type;
    @TableId(type = IdType.AUTO, value = "messageID")
    int messageID;
    @TableField("isReceiver")
    int isReceiver;
}
