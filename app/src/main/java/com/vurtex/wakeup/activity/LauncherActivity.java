package com.vurtex.wakeup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.vurtex.wakeup.R;
import com.vurtex.wakeup.base.BaseActivity;

public class LauncherActivity extends BaseActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launcher);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检查是否登录
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (appPreferences.getBoolean("enter",false)) {
                        String username = appPreferences.getString("username",null);
                        String password = appPreferences.getString("password",null);
                        if (username != null && password != null) {
                            MainActivity.startMain(LauncherActivity.this);
                        } else {
                            startActivity(new Intent(LauncherActivity.this,LoginActivity.class));
                        }
                    } else {
                        IndexActivity.startIndex(getBaseContext());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    IndexActivity.startIndex(getBaseContext());
                }finally {
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
