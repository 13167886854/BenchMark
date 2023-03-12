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
import com.example.benchmark.data.IpPort;

/**
 * IpPortDialog
 *
 * @version 1.0
 * @since 2023/3/7 15:14
 */
public class IpPortDialog extends Dialog {
    /**
     * TAG
     */
    private static final String TAG = "Login";

    /**
     * 确定按钮
     */
    public Button yes;

    private Button no; // 取消按钮
    private TextView titleTv; // 消息标题文本
    private TextView messageTv; // 消息提示文本
    private String titleStr; // 从外界设置的title文本
    private String messageStr; // 从外界设置的消息文本
    private View view; // 确定文本和取消文本的显示内容
    private String yesStr;
    private String noStr;
    private EditText ipAddress;
    private EditText port;
    private String textIp = "";
    private String testPort = "";

    private OnNoOnclickListener noOnclickListener; // 取消按钮被点击了的监听器
    private OnYesOnclickListener yesOnclickListener; // 确定按钮被点击了的监听器
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
     * IpPortDialog
     *
     * @param context description
     * @date 2023/3/10 11:25
     */
    public IpPortDialog(@NonNull Context context) {
        super(context);
    }

    /**
     * setNoOnclickListener
     *
     * @param str                 description
     * @param onNoOnclickListener description
     * @return void
     * @date 2023/3/10 11:25
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
     * @param str                  description
     * @param onYesOnclickListener description
     * @return void
     * @date 2023/3/10 11:25
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
     * @date 2023/3/10 11:25
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip_port_dialog);

        // 设置背景透明，不然会出现白色直角问题
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        // 初始化界面控件
        initView();

        // 初始化界面控件的事件
        initEvent();
        ipAddress = findViewById(R.id.ip_address); // 用户名
        ipAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i0, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i0, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                textIp = editable.toString();
                IpPort.ip = textIp;
            }
        });
        port = findViewById(R.id.port); // 密码
        port.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i0, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i0, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                testPort = editable.toString();
                IpPort.port = testPort;
                IpPort.ip = textIp;
            }
        });
    }

    private void initEvent() {
        // 设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yesOnclickListener != null) {
                    Log.d(TAG, "onClick: ");
                }
                yesOnclickListener.onYesClick();
            }
        });
        // 设置取消按钮被点击后，向外界提供监听
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
        yes = findViewById(R.id.yes2);
        no = findViewById(R.id.no2);
        titleTv = findViewById(R.id.title);
        messageTv = findViewById(R.id.message);
        view = findViewById(R.id.view_dialog);
    }

    /**
     * setTitle
     *
     * @param title description
     * @return void
     * @date 2023/3/10 11:25
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * setMessage
     *
     * @param message description
     * @return void
     * @date 2023/3/10 11:25
     */
    public void setMessage(String message) {
        messageStr = message;
    }

    /**
     * IpPortDialog.java
     *
     * @Author benchmark
     * @Version 1.0
     * @since 2023/3/10 11:25
     */
    public interface OnYesOnclickListener {
        /**
         * onYesClick
         *
         * @return void
         * @date 2023/3/10 11:29
         */
        void onYesClick();
    }

    /**
     * IpPortDialog.java
     *
     * @Author benchmark
     * @Version 1.0
     * @since 2023/3/10 11:31
     */
    public interface OnNoOnclickListener {
        /**
         * onNoClick
         *
         * @return void
         * @date 2023/3/10 11:29
         */
        void onNoClick();
    }
}