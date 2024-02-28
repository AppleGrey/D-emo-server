package com.studio314.d_emo.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studio314.d_emo.mapper.SummaryMapper;
import com.studio314.d_emo.pojo.Summary;
import com.studio314.d_emo.server.SummaryServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryServerImpl implements SummaryServer {
    @Autowired
    private SummaryMapper summaryMapper;
    @Override
    public Summary getSummary(int userid) {
        // 取出该用户所有的 summary，按日期倒序排列，并限制返回结果数量为1
        LambdaQueryWrapper<Summary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Summary::getUserid, userid);
        wrapper.orderByDesc(Summary::getDate);
        wrapper.last("limit 1");
        return summaryMapper.selectOne(wrapper);
    }
}
