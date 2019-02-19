package com.lemonxq_laplace.pregnantmonitor.nearby_location;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

import org.litepal.LitePalApplication;

public class App extends Application {

    public static LocationService locationService;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        LitePalApplication.initialize(this);
        locationService = new LocationService(getApplicationContext());
    }
}
