package com.libeye.libeyes;

import com.lib.eyes.application.BaseApplication;
import com.libeye.admob.AdmobExtKt;
import com.libeye.admob.application.AdmobApplicationDelegate;
import com.lib.eyes.AdsPool;
import com.lib.eyes.BuildConfig;

import java.util.ArrayList;
import java.util.Arrays;

public class App extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        registerApplicationLifecycleCallback(
                new AdmobApplicationDelegate(
                        this,
                        Arrays.asList("fkldjkl")
                )
        );
        AdmobExtKt.registerOpenAppAd(
                AdsPool.INSTANCE,
                this,
                BuildConfig.openApp_main
        );
    }
}
