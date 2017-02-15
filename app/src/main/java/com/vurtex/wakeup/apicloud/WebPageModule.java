package com.vurtex.wakeup.apicloud;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.uzmap.pkg.openapi.ExternalActivity;
import com.uzmap.pkg.openapi.Html5EventListener;
import com.uzmap.pkg.openapi.WebViewProvider;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONObject;

/**
 * 
 * 使用SuperWebview的Activity，需继承自ExternalActivity
 * @author dexing.li
 *
 */
public class WebPageModule extends ExternalActivity {

	private TextView mProgress;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProgress = new TextView(this);
		mProgress.setTextColor(0xFFFF0000);
		mProgress.setTextSize(20);
		mProgress.setVisibility(View.GONE);
		addContentView(mProgress, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		bindSomething();
	}
	
	private void bindSomething(){
		//设置一个监听，监听来自Html5页面发送出来的事件
		//此处我们监听"abc"事件，监听Html5页面通过api.sendEvent发出"abc"事件时
		addHtml5EventListener(new Html5EventListener("abc") {
			@Override
			public void onReceive(WebViewProvider provider, Object extra) {
				showAlert("收到来自Html5的abc事件，传入的参数为：\n\n" + extra + "\n\n");
			}
		});
	}
	
	/**
	 * 重写该函数，可实现处理收到来自Html5页面的操作请求，处理完毕后异步回调至Html5
	 */
	@Override
	protected boolean onHtml5AccessRequest(WebViewProvider provider, UZModuleContext moduleContext) {
		String name = moduleContext.optString("name");
		//"requestEvent"与assets/widget/html/wind.html页面的发送请求相匹配
		if("requestEvent".equals(name)){
			JSONObject extra = new JSONObject();
			try{
				extra.put("value", "哈哈哈，我是来自Native的事件");
			}catch(Exception e){
				;
			}
			//"fromNative"与assets/widget/html/wind.html页面的apiready中注册的监听相对应
			sendEventToHtml5("fromNative", extra);
			return true;
		}
		defaultHandleHtml5AccessRequest(moduleContext);
		return true;
	}

	/**
	 * 重写该函数，可实现处理某Html5页面开始加载时，执行相应的逻辑
	 */
	@Override
	protected void onPageStarted(WebViewProvider provider, String url, Bitmap favicon) {
		//远程Url，加载较慢
		if(url.startsWith("http")){
			showProgress();
		}
	}

	/**
	 * 重写该函数，可实现处理某Html5页面结束加载时，执行相应的逻辑
	 */
	@Override
	protected void onPageFinished(WebViewProvider provider, String url) {
		//远程Url，加载较慢
		if(url.startsWith("http")){
			mProgress.setText("加载进度：100");
			hidenProgress();
		};
	}

	/**
	 * 重写该函数，可实现处理某Html5页面加载进度发生变化时，执行相应的逻辑
	 */
	@Override
	protected void onProgressChanged(WebViewProvider provider, int newProgress) {
		//远程Url，加载较慢，显示进度，提升体验
		mProgress.setText("加载进度：" + newProgress);
		if(100 == newProgress){
			hidenProgress();
		}
	}

	/**
	 * 重写该函数，可实现处理某Html5页面dom的title标签发生变化时，执行相应的逻辑
	 */
	@Override
	protected void onReceivedTitle(WebViewProvider provider, String title) {
		;
	}

	/**
	 * 重写该函数，可实现处理拦截某Html5页面是否允许访问某API，如模块的API，APICloud引擎的API
	 */
	@Override
	protected boolean shouldForbiddenAccess(String host, String module, String api) {
		
		return true;
	}

	/**
	 * 重写该函数，可实现处理当某Webview即将加载某Url时，是否进行拦截，拦截后，该Webview将不继续加载该Url
	 */
	@Override
	protected boolean shouldOverrideUrlLoading(WebViewProvider provider, String url) {
		if(url.contains("taobao")){
			showAlert("不允许访问淘宝！");
			return true;
		}
		return false;
	}
	
	//默认处理收到收到来自Html5页面的操作请求，并通过UZModuleContext给予JS回调
	private void defaultHandleHtml5AccessRequest(final UZModuleContext moduleContext){
		String name = moduleContext.optString("name");
		Object extra = moduleContext.optObject("extra");
		AlertDialog.Builder dia = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		dia.setTitle("提示消息");
		dia.setMessage("收到来自Html5页面的操作请求，访问的名称标识为：\n[" + name + "]\n传入的参数为：\n[" + extra + "]\n\n" + "是否处理？\n");
		dia.setCancelable(false);
		dia.setPositiveButton("不处理", null);
		dia.setNegativeButton("处理", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				JSONObject json = new JSONObject();
				try{
					json.put("result0", "value0");
					json.put("result1", "value1");
					json.put("result2", "value2");
				}catch(Exception e){
					;
				}
				moduleContext.success(json, true);
			}
		});
		dia.show();
	}
	
	private void showAlert(String message){
		AlertDialog.Builder dia = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		dia.setTitle("提示消息");
		dia.setMessage(message);
		dia.setCancelable(false);
		dia.setPositiveButton("确定", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				;
			}
		});
		dia.show();
	}
	
	private void showProgress(){
		mProgress.setVisibility(View.VISIBLE);
	}
	
	private void hidenProgress(){
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(mProgress.isShown()){
					mProgress.setVisibility(View.GONE);
				}
			}
		}, 1500);
	}
}
