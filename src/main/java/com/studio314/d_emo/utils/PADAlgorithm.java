package com.studio314.d_emo.utils;

import java.util.HashMap;
import java.util.Map;

public class PADAlgorithm {

    static Map<EmotionType, Integer> emojiMap = new HashMap<EmotionType, Integer>() {{
        put(EmotionType.JOY_HIGH, 2131230905);
        put(EmotionType.JOY_MEDIUM, 2131230918);
        put(EmotionType.JOY_LOW, 2131230913);
        put(EmotionType.DISTRESS_HIGH, 2131230915);
        put(EmotionType.DISTRESS_MEDIUM, 2131230907);
        put(EmotionType.DISTRESS_LOW, 2131230924);
        put(EmotionType.CALM_HIGH, 2131230913);
        put(EmotionType.CALM_MEDIUM, 2131230933);
        put(EmotionType.CALM_LOW, 2131230934);
        put(EmotionType.ANGER_HIGH, 2131230916);
        put(EmotionType.ANGER_MEDIUM, 2131230906);
        put(EmotionType.ANGER_LOW, 2131230919);
        put(EmotionType.FEAR_HIGH, 2131230921);
        put(EmotionType.FEAR_MEDIUM, 2131230908);
        put(EmotionType.FEAR_LOW, 2131230917);
        put(EmotionType.SADNESS_HIGH, 2131230910);
        put(EmotionType.SADNESS_MEDIUM, 2131230909);
        put(EmotionType.SADNESS_LOW, 2131230920);
        put(EmotionType.VIGOR_HIGH, 2131230911);
        put(EmotionType.VIGOR_MEDIUM, 2131230914);
        put(EmotionType.VIGOR_LOW, 2131230931);
        put(EmotionType.SLEEPINESS_HIGH, 2131230932);
        put(EmotionType.SLEEPINESS_MEDIUM, 2131230908);
        put(EmotionType.SLEEPINESS_LOW, 2131230912);

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
            case DISTRESS_HIGH -> "痛苦";
            case DISTRESS_MEDIUM -> "焦虑";
            case DISTRESS_LOW -> "烦躁";
            case CALM_HIGH -> "平静";
            case CALM_MEDIUM -> "轻松";
            case CALM_LOW -> "安逸";
            case ANGER_HIGH -> "愤怒";
            case ANGER_MEDIUM -> "生气";
            case ANGER_LOW -> "不满";
            case FEAR_HIGH -> "恐惧";
            case FEAR_MEDIUM -> "担忧";
            case FEAR_LOW -> "害怕";
            case SADNESS_HIGH -> "悲伤";
            case SADNESS_MEDIUM -> "失落";
            case SADNESS_LOW -> "沮丧";
            case VIGOR_HIGH -> "活力";
            case VIGOR_MEDIUM -> "兴奋";
            case VIGOR_LOW -> "疲惫";
            case SLEEPINESS_HIGH -> "困倦";
            case SLEEPINESS_MEDIUM -> "昏昏欲睡";
            case SLEEPINESS_LOW -> "疲劳";
        };
    }

    public static EmotionType getEmotion(double pleasure, double stress, double heartRate, double sleepScore) {
        double arousal = mapToArousal(stress, heartRate);
        double dominance = mapToDominance(sleepScore);
        EmotionType emotionType = analyzeEmotion(pleasure, arousal, dominance);
        return emotionType;
    }

    public static EmotionType analyzeEmotion(double pleasure, double arousal, double dominance) {

        EmotionType emotionType;

        if (pleasure > 0.67) {
            if (arousal > 0.67) {
                if (dominance > 0.67) {
                    emotionType = EmotionType.JOY_HIGH;
                } else if (dominance >= 0.33) {
                    emotionType = EmotionType.JOY_MEDIUM;
                } else {
                    emotionType = EmotionType.JOY_LOW;
                }
            } else if (arousal >= 0.33) {
                if (dominance > 0.67) {
                    emotionType = EmotionType.CALM_HIGH;
                } else if (dominance >= 0.33) {
                    emotionType = EmotionType.CALM_MEDIUM;
                } else {
                    emotionType = EmotionType.CALM_LOW;
                }
            } else {
                if (dominance > 0.67) {
                    emotionType = EmotionType.SLEEPINESS_HIGH;
                } else if (dominance >= 0.33) {
                    emotionType = EmotionType.SLEEPINESS_MEDIUM;
                } else {
                    emotionType = EmotionType.SLEEPINESS_LOW;
                }
            }
        } else if (pleasure >= 0.33) {
            if (arousal > 0.67) {
                if (dominance > 0.67) {
                    emotionType = EmotionType.DISTRESS_HIGH;
                } else if (dominance >= 0.33) {
                    emotionType = EmotionType.DISTRESS_MEDIUM;
                } else {
                    emotionType = EmotionType.DISTRESS_LOW;
                }
            } else if (arousal >= 0.33) {
                if (dominance > 0.67) {
                    emotionType = EmotionType.SADNESS_HIGH;
                } else if (dominance >= 0.33) {
                    emotionType = EmotionType.SADNESS_MEDIUM;
                } else {
                    emotionType = EmotionType.SADNESS_LOW;
                }
            } else {
                if (dominance > 0.67) {
                    emotionType = EmotionType.FEAR_HIGH;
                } else if (dominance >= 0.33) {
                    emotionType = EmotionType.FEAR_MEDIUM;
                } else {
                    emotionType = EmotionType.FEAR_LOW;
                }
            }
        } else {
            if (arousal > 0.67) {
                if (dominance > 0.67) {
                    emotionType = EmotionType.ANGER_HIGH;
                } else if (dominance >= 0.33) {
                    emotionType = EmotionType.ANGER_MEDIUM;
                } else {
                    emotionType = EmotionType.ANGER_LOW;
                }
            } else if (arousal >= 0.33) {
                if (dominance > 0.67) {
                    emotionType = EmotionType.VIGOR_HIGH;
                } else if (dominance >= 0.33) {
                    emotionType = EmotionType.VIGOR_MEDIUM;
                } else {
                    emotionType = EmotionType.VIGOR_LOW;
                }
            } else {
                if (dominance > 0.67) {
                    emotionType = EmotionType.SADNESS_HIGH;
                } else if (dominance >= 0.33) {
                    emotionType = EmotionType.SADNESS_MEDIUM;
                } else {
                    emotionType = EmotionType.SADNESS_LOW;
                }
            }
        }

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
        } else {
            stressScore = 0.9; // 偏高
        }

        double heartRateScore;
        if (heartRate >= 50 && heartRate <= 90) {
            heartRateScore = 0.3; // 正常
        } else if (heartRate <= 50) {
            heartRateScore = 0.2; // 偏低
        } else if (heartRate >= 91 && heartRate <= 100) {
            heartRateScore = 0.67; // 偏高
        } else if (heartRate >= 101 && heartRate <= 110) {
            heartRateScore = 0.7; // 较高
        } else {
            heartRateScore = 0.9; // 过高
        }

        //压力值占比0.1，心率占比0.9
        return 0.1 * stressScore + 0.9 * heartRateScore;
    }

    private static double mapToDominance(double sleepScore) {
        return sleepScore / 100;
    }

}

