package com.zyt.crashlistener;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;



import java.io.Serializable;

/**
 * 用于监听未捕获的异常消息
 * @author scj
 */
public class CrashWatchDog {

    private static final String EXTRA_RESTART_ACTIVITY_CLASS = "com.kaicom.log.crash.CrashWatchDog.EXTRA_RESTART_ACTIVITY_CLASS";
    public static final String EXTRA_STACK_TRACE_MESSAGE = "com.kaicom.log.crash.CrashWatchDog.EXTRA_STACK_TRACE_MESSAGE";

    private Application application;
    private Class<? extends Activity> mRestartActivity;
    private Class<? extends Activity> mErrorActivity;

    private Callback callback;

    /**
     * 监听crash.
     */
    public void watch(Application application) {
        watch(application, null);
    }

    public void watch(Application application, Callback callback) {
        this.application = application;
        this.callback = callback;
        mErrorActivity=CrashErrorActivity.class;
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
    }

    public interface Callback {
        void handle(Throwable ex);
    }

    /**
     * 处理未捕获的异常.
     */
    private class CrashHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            try {
                // 使用Toast来显示异常信息
                new Thread() {

                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(application, R.string.alert_crash_occurred,
                                Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }.start();

                Thread.sleep(1000);
                if (callback != null)
                    callback.handle(ex);

                Intent intent = new Intent(application, getErrorActivity());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }
                intent.putExtra(EXTRA_RESTART_ACTIVITY_CLASS, getRestartActivity());
                intent.putExtra(EXTRA_STACK_TRACE_MESSAGE, Log.getStackTraceString(ex));
                application.startActivity(intent);
                killCurrentProcess();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void setErrorActivity(Class<? extends Activity> errorActivity) {
        this.mErrorActivity = errorActivity;
    }

    public Class<? extends Activity> getErrorActivity() {
        return mErrorActivity == null ? CrashErrorActivity.class : mErrorActivity;
    }

    public void setRestartActivity(Class<? extends Activity> restartActivity) {
        this.mRestartActivity = restartActivity;
    }

    public Class<? extends Activity> getRestartActivity() {
        if (mRestartActivity != null)
            return mRestartActivity;
        return getLauncherActivity(application);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Activity> getLauncherActivity(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            try {
                return (Class<? extends Activity>) Class.forName(intent.getComponent().getClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void killCurrentProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends Activity> getRestartActivityFromIntent(Intent intent) {
        Serializable serializedClass = intent.getSerializableExtra(CrashWatchDog.EXTRA_RESTART_ACTIVITY_CLASS);

        if (serializedClass != null && serializedClass instanceof Class) {
            return (Class<? extends Activity>) serializedClass;
        } else {
            return null;
        }
    }

    public static String getStackTraceFromIntent(Intent intent) {
        return intent.getStringExtra(EXTRA_STACK_TRACE_MESSAGE);
    }

}

