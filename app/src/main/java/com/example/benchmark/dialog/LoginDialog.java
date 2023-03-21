/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.dialog;

import android.app.Dialog;
import android.content.Context;
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
 * LoginDialog
 *
 * @version 1.0
 * @since 2023/3/7 15:14
 */
public class LoginDialog extends Dialog {
    /** Login */
    private static final String TAG = "Login";

    /** 确定按钮  Confirm button */
    public Button yes;

    // 取消按钮  Cancel button
    private Button no;

    // 消息标题文本  Message header text
    private TextView titleTv;

    // 消息提示文本  Message prompt text
    private TextView messageTv;

    // 从外界设置的title文本  The title text is set from the outside
    private String titleStr;

    // 从外界设置的消息文本  The message text set from the outside world
    private String messageStr;
    private View view;
    private String yesStr;
    private String noStr;
    private String username = "";
    private String password = "";

    // 确定文本和取消文本的显示内容  Determines the text and undisplays the text
    private EditText mUserName;
    private EditText mPassWord;

    // 取消按钮被点击了的监听器  Cancel the listener whose button was clicked
    private OnNoOnclickListener noOnclickListener;

    // 确定按钮被点击了的监听器  A listener that determines that the button was clicked
    private OnYesOnclickListener yesOnclickListener;

    private Handler mHandler = new Handler() {
        /**
         * handleMessage
         *
         * @param msg description
         * @return void
         * @date 2023/3/10 11:35
         */
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                yes.setEnabled(true);
            }
        }
    };

    /**
     * LoginDialog
     *
     * @param context description
     * @date 2023/3/10 11:33
     */
    public LoginDialog(@NonNull Context context) {
        super(context);
    }

    /**
     * setNoOnclickListener
     *
     * @param str description
     * @param onNoOnclickListener description
     * @date 2023/3/10 11:33
     */
    public void setNoOnclickListener(String str, OnNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * setYesOnclickListener
     *
     * @param str description
     * @param onYesOnclickListener description
     * @return void
     * @date 2023/3/10 11:33
     */
    public void setYesOnclickListener(String str, OnYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    /**
     * onCreate
     *
     * @param savedInstanceState description
     * @return void
     * @date 2023/3/9 19:43
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mydialog);

        // 设置背景透明，不然会出现白色直角问题  Make the background transparent, otherwise the white right Angle problem will occur
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 按空白处不能取消动画  Pressing blank space cannot cancel animation
        setCanceledOnTouchOutside(false);

        // 初始化界面控件  Initializes the interface control
        initView();

        // 初始化界面控件的事件  The event that initializes an interface control
        initEvent();

        // 用户名  User name
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
        // 密码  password
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

    private void initEvent() {
        // 设置确定按钮被点击后，向外界提供监听  Set OK button is clicked, to provide external listening
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yesOnclickListener != null) {
                    Log.d(TAG, "onClick: yesOnclickListener");
                }
                yesOnclickListener.onYesClick();
            }
        });

        // 设置取消按钮被点击后，向外界提供监听  Set Cancel button is clicked to provide external listening
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
    }

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

    /**
     * OnYesOnclickListener
     *
     * @return
     * @throws null
     * @date 2023/3/8 09:44
     */
    public interface OnYesOnclickListener {
        /**
         * onYesClick
         *
         * @return void
         * @throws null
         * @date 2023/3/8 09:44
         */
        void onYesClick();
    }

    /**
     * OnNoOnclickListener
     *
     * @return
     * @throws null
     * @date 2023/3/8 09:44
     */
    public interface OnNoOnclickListener {
        /**
         * onNoClick
         *
         * @return void
         * @throws null
         * @date 2023/3/8 09:44
         */
        void onNoClick();
    }
}



