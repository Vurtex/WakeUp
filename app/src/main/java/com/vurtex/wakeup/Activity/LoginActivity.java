package com.vurtex.wakeup.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.github.ybq.android.spinkit.style.Wave;
import com.vurtex.wakeup.R;
import com.vurtex.wakeup.common.Colors;
import com.vurtex.wakeup.common.Constants;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Vurtex
 * @date 2017-1-7
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  implements Colors {

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private AQuery aq;
    private Context context;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=this;
        aq = new AQuery(context);
//        spinKitView = (SpinKitView)findViewById(R.id.spin_kit);
        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.et_username);

        mPasswordView = (EditText) findViewById(R.id.et_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    //自动登录
                    return true;
                }
                return false;
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progress);
        Wave doubleBounce = new Wave();
        doubleBounce.setBounds(0, 0, 100, 100);
        doubleBounce.setColor(colors[8]);
        progressBar.setIndeterminateDrawable(doubleBounce);

        Button mEmailSignInButton = (Button) findViewById(R.id.btn_login);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //去登录
                progressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }).start();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        });

    }

    OkHttpClient mOkHttpClient = new OkHttpClient();

    /** 登录
     * POST 请求
     * @param Username
     * @param Password
     */
    private void LoginPostAsynHttp(String Username, String Password) {

        RequestBody formBody = new FormBody.Builder()
                .add("username", Username)
                .add("password", Password)
                .add("versionCode", "100")
                .add("systemType", "Android")
                .build();
        Request request = new Request.Builder()
                .url(Constants.Service_url+"/lianxi/123.php")
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("vurtex", call.toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.i("vurtex", str);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "请求成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    }
                });
            }

        });
    }
}

