package com.zyt.crashsample;

import android.app.Application;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.zyt.crashlistener.CrashErrorUtils;
import com.zyt.crashlistener.CrashWatchDog;

/**
 * Created by Administrator on 2016/7/14.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        CrashReport.initCrashReport(getApplicationContext(), "900046780", false);
        Bugly.init(getApplicationContext(), "900046780", false);
        //watchCrash();
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
