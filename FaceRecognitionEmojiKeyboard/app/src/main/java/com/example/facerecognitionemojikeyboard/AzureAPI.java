package com.example.facerecognitionemojikeyboard;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONObject;

public class AzureAPI {

    private final String apiEndpoint =
            "https://westeurope.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceId=true" +
                    "&returnFaceLandmarks=false&returnFaceAttributes=emotion";
    private String apiKey;

    AzureAPI() {

        // Read in API keys
        String rawApiKeys = JSONParser.loadJSONFromAsset(SimpleIME.getAppContext(),
                "apiKeys.json");
        JSONObject apiKeys;
        try {
            apiKeys = new JSONObject(rawApiKeys);
            apiKey = apiKeys.getString("faceApi");
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }

//        Log.d("AZUREAPI", "Api Key: " + apiKey);
    }

    public void sendBitmap(Bitmap bitmap) {
        
    }

}
