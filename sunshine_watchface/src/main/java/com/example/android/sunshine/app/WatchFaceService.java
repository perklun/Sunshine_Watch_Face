package com.example.android.sunshine.app;

import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class WatchFaceService extends WearableListenerService {
    private static final String TAG = "LIST";

    /**
    private UpdateUIListener updateUIListener;

    public WatchFaceService(){
        this.updateUIListener = null;
    }

    public void setUpdateUIListener(UpdateUIListener updateUIListener) {
        this.updateUIListener = updateUIListener;
    }
    **/
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
    /**
    public interface UpdateUIListener{
        public void updateUI();
    }
     **/

}
