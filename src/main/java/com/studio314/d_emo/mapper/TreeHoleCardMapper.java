package com.studio314.d_emo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studio314.d_emo.pojo.TreeHoleCard;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface TreeHoleCardMapper extends BaseMapper<TreeHoleCard> {
    @Options(keyProperty = "cardID", useGeneratedKeys = true)
    @Insert("insert into treeHoleCard(imageURL, text, emotionId) values (#{imageURL}, #{text}, #{emotionId})")
    TreeHoleCard insertTreeHoleCard(TreeHoleCard treeHoleCard);


}
