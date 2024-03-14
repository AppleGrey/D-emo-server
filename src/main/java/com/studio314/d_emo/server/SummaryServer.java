package com.studio314.d_emo.server;

import com.studio314.d_emo.pojo.SleepCard;

import java.io.IOException;
import java.util.List;

public interface SummaryServer {
//    Summary getSummary(int userid);

    String getSummary(List<SleepCard> sleepCards) throws IOException;
}
