package com.example.wil_project.app_utilities;

import android.app.Application;

import com.backendless.Backendless;

public class ApplicationClass extends Application {

    public static final String APPLICATION_ID = "FD83BCFC-EBBD-E8F5-FF55-5925171AF400";
    public static final String API_KEY = "C86343FC-EF81-49CF-9120-5658A39CF76D";
    public static int HitchhikerId;
    public static int remoteCheck;

    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.initApp( getApplicationContext(),
                APPLICATION_ID,
                API_KEY );
    }
}
