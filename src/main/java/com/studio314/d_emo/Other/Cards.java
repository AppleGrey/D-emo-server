package com.studio314.d_emo.Other;


import com.studio314.d_emo.pojo.TreeHoleCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cards{
    public List<TreeHoleCard> treeHoleCards;
    public Map<Integer,String> usernames;

    public Cards(){
        treeHoleCards = new ArrayList<>();
        usernames = new HashMap<>();
    }
}
