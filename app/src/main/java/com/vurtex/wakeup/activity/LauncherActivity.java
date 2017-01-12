package com.vurtex.wakeup.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.vurtex.wakeup.base.BaseActivity;

import net.grandcentrix.tray.AppPreferences;
import net.grandcentrix.tray.core.ItemNotFoundException;

import static xiaofei.library.hermes.Hermes.getContext;

public class LauncherActivity extends BaseActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                    if (appPreferences.getBoolean("enter")) {
                        String username = appPreferences.getString("username");
                        String password = appPreferences.getString("password");
                        if (username != null && password != null) {
                            MainActivity.startMain(LauncherActivity.this);
                        } else {
                            LoginActivity.startLogin(LauncherActivity.this);
                        }
                    } else {
                        IndexActivity.startIndex(getBaseContext());
                    }
                } catch (ItemNotFoundException e) {
                    e.printStackTrace();
                    IndexActivity.startIndex(getBaseContext());
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
