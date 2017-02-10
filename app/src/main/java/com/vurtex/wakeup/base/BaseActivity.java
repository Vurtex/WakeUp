package com.vurtex.wakeup.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.androidquery.AQuery;
import com.hyphenate.easeui.controller.EaseUI;
import com.vurtex.wakeup.common.PreferencesUtils;

/**
 * Created by Vurtex on 2017/1/12.
 */

public class BaseActivity extends AppCompatActivity{

    protected InputMethodManager inputMethodManager;
    public AQuery aq;

    public PreferencesUtils appPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq=new AQuery(this);
        appPreferences=new PreferencesUtils(this);
        if(!isTaskRoot()){
            Intent intent = getIntent();
            String action = intent.getAction();
            if(intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)){
                finish();
                return;
            }
        }

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // cancel the notification
        EaseUI.getInstance().getNotifier().reset();
    }

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * back
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }
}
