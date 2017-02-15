package com.vurtex.wakeup.common;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.easemob.redpacketsdk.RedPacket;
import com.vurtex.wakeup.DemoHelper;

/**
 * Created by Vurtex on 2017/1/12.
 */

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
//        1. 显示Loading提示框
//        SimpleHUD.showLoadingMessage(this, "loading data, please wait...", true);
//        其中第三个参数为boolean cancelable，代表用户是否可以按back键关闭对话框。
//
//        2. 显示信息提示框
//        包括三种：信息、成功、失败
//        SimpleHUD.showInfoMessage(this, "This is a info message.");
//        SimpleHUD.showErrorMessage(this, "This ia an error message.");
//        SimpleHUD.showSuccessMessage(this, "This ia a success message.");
//        这三种提示框会在2秒后自动关闭。
//
//        3. 隐藏提示框
//        在调用新的show之前，SimpleHUD会自动关闭正在显示的HUD。
//        也可以使用SimpleHUD.dismiss(); 方法手动关闭。
//
//        4. 替换icon
//        四个图标在drawable文件夹内，使用相同名字图标替换即可。

//        SimpleHUD.showInfoMessage(this, "This is a info message.");
//        new Thread(){
//            public void run(){
//                try {
//                    sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                SimpleHUD.dismiss();
//            }
//        }.start();
    public static Context applicationContext;
    private static MyApplication instance;
    // login user name
    public final String PREF_USERNAME = "username";

    /**
     * nickname for current user, the nickname instead of ID be shown when user receive notification from APNs
     */
    public static String currentUserNick = "";

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        applicationContext = this;
        instance = this;

        //init demo helper
        DemoHelper.getInstance().init(applicationContext);
        //red packet code : 初始化红包上下文，开启日志输出开关
        RedPacket.getInstance().initContext(applicationContext);
        RedPacket.getInstance().setDebugMode(true);
        //end of red packet code
//        APICloud.initialize(this);//初始化APICloud，SDK中所有的API均需要初始化后方可调用执行
        //初始化APICloud网络请求框架，如果不需要则忽略，具体使用方式见MainActivity中的Case
//        APICloudHttpClient.createInstance(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
