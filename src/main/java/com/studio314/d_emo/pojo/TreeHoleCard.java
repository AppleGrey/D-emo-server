package com.studio314.d_emo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
    @TableName("treeHoleCard")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
public class TreeHoleCard {
    @TableId(type = IdType.AUTO, value = "cardID")
    int cardID;
    @TableField("imageURL")
    String imageURL;
    String date;
    String text;
    @TableField("emotionId")
    int emotionId;
    @TableField("userId")
    int userId;
    @TableField("isPersonal")
    int isPersonal;
    String comment;
}
