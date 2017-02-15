package com.vurtex.wakeup.apicloud;


import android.app.ProgressDialog;
import android.content.Context;
import android.view.KeyEvent;

public class BlockMasking extends ProgressDialog {

	public BlockMasking(Context context) {
		super(context);
		setCanceledOnTouchOutside(false);
		setCancelable(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		return true;
	}
}
