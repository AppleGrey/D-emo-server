package com.studio314.d_emo.utils;

import java.util.HashMap;
import java.util.Map;

public class PADAlgorithm {

    static Map<EmotionType, Integer> emojiMap = new HashMap<EmotionType, Integer>() {{
        put(EmotionType.JOY_HIGH, 1);
        put(EmotionType.JOY_MEDIUM, 21);
        put(EmotionType.JOY_LOW, 17);
        put(EmotionType.DISTRESS_HIGH, 19);
        put(EmotionType.DISTRESS_MEDIUM, 11);
        put(EmotionType.DISTRESS_LOW, 27);
        put(EmotionType.CALM_HIGH, 17);
        put(EmotionType.CALM_MEDIUM, 8);
        put(EmotionType.CALM_LOW, 9);
        put(EmotionType.ANGER_HIGH, 2);
        put(EmotionType.ANGER_MEDIUM, 10);
        put(EmotionType.ANGER_LOW, 22);
        put(EmotionType.FEAR_HIGH, 24);
        put(EmotionType.FEAR_MEDIUM, 12);
        put(EmotionType.FEAR_LOW, 20);
        put(EmotionType.SADNESS_HIGH, 14);
        put(EmotionType.SADNESS_MEDIUM, 13);
        put(EmotionType.SADNESS_LOW, 23);
        put(EmotionType.VIGOR_HIGH, 15);
        put(EmotionType.VIGOR_MEDIUM, 18);
        put(EmotionType.VIGOR_LOW, 6);
        put(EmotionType.SLEEPINESS_HIGH, 7);
        put(EmotionType.SLEEPINESS_MEDIUM, 12);
        put(EmotionType.SLEEPINESS_LOW, 16);

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

    public static int getEmoji(double pleasure, double stress, double heartRate, double sleepScore) {
        double arousal = mapToArousal(stress, heartRate);
        double dominance = mapToDominance(sleepScore);
        EmotionType emotionType = analyzeEmotion(pleasure, arousal, dominance);
        return emojiMap.get(emotionType);
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

