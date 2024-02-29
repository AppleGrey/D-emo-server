package com.studio314.d_emo.Other;

import com.studio314.d_emo.pojo.TreeHoleCard;

import java.util.List;

//出现最多的情绪和次数
public class Statistic {
    List<TreeHoleCard> treeHoleCards;
    List<Integer> count;

    public List<TreeHoleCard> getTreeHoleCards() {
        return treeHoleCards;
    }

    public void setTreeHoleCards(List<TreeHoleCard> treeHoleCards) {
        this.treeHoleCards = treeHoleCards;
    }

    public List<Integer> getCount() {
        return count;
    }

    public void setCount(List<Integer> count) {
        this.count = count;
    }
}
