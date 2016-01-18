package com.example.android.sunshine.app.wearable;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.android.sunshine.app.Utility;
import com.example.android.sunshine.app.data.WeatherContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;

/**
 * Created by perklun on 1/17/2016.
 */
public class WearableIntentService extends IntentService implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "LIST";

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
    };
    // these indices must match the projection
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_MAX_TEMP = 2;
    private static final int INDEX_MIN_TEMP = 3;

    String formattedMaxTemperature;
    String formattedMinTemperature;
    int resourceID;

    private GoogleApiClient mGoogleApiClient;

    public WearableIntentService() {
        super("WearableIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Get today's data from the ContentProvider
        String location = Utility.getPreferredLocation(this);
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                location, System.currentTimeMillis());
        Cursor data = getContentResolver().query(weatherForLocationUri, FORECAST_COLUMNS, null,
                null, WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        // Extract the weather data from the Cursor
        int weatherId = data.getInt(INDEX_WEATHER_ID);
        double maxTemp = data.getDouble(INDEX_MAX_TEMP);
        double minTemp = data.getDouble(INDEX_MIN_TEMP);
        formattedMaxTemperature = Utility.formatTemperature(this, maxTemp);
        formattedMinTemperature = Utility.formatTemperature(this, minTemp);
        resourceID = Utility.getIconResourceForWeatherCondition(weatherId);
        data.close();
        sendNewWeatherData();
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public void sendNewWeatherData(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/weather_data").setUrgent();

        //Send image
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceID);
        Asset asset = createAssetFromBitmap(bitmap);
        putDataMapRequest.getDataMap().putAsset("IMAGE", asset);
        //Send temperature
        putDataMapRequest.getDataMap().putString("MAX", formattedMaxTemperature);
        putDataMapRequest.getDataMap().putString("MIN", formattedMinTemperature);
        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        Log.i(TAG, "Updating wearable app:" + formattedMaxTemperature + " " + formattedMinTemperature);
        Wearable.DataApi.putDataItem(mGoogleApiClient, request).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                Log.i(TAG, "Data sent...");
            }
        });
        mGoogleApiClient.disconnect();
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }
}
