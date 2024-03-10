package com.studio314.d_emo.mapper;

import com.studio314.d_emo.pojo.VCode;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VCodeMapper {

    @Insert("insert into ValidateCode(mail, code) VALUES(#{mail}, #{code})")
    public void insert(String mail, String code);

    @Select("select * from ValidateCode where mail = #{mail} and code = #{code}")
    public List<VCode> getCode(String mail, String code);
}
