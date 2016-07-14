package com.zyt.crashlistener;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/**
 * 崩溃时用于展示的界面
 */
public class CrashErrorActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnShowCrash, btnRestartApplication, btnExitApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_error);
        btnShowCrash = (Button) findViewById(R.id.btn_show_crash_message);
        btnRestartApplication = (Button) findViewById(R.id.btn_restart_application);
        btnExitApplication = (Button) findViewById(R.id.btn_exit_application);
        btnShowCrash.setOnClickListener(this);
        btnRestartApplication.setOnClickListener(this);
        btnExitApplication.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnShowCrash)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error_report))
                    .setMessage(CrashWatchDog.getStackTraceFromIntent(getIntent()))
                    .setNeutralButton(R.string.crash_dialog_neutral, null)
                    .show();
        } else if (v.equals(btnRestartApplication)) {
            Class<? extends Activity> restartActivity = CrashWatchDog.getRestartActivityFromIntent(getIntent());
            if (restartActivity == null) {
                Toast.makeText(CrashErrorActivity.this, "restartActivity is null", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(CrashErrorActivity.this, R.string.restart_app, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, restartActivity);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        } else if (v.equals(btnExitApplication)) {
            exitApp();
            finish();
            System.exit(0);
        }
    }

    private void exitApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

}
