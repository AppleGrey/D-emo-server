package com.studio314.d_emo.utils;

import java.util.HashMap;
import java.util.Map;

public class PADAlgorithm {

    static Map<EmotionType, Integer> emojiMap = new HashMap<EmotionType, Integer>() {{
        put(EmotionType.JOY_HIGH, 2131230905);
        put(EmotionType.JOY_MEDIUM, 2131230918);
        put(EmotionType.JOY_LOW, 2131230913);
        put(EmotionType.DISTRESS_HIGH, 2131230915);
        put(EmotionType.DISTRESS_MEDIUM, 2131230932);
        put(EmotionType.DISTRESS_LOW, 2131230906);
        put(EmotionType.CALM_HIGH, 2131230913);
        put(EmotionType.CALM_MEDIUM, 2131230911);
        put(EmotionType.CALM_LOW, 2131230934);
        put(EmotionType.ANGER_HIGH, 2131230916);
        put(EmotionType.ANGER_MEDIUM, 2131230906);
        put(EmotionType.ANGER_LOW, 2131230919);
        put(EmotionType.FEAR_HIGH, 2131230910);
        put(EmotionType.FEAR_MEDIUM, 2131230908);
        put(EmotionType.FEAR_LOW, 2131230912);
        put(EmotionType.SADNESS_HIGH, 2131230921);
        put(EmotionType.SADNESS_MEDIUM, 2131230920);
        put(EmotionType.SADNESS_LOW, 2131230909);
        put(EmotionType.VIGOR_HIGH, 2131230933);
        put(EmotionType.VIGOR_MEDIUM, 2131230914);
        put(EmotionType.VIGOR_LOW, 2131230931);
        put(EmotionType.SLEEPINESS_HIGH, 2131230932);
        put(EmotionType.SLEEPINESS_MEDIUM, 2131230908);
        put(EmotionType.SLEEPINESS_LOW, 2131230929);

    }};


    public enum EmotionType {
        JOY_HIGH, JOY_MEDIUM, JOY_LOW,
        DISTRESS_HIGH, DISTRESS_MEDIUM, DISTRESS_LOW,
        CALM_HIGH, CALM_MEDIUM, CALM_LOW,
        ANGER_HIGH, ANGER_MEDIUM, ANGER_LOW,
        FEAR_HIGH, FEAR_MEDIUM, FEAR_LOW,
        SADNESS_HIGH, SADNESS_MEDIUM, SADNESS_LOW,
        VIGOR_HIGH, VIGOR_MEDIUM, VIGOR_LOW,
        SLEEPINESS_HIGH, SLEEPINESS_MEDIUM, SLEEPINESS_LOW
    }

    public static int getEmoji(EmotionType emotion) {
        return emojiMap.get(emotion);
    }

    public static String getEmotionString(EmotionType emotion) {
        return switch (emotion) {
            case JOY_HIGH -> "高兴";
            case JOY_MEDIUM -> "愉快";
            case JOY_LOW -> "开心";
            case DISTRESS_HIGH -> "大哭";
            case DISTRESS_MEDIUM -> "焦虑";
            case DISTRESS_LOW -> "烦躁";
            case CALM_HIGH -> "平静";
            case CALM_MEDIUM -> "轻松";
            case CALM_LOW -> "安逸";
            case ANGER_HIGH -> "愤怒";
            case ANGER_MEDIUM -> "生气";
            case ANGER_LOW -> "不满";
            case FEAR_HIGH -> "恐惧";
            case FEAR_MEDIUM -> "害怕";
            case FEAR_LOW -> "担忧";
            case SADNESS_HIGH -> "悲伤";
            case SADNESS_MEDIUM -> "失落";
            case SADNESS_LOW -> "沮丧";
            case VIGOR_HIGH -> "活力";
            case VIGOR_MEDIUM -> "兴奋";
            case VIGOR_LOW -> "调皮";
            case SLEEPINESS_HIGH -> "无聊";
            case SLEEPINESS_MEDIUM -> "困倦";
            case SLEEPINESS_LOW -> "疲劳";
        };
    }

    public static EmotionType getEmotion(double pleasure, double stress, double heartRate, double sleepScore) {
        double arousal = normalize(mapToArousal(stress, heartRate));
        double dominance = normalize(mapToDominance(sleepScore));
        pleasure = normalize(pleasure);
        EmotionType emotionType = analyzeEmotion(pleasure, arousal, dominance);
        return emotionType;
    }

    public static EmotionType getEmotionWithAI(double pleasure, double stress, double heartRate, double sleepScore, double a_AI, double d_AI) {
        double arousal = normalize(mapToArousal(stress, heartRate));
        double dominance = normalize(mapToDominance(sleepScore));
        pleasure = normalize(pleasure);
        arousal = normalize(a_AI) * 0.5 + arousal * 0.5;
        dominance = normalize(d_AI) * 0.5 + dominance * 0.5;
        System.out.println("计算的pleasure: " + pleasure + " arousal: " + arousal + " dominance: " + dominance);
        EmotionType emotionType = analyzeEmotion(pleasure, arousal, dominance);
        return emotionType;
    }

    //将数字标准化
    public static double normalize(double value) {
        return value * 2 - 1;
    }

