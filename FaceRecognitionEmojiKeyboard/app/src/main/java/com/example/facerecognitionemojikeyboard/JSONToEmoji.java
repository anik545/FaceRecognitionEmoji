package com.example.facerecognitionemojikeyboard;

import com.example.facerecognitionemojikeyboard.Emotion;

import java.util.Map;

public class JSONToEmoji {

    public static String getEmoji(Map<Emotion, Double> scores) {
        Emotion maxEmotion = findMaxEmotion(scores);
        switch(maxEmotion) {
            case ANGER:
                return "1F621";
            case CONTEMPT:
                return "1F60F";
            case DISGUST:
                return "1F92E";
            case FEAR:
                return "1F631";
            case HAPPINESS:
                return "1F630";
            case NEUTRAL:
                return "1F610";
            case SADNESS:
                return "1F622";
            case SURPRISE:
                return "1F632";
        }
        return "";
    }

    private static Emotion findMaxEmotion(Map<Emotion, Double> scores) {
        Emotion maxEmotion = Emotion.ANGER;
        double maxScore = scores.get(Emotion.ANGER);
        for (Emotion emotion : scores.keySet()) {
            if (scores.get(emotion) > maxScore) {
                maxScore = scores.get(emotion);
                maxEmotion = emotion;
            }
        }
        return maxEmotion;
    }



}
