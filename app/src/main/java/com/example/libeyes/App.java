package com.example.libeyes;

import com.example.wireframe.application.ApplicationDelegation;
import com.lib.eyes.AdsPool;
import com.lib.eyes.BuildConfig;
import com.lib.eyes.application.AdmobApplication;
import com.lib.eyes.application.AdmobApplicationKt;

import java.util.ArrayList;
import java.util.Arrays;

public class App extends AdmobApplication {
    private final ApplicationDelegation delegation =
            AdmobApplicationKt.createDelegation(this, new ArrayList<>(
                    Arrays.asList()
            ));

    @Override
    public void onCreate() {
        super.onCreate();
        delegation.onCreate();
        AdsPool.INSTANCE.registerOpenAppAd(this, BuildConfig.openApp_main);
    }
}
