package com.tigerobo.venturecapital.messageinterccept;

import android.app.Application;

import st.lowlevel.storo.StoroBuilder;

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StoroBuilder.configure(8192)  // maximum size to allocate in bytes
                .setDefaultCacheDirectory(this)
                .initialize();
    }
}
