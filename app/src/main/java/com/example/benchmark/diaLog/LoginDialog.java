package com.example.benchmark.diaLog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.benchmark.R;
import com.example.benchmark.data.Admin;


/**
 * @version 1.0
 * @description LoginDialog 登录DigLog
 * @time 2023/2/23 09:41
 */
public class LoginDialog extends Dialog {
    private static final String TAG = "Login";

    //确定按钮
    public Button yes;

    //取消按钮
    private Button no;

    //消息标题文本
    private TextView titleTv;

    //消息提示文本
    private TextView messageTv;

    //从外界设置的title文本
    private String titleStr;

    //从外界设置的消息文本
    private String messageStr;
    private View view;
    private String yesStr, noStr;
    private String username = "";
    private String password = "";
    private boolean mIsChecked = false;

    //确定文本和取消文本的显示内容
    private EditText mUserName;
    private EditText mPassWord;
    private SharedPreferences sharedPreferences;
    private Thread mThread;
    private Message mMessage;

    //取消按钮被点击了的监听器
    private OnNoOnclickListener noOnclickListener;

    //确定按钮被点击了的监听器
    private OnYesOnclickListener yesOnclickListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                yes.setEnabled(true);
            }
        }
    };

    /**
     * @param context description
     * @return null
     * @throws null
     * @description: LoginDialog
     * @date 2023/2/23 09:44
     */
    public LoginDialog(@NonNull Context context) {
        super(context);
    }

    /**
     * @param str                 description
     * @param onNoOnclickListener description
     * @return void
     * @throws null
     * @description: 设置取消按钮的显示内容和监听
     * @date 2023/2/23 09:41
     */
    public void setNoOnclickListener(String str, OnNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * @param str                  description
     * @param onYesOnclickListener description
     * @return void
     * @throws null
     * @description: 设置确定按钮的显示内容和监听
     * @date 2023/2/23 09:42
     */
    public void setYesOnclickListener(String str, OnYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mydialog);

        //设置背景透明，不然会出现白色直角问题
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();

        //初始化界面数据
        initData();

        //初始化界面控件的事件
        initEvent();

        // 用户名
        mUserName = findViewById(R.id.et_username);
        mUserName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i0, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i0, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                username = editable.toString();
                Admin.username = username;

            }
        });

        // 密码
        mPassWord = findViewById(R.id.et_password);
        mPassWord.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i0, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i0, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                password = editable.toString();
                Admin.password = password;
            }
        });

    }

    /**
     * @return void
     * @throws null
     * @description: 初始化界面的确定和取消监听器
     * @date 2023/2/23 09:42
     */
    private void initEvent() {

        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yesOnclickListener != null) {
                    Log.d(TAG, "onClick: yesOnclickListener");
                }
                yesOnclickListener.onYesClick();
            }
        });

        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
    }

    /**
     * @return void
     * @throws null
     * @description: 初始化界面控件的显示数据
     * @date 2023/2/23 09:43
     */
    private void initData() {

    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        if (findViewById(R.id.yes) instanceof Button) {
            yes = (Button) findViewById(R.id.yes);
        }
        if (findViewById(R.id.no) instanceof Button) {
            no = (Button) findViewById(R.id.no);
        }
        if (findViewById(R.id.title) instanceof TextView) {
            titleTv = (TextView) findViewById(R.id.title);
        }
        if (findViewById(R.id.message) instanceof TextView) {
            messageTv = (TextView) findViewById(R.id.message);
        }
        view = findViewById(R.id.view_dialog);
    }

    /**
     * @param title description
     * @return void
     * @throws null
     * @description: 从外界Activity为Dialog设置标题
     * @date 2023/2/23 09:43
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * @param message description
     * @return void
     * @throws null
     * @description: 从外界Activity为Dialog设置dialog的message
     * @date 2023/2/23 09:43
     */
    public void setMessage(String message) {
        messageStr = message;
    }

    public interface OnYesOnclickListener {
        void onYesClick();
    }

    public interface OnNoOnclickListener {
        void onNoClick();
    }

}



