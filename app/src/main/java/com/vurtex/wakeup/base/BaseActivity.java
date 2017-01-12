package com.vurtex.wakeup.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.androidquery.AQuery;

import net.grandcentrix.tray.AppPreferences;

import static xiaofei.library.hermes.Hermes.getContext;

/**
 * Created by Vurtex on 2017/1/12.
 */

public class BaseActivity extends AppCompatActivity{

    public AppPreferences appPreferences = new AppPreferences(getContext());
    public AQuery aq;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq=new AQuery(this);
    }
}
