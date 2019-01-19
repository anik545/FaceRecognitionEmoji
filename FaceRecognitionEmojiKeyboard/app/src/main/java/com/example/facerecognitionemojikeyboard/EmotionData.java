package com.example.facerecognitionemojikeyboard;

import org.json.JSONObject;

import java.util.HashMap;

public class EmotionData {

    public double anger;
    public double contempt;
    public double disgust;
    public double fear;
    public double happiness;
    public double neutral;
    public double sadness;
    public double surprise;

    EmotionData(JSONObject rawData) {
        // TODO: Implement
    }

    HashMap<Emotion, Double> exportMap() {
        HashMap<Emotion, Double> mapData = new HashMap<>();
        mapData.put(Emotion.ANGER, anger);
        mapData.put(Emotion.CONTEMPT, contempt);
        mapData.put(Emotion.DISGUST, disgust);
        mapData.put(Emotion.FEAR, fear);
        mapData.put(Emotion.HAPPINESS, happiness);
        mapData.put(Emotion.NEUTRAL, neutral);
        mapData.put(Emotion.SADNESS, sadness);
        mapData.put(Emotion.SURPRISE, surprise);

        return mapData;
    }

}