    public static EmotionType analyzeEmotion(double P, double A, double D) {

        EmotionType emotionType;
        //PAD三个值范围都是-1到1
        if (P >= 0 && A >= 0 && D >= 0) {
            double value = Math.abs(P) + Math.abs(A) + Math.abs(D);
            if (value >= 2) {
                emotionType = EmotionType.JOY_HIGH;
            } else if (value >= 1) {
                emotionType = EmotionType.JOY_MEDIUM;
            } else {
                emotionType = EmotionType.JOY_LOW;
            }
        } else if (P < 0 && A >= 0 && D >= 0) {
            double value = Math.abs(P) + Math.abs(A) + Math.abs(D);
            if (value >= 2) {
                emotionType = EmotionType.ANGER_HIGH;
            } else if (value >= 1) {
                emotionType = EmotionType.ANGER_MEDIUM;
            } else {
                emotionType = EmotionType.ANGER_LOW;
            }
        } else if (P >= 0 && A < 0 && D >= 0) {
            double value = Math.abs(P) + Math.abs(A) + Math.abs(D);
            if (value >= 2) {
                emotionType = EmotionType.CALM_HIGH;
            } else if (value >= 1) {
                emotionType = EmotionType.CALM_MEDIUM;
            } else {
                emotionType = EmotionType.CALM_LOW;
            }
        } else if (P < 0 && A < 0 && D >= 0) {
            double value = Math.abs(P) + Math.abs(A) + Math.abs(D);
            if (value >= 2) {
                emotionType = EmotionType.FEAR_HIGH;
            } else if (value >= 1) {
                emotionType = EmotionType.FEAR_MEDIUM;
            } else {
                emotionType = EmotionType.FEAR_LOW;
            }
        } else if (P >= 0 && A >= 0 && D < 0) {
            double value = Math.abs(P) + Math.abs(A) + Math.abs(D);
            if (value >= 2) {
                emotionType = EmotionType.VIGOR_HIGH;
            } else if (value >= 1) {
                emotionType = EmotionType.VIGOR_MEDIUM;
            } else {
                emotionType = EmotionType.VIGOR_LOW;
            }
        } else if (P < 0 && A >= 0 && D < 0) {
            double value = Math.abs(P) + Math.abs(A) + Math.abs(D);
            if (value >= 2) {
                emotionType = EmotionType.DISTRESS_HIGH;
            } else if (value >= 1) {
                emotionType = EmotionType.DISTRESS_MEDIUM;
            } else {
                emotionType = EmotionType.DISTRESS_LOW;
            }
        } else if (P >= 0 && A < 0 && D < 0) {
            double value = Math.abs(P) + Math.abs(A) + Math.abs(D);
            if (value >= 2) {
                emotionType = EmotionType.SLEEPINESS_HIGH;
            } else if (value >= 1) {
                emotionType = EmotionType.SLEEPINESS_MEDIUM;
            } else {
                emotionType = EmotionType.SLEEPINESS_LOW;
            }
        } else if (P < 0 && A < 0 && D < 0) {
            double value = Math.abs(P) + Math.abs(A) + Math.abs(D);
            if (value >= 2) {
                emotionType = EmotionType.SADNESS_HIGH;
            } else if (value >= 1) {
                emotionType = EmotionType.SADNESS_MEDIUM;
            } else {
                emotionType = EmotionType.SADNESS_LOW;
            }
        } else {
            emotionType = EmotionType.CALM_LOW;
        }
//        } else if (P > 0 && A < 0 && D > 0) {
//            return "calm";
//        } else if (P < 0 && A < 0 && D > 0) {
//            return "distress";
//        } else if (P > 0 && A > 0 && D < 0) {
//            return "vigor";
//        } else if (P < 0 && A > 0 && D < 0) {
//            return "fear";
//        } else if (P > 0 && A < 0 && D < 0) {
//            return "sleepness";
//        } else if (P < 0 && A < 0 && D < 0) {
//            return "sadness";
//        }

        return emotionType;
    }

    private static double mapToArousal(double stress, double heartRate) {
        double stressScore;
        if (stress >= 1 && stress <= 29) {
            stressScore = 0.2; // 放松
        } else if (stress >= 30 && stress <= 59) {
            stressScore = 0.67; // 正常
        } else if (stress >= 60 && stress <= 79) {
            stressScore = 0.7; // 中等
        } else if(stress > 79){
            stressScore = 0.9; // 偏高
        } else {
            stressScore = 0.5; // 未知
        }
        System.out.println("stressScore: " + stressScore);

        double heartRateScore;
        if (heartRate >= 50 && heartRate <= 90) {
            heartRateScore = 0.3; // 正常
        } else if (heartRate <= 50 && heartRate >= 10) {
            heartRateScore = 0.2; // 偏低
        } else if (heartRate >= 91 && heartRate <= 100) {
            heartRateScore = 0.67; // 偏高
        } else if (heartRate >= 101 && heartRate <= 110) {
            heartRateScore = 0.7; // 较高
        } else if (heartRate > 110) {
            heartRateScore = 0.9; // 过高
        } else {
            heartRateScore = 0.5; // 未知
        }
        System.out.println("heartRateScore: " + heartRateScore);
        //压力值占比0.1，心率占比0.9
        return 0.1 * stressScore + 0.9 * heartRateScore;
    }

    private static double mapToDominance(double sleepScore) {
        if(sleepScore < 0) {
            return 0.5;
        }
        if(sleepScore >= 0 && sleepScore <= 60) {
            return 0.2;
        } else if(sleepScore > 60 && sleepScore <= 70) {
            return 0.4;
        } else if(sleepScore > 70 && sleepScore <= 80) {
            return 0.6;
        } else if(sleepScore > 80 && sleepScore <= 90) {
            return 0.8;
        } else {
            return 1;
        }
    }

//    public static void main(String[] args) {
//        EmotionType emotionWithAI = getEmotionWithAI(0, -1, 0, 75, 0.7, 0.3);
//        System.out.println(emotionWithAI);
//    }
}

