/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.benchmark.data.Admin;
import com.example.benchmark.dialog.LoginDialog;
import com.example.benchmark.R;

/**
 * LoginActivity
 *
 * @version 1.0
 * @since 2023/3/7 15:05
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private int count = 0;
    private String username = "";
    private String password = "";
    private boolean isChecked = false;

    private ImageView imageView;
    private TextView textView;
    private Button mBtnSignIn;
    private EditText mUserName;
    private EditText mPassWord;
    private Thread mThread;
    private CheckBox checkBox;
    private SharedPreferences sharedPreferences;
    private Message mMessage;
    private LoginDialog myDialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mBtnSignIn.setEnabled(true);
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init1();
        mPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i0, int i1, int i2) {
                Log.d(TAG, "beforeTextChanged: ");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i0, int i1, int i2) {
                Log.d(TAG, "onTextChanged: " + charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 在文本改变之后记录用户密码   Record the user password after the text changes
                if (isChecked) {
                    if (sharedPreferences == null) {
                        sharedPreferences = getApplication().getSharedPreferences("config", MODE_PRIVATE);
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("password", mPassWord.getText().toString());
                }
                password = editable.toString();
            }
        });
    }

    private void init1() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        imageView = findViewById(R.id.imageView);
        mBtnSignIn = findViewById(R.id.btn_sign_in);
        mBtnSignIn.setEnabled(true);

        SharedPreferences sp2 = getSharedPreferences("Login", MODE_PRIVATE);
        mUserName = findViewById(R.id.et_username);
        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i0, int i1, int i2) {
                Log.d(TAG, "beforeTextChanged: ");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i0, int i1, int i2) {
                Log.d(TAG, "onTextChanged: " + charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 在文本改变之后记录用户账号   Record the user account after the text changes
                if (isChecked) {
                    if (sharedPreferences == null) {
                        sharedPreferences = getApplication().getSharedPreferences("config", MODE_PRIVATE);
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", mUserName.getText().toString());
                }
                username = editable.toString();
            }
        });
        mPassWord = findViewById(R.id.et_password);
    }

    /**
     * showDialog
     *
     * @return void
     * @throws null
     * @date 2023/3/8 09:49
     */
    public void showDialog() {
        myDialog = new LoginDialog(this);
        myDialog.setNoOnclickListener("取消", new LoginDialog.OnNoOnclickListener() {
            @Override
            public void onNoClick() {
                myDialog.dismiss();
            }
        });
        myDialog.setYesOnclickListener("确定", new LoginDialog.OnYesOnclickListener() {
            @Override
            public void onYesClick() {
                if (Admin.getInstance().getStatus().equals("Success")) {
                    myDialog.dismiss();
                }
            }
        });
        myDialog.show();
        Window dialogWindow = myDialog.getWindow();
        WindowManager manager = LoginActivity.this.getWindowManager();

        // 获取屏幕宽、高度   Obtain the screen width and height
        Display display = manager.getDefaultDisplay();

        // 获取对话框当前的参数值   Gets the current parameter values for the dialog box
        WindowManager.LayoutParams params = dialogWindow.getAttributes();

        // 高度设置为屏幕的0.6，根据实际情况调整
        // Set the height to 0.6 of the screen and adjust it according to the actual situation
        params.height = (int) (display.getHeight() * 0.9);

        // 宽度设置为屏幕的0.65，根据实际情况调整   Set the width to 0.65 of the screen and adjust as needed
        params.width = (int) (display.getWidth() * 0.9);
        dialogWindow.setAttributes(params);
    }

    private void setView() {
        if (sharedPreferences == null) {
            sharedPreferences = getApplication().getSharedPreferences("config", MODE_PRIVATE);
        }
        mUserName.setText(sharedPreferences.getString("username", ""));
        mPassWord.setText(sharedPreferences.getString("password", ""));
        isChecked = sharedPreferences.getBoolean("状态", false);
        checkBox.setChecked(isChecked);
    }
}