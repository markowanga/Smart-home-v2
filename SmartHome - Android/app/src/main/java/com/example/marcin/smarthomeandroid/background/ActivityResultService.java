package com.example.marcin.smarthomeandroid.background;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ActivityResultService extends IntentService {

    public static final String REFRESH_ACTIONS = "com.example.marcin.otwieraniebramy.action.REFRESH_ACTIONS";

    public ActivityResultService() {
        super("ActivityResultService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("ActivityResultService", "running");
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result.getProbableActivities());
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        Intent broadcastIntent = new Intent(REFRESH_ACTIONS);

        Map<String, Integer> map = new TreeMap<>();
        for (int actInd = 0; actInd < 9; actInd++)
            if (actInd != 6)
                map.put(DetectedActivity.zzkf(actInd), 0);
                // todo: sprawdzić czy ok bo zmieniałem metodę

        for (DetectedActivity activity : probableActivities) {
            switch (activity.getType()) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e("ActivityRecogition", "In Vehicle: " + activity.getConfidence());
                    broadcastIntent.putExtra("IN_VEHICLE", activity.getConfidence());
                    map.put("IN_VEHICLE", activity.getConfidence());
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e("ActivityRecogition", "On Bicycle: " + activity.getConfidence());
                    broadcastIntent.putExtra("ON_BICYCLE", activity.getConfidence());
                    map.put("ON_BICYCLE", activity.getConfidence());
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e("ActivityRecogition", "On Foot: " + activity.getConfidence());
                    broadcastIntent.putExtra("ON_FOOT", activity.getConfidence());
                    map.put("ON_FOOT", activity.getConfidence());
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e("ActivityRecogition", "Running: " + activity.getConfidence());
                    broadcastIntent.putExtra("RUNNING", activity.getConfidence());
                    map.put("RUNNING", activity.getConfidence());
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e("ActivityRecogition", "Still: " + activity.getConfidence());
                    broadcastIntent.putExtra("STILL", activity.getConfidence());
                    map.put("STILL", activity.getConfidence());
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e("ActivityRecogition", "Tilting: " + activity.getConfidence());
                    broadcastIntent.putExtra("TILTING", activity.getConfidence());
                    map.put("TILTING", activity.getConfidence());
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e("ActivityRecogition", "Walking: " + activity.getConfidence());
                    broadcastIntent.putExtra("WALKING", activity.getConfidence());
                    map.put("WALKING", activity.getConfidence());
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e("ActivityRecogition", "Unknown: " + activity.getConfidence());
                    broadcastIntent.putExtra("UNKNOWN", activity.getConfidence());
                    map.put("UNKNOWN", activity.getConfidence());
                    break;
                }
            }
        }

        MyService.getResponseFromActivityResultService(map);
        //sendBroadcast(broadcastIntent);
    }
}