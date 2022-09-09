package com.example.benchmark.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.benchmark.Data.Admin;
import com.example.benchmark.R;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.OkHttpUtils;
import com.example.benchmark.utils.OnSwipeTouchListener;

import java.security.KeyStore;

import okhttp3.Call;

public class LoginActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    int count = 0;
    private Button mBtnSignIn;
    private EditText mUserName;
    private EditText mPassWord;
    private String username = "";
    private String password = "";
    private Thread mThread;
    private CheckBox checkBox;
    private SharedPreferences sharedPreferences;
    private boolean mIsChecked=false;
    private static final String TAG = "LoginActivity";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        mBtnSignIn = findViewById(R.id.btn_sign_in);

        SharedPreferences sp2 = getSharedPreferences("Login", MODE_PRIVATE);
        mUserName = findViewById(R.id.et_username); // 用户名
        mUserName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Log.d(TAG, "beforeTextChanged: ");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Log.d(TAG, "onTextChanged: " + charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Log.d(TAG, "afterTextChanged: " + editable);
                //在文本改变之后记录用户账号
                if (mIsChecked){
                    if (sharedPreferences == null)
                        sharedPreferences = getApplication().getSharedPreferences("config", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", mUserName.getText().toString());
                }
                username = editable.toString();
                //Log.d(TAG, "afterTextChanged: " + username);
            }
        });

        mPassWord = findViewById(R.id.et_password); // 密码
        mPassWord.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Log.d(TAG, "beforeTextChanged: ");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Log.d(TAG, "onTextChanged: " + charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Log.d(TAG, "afterTextChanged: " + editable);
                //在文本改变之后记录用户密码
                if (mIsChecked){
                    if (sharedPreferences == null)
                        sharedPreferences = getApplication().getSharedPreferences("config", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("password", mPassWord.getText().toString());
                }
                password = editable.toString();
                //Log.d(TAG, "afterTextChanged: " + password);
            }
        });
        //setView();
        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "点击登录: username---" + username + "---password---" + password);
                if (username.length() < 5 || username.length() > 15) {
                    Toast.makeText(LoginActivity.this, "用户名长度为5~15位", Toast.LENGTH_SHORT).show();
                    if (password.length() < 5 || password.length() > 15) {
                        Toast.makeText(LoginActivity.this, "密码长度为5~15位", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // TODO 发送后端登录验证请求
                    mThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP+"/admin/loginAndReg")
                                    .addParam("adminName", username)
                                    .addParam("adminPasswd", password)
                                    .addHeader("Content-Type", "application/json; charset=utf-8")
                                    .post(true)
                                    .async(new OkHttpUtils.ICallBack() {
                                        @Override
                                        public void onSuccessful(Call call, String data) {
                                            Log.d(TAG, "onSuccessful: data--" + data);
                                            if (data.endsWith("成功")) {
                                                Admin.adminName = data.split(" ")[1];
                                                Log.d(TAG, "onSuccessful: Admin.adminName==" + Admin.adminName);

                                                Looper.prepare();
                                                Toast.makeText(LoginActivity.this, data, Toast.LENGTH_SHORT).show();
                                                // 验证成功后，跳转到主界面
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                Looper.loop();
                                            } else {
                                                Log.d(TAG, "onSuccessful: data==>" + data);
                                                Looper.prepare();
                                                Toast.makeText(LoginActivity.this, data, Toast.LENGTH_SHORT).show();
                                                Looper.loop();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call call, String errorMsg) {
                                            Log.d(TAG, "onFailure: errorMsg ==>" + errorMsg);
                                            Looper.prepare();
                                            Toast.makeText(LoginActivity.this, "遇见未知异常! 请检查网络后重新启动应用🙂 ", Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }
                                    });
                        }
                    });
                    mThread.start();
                }
            }
        });

        imageView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeTop() {
            }

            public void onSwipeRight() {
                if (count == 0) {
                    imageView.setImageResource(R.drawable.good_night_img);
                    textView.setText("Night");
                    count = 1;
                } else {
                    imageView.setImageResource(R.drawable.good_morning_img);
                    textView.setText("Morning");
                    count = 0;
                }
            }

            public void onSwipeLeft() {
                if (count == 0) {
                    imageView.setImageResource(R.drawable.good_night_img);
                    textView.setText("Night");
                    count = 1;
                } else {
                    imageView.setImageResource(R.drawable.good_morning_img);
                    textView.setText("Morning");
                    count = 0;
                }
            }
            public void onSwipeBottom() {
            }
        });

    }

    //记住密码了之后，还需要能够将其回显出来
    private void setView() {
        if (sharedPreferences == null)
            sharedPreferences = getApplication().getSharedPreferences("config", MODE_PRIVATE);
        mUserName.setText(sharedPreferences.getString("username",""));
        mPassWord.setText(sharedPreferences.getString("password",""));
        mIsChecked=sharedPreferences.getBoolean("状态",false);
        checkBox.setChecked(mIsChecked);
    }

}