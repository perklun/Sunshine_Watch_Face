package com.example.android.sunshine.app;

import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * This is an example of a wearable listener service but it is current not in use
 * The DataApiListener is used in SunshineWatchFace instead
 */
public class WatchFaceService extends WearableListenerService {
    private static final String TAG = "LIST";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent dataEvent : dataEvents) {
            DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
            String path = dataEvent.getDataItem().getUri().getPath();
            if(path.equals("/weather_data")){
                String high = dataMap.getString("MAX");
                String low = dataMap.getString("MIN");
                Log.i(TAG, "Updating on watch: " + high + " " + low);
            }
        }
    }


}
