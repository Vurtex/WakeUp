package com.vurtex.wakeup.common;

import android.app.Application;
import android.support.multidex.MultiDex;

import java.util.UUID;

import tech.jiangtao.support.ui.SupportUI;
import work.wanghao.simplehud.SimpleHUD;

/**
 * Created by Vurtex on 2017/1/12.
 */

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    @Override public void onCreate() {
        super.onCreate();
        //LeakCanary.install(this);
        MultiDex.install(this);
//        PgyCrashManager.register(this);
        SimpleHUD.backgroundHexColor="#FF4081";
        SupportUI.initialize(this, "dc-a4b8eb92-xmpp.jiangtao.tech.", UUID.randomUUID().toString(),
                "139.162.73.105", 5222, "6e7ea2251ca5479d875916785c4418f1",
                "026eb8a2cb7b4ab18135a6a0454fd698", "http://ci.jiangtao.tech/fileUpload/");
    }
}
