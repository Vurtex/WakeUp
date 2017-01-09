package com.vurtex.wakeup.view;

/**
 * Created by Vurtex on 2017/1/8.
 */


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.SpriteFactory;
import com.github.ybq.android.spinkit.Style;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.vurtex.wakeup.R;


public class ConfirmDialog extends Dialog {


    public ConfirmDialog(Context context) {
        super(context);
    }

    public ConfirmDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ConfirmDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        View process= LayoutInflater.from(getContext()).inflate(R.layout.item_pager,null);
        SpinKitView kit = (SpinKitView) process.findViewById(R.id.spin_kit);
        Style style = Style.values()[2];
        Sprite drawable = SpriteFactory.create(style);
        kit.setIndeterminateDrawable(drawable);
        setContentView(process);
    }



}