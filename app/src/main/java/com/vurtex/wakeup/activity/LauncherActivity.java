package com.vurtex.wakeup.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.vurtex.wakeup.R;
import com.vurtex.wakeup.base.BaseActivity;
import com.vurtex.wakeup.view.FillParentVideoView;

public class LauncherActivity extends BaseActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 3000;
    private FillParentVideoView videoview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_launcher);
        videoview = (FillParentVideoView) findViewById(R.id.videoview);
        //设置播放加载路径
        videoview.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.launcher));
        //播放
        videoview.start();
        //循环播放
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }
        });
        super.onCreate(savedInstanceState);
        Button btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });


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
