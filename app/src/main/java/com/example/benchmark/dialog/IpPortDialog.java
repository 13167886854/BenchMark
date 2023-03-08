/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.dialog;

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
import com.example.benchmark.data.IpPort;


/**
 * IpPortDialog
 *
 * @version 1.0
 * @since 2023/3/7 15:14
 */
public class IpPortDialog extends Dialog {
    public Button yes;//确定按钮
    private Button no;//取消按钮
    private TextView titleTv;//消息标题文本
    private TextView messageTv;//消息提示文本
    private String titleStr;//从外界设置的title文本
    private String messageStr;//从外界设置的消息文本
    private View view;
    //确定文本和取消文本的显示内容
    private String yesStr, noStr;

    private EditText ipAddress;
    private EditText port;
    private String textIp = "";
    private String testPort = "";
    private SharedPreferences sharedPreferences;
    private boolean mIsChecked = false;
    private Thread mThread;
    private Message mMessage;
    private static final String TAG = "Login";

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                yes.setEnabled(true);
            }
        }
    };

    public IpPortDialog(@NonNull Context context) {
        super(context);
    }

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {

            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip_port_dialog);
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
        ipAddress = findViewById(R.id.ip_address); // 用户名
        ipAddress.addTextChangedListener(new TextWatcher() {

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
                //if (mIsChecked){
                //    if (sharedPreferences == null)
                //        sharedPreferences = getApplication().getSharedPreferences("config", MODE_PRIVATE);
                //    SharedPreferences.Editor editor = sharedPreferences.edit();
                //    editor.putString("username", mUserName.getText().toString());
                //}
                textIp = editable.toString();
                IpPort.ip = textIp;
                //Log.d(TAG, "afterTextChanged: " + username);ad
            }
        });

        port = findViewById(R.id.port); // 密码
        port.addTextChangedListener(new TextWatcher() {

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
                //if (mIsChecked){
                //    if (sharedPreferences == null)
                //        sharedPreferences = getApplication().getSharedPreferences("config", MODE_PRIVATE);
                //    SharedPreferences.Editor editor = sharedPreferences.edit();
                //    editor.putString("password", mPassWord.getText().toString());
                //}
                testPort = editable.toString();
                IpPort.port = testPort;
                IpPort.ip = textIp;
                //Log.d(TAG, "afterTextChanged: " + password);
            }
        });

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    Log.d(TAG, "onClick: ");
                }
                yesOnclickListener.onYesClick();
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {

    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        yes = (Button) findViewById(R.id.yes2);
        no = (Button) findViewById(R.id.no2);
        titleTv = (TextView) findViewById(R.id.title);
        messageTv = (TextView) findViewById(R.id.message);
        view = findViewById(R.id.view_dialog);
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    public void setMessage(String message) {
        messageStr = message;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick();
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }

}



