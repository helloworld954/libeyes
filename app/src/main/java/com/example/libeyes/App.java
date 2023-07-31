package com.example.libeyes;

import com.lib.eyes.AdsPool;
import com.lib.eyes.BuildConfig;
import com.lib.eyes.application.AdmobApplication;

public class App extends AdmobApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        AdsPool.INSTANCE.registerOpenAppAd(this, BuildConfig.openApp_main);
    }
}
