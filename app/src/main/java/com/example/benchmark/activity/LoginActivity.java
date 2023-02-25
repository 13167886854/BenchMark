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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.benchmark.data.Admin;
import com.example.benchmark.diaLog.LoginDialog;
import com.example.benchmark.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

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
    private boolean isChecked = false;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        imageView = findViewById(R.id.imageView);
        mBtnSignIn = findViewById(R.id.btn_sign_in); // 登录
        mBtnSignIn.setEnabled(true);

        SharedPreferences sp2 = getSharedPreferences("Login", MODE_PRIVATE);
        mUserName = findViewById(R.id.et_username); // 用户名
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
                // 在文本改变之后记录用户账号
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

        mPassWord = findViewById(R.id.et_password); // 密码
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
                // 在文本改变之后记录用户密码
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
                if (Admin.STATUS.equals("Success")) {
                    myDialog.dismiss();
                }
            }
        });
        myDialog.show();
        Window dialogWindow = myDialog.getWindow();
        WindowManager manager = LoginActivity.this.getWindowManager();
        Display display = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        params.height = (int) (display.getHeight() * 0.9); // 高度设置为屏幕的0.6，根据实际情况调整
        params.width = (int) (display.getWidth() * 0.9); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(params);
    }

    // 记住密码了之后，还需要能够将其回显出来
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