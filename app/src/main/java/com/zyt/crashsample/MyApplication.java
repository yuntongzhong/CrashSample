package com.zyt.crashsample;

import android.app.Application;

import com.zyt.crashlistener.CrashErrorUtils;
import com.zyt.crashlistener.CrashWatchDog;

/**
 * Created by Administrator on 2016/7/14.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        watchCrash();
    }
    /**
     * 监听Crash.
     */
    private void watchCrash() {
        CrashWatchDog watchDog = new CrashWatchDog();
        // watchDog.setErrorActivity(CrashErrorActivity.class);
        watchDog.watch(this, new CrashWatchDog.Callback() {
            @Override
            public void handle(Throwable ex) {

                try {
                    String path=getExternalFilesDir("/crash-log/").getAbsolutePath();
                    new CrashErrorUtils(path).saveCrashInfoFile(ex);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
