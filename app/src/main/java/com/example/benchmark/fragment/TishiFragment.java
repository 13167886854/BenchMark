/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.benchmark.data.Admin;
import com.example.benchmark.dialog.LoginDialog;
import com.example.benchmark.R;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.OkHttpUtils;

import okhttp3.Call;

/**
 * TishiFragment
 *
 * @version 1.0
 * @since 2023/3/7 15:17
 */
public class TishiFragment extends Fragment {
    private static final String TAG = "login";

    private Button infoFluency;
    private Button infoStability;
    private Button infoTouch;
    private Button infoAudioVideo;
    private Button infoHardware;
    private LoginDialog myDialog;
    private Message mMessage;
    private Thread mThread;
    private FragmentManager fragmentManager;
    private HistoryFragment history;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                myDialog.yes.setEnabled(true);
            } else if (msg.what == 2) {
                myDialog.yes.setEnabled(true);
                myDialog.dismiss();
            } else {
                Log.e(TAG, "handleMessage: num of msg.what error");
            }
        }
    };

    /**
     * onCreateView
     *
     * @param inflater           description
     * @param container          description
     * @param savedInstanceState description
     * @return android.view.View
     * @throws null
     * @date 2023/3/8 10:11
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tishi_fragment, container, false);
        infoFluency = view.findViewById(R.id.info_fluency);
        infoStability = view.findViewById(R.id.info_stability);
        infoTouch = view.findViewById(R.id.info_touch);
        infoAudioVideo = view.findViewById(R.id.info_audio_video);
        infoHardware = view.findViewById(R.id.info_hardware);

        history = new HistoryFragment();

        if (!Admin.status.equals("Success")) {
            showDialog();
        }

        infoFluency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "info_fluency");
                history.setArguments(bundle);
                fragmentManager = getFragmentManager();

                // 开启事务
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_fram, history);
                fragmentTransaction.commit();
            }
        });
        infoStability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "info_stability");
                history.setArguments(bundle);
                fragmentManager = getFragmentManager();

                // 开启事务
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_fram, history);
                fragmentTransaction.commit();
            }
        });
        infoTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "info_touch");
                history.setArguments(bundle);
                fragmentManager = getFragmentManager();

                // 开启事务
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_fram, history);
                fragmentTransaction.commit();
            }
        });
        infoAudioVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "info_audio_video");
                history.setArguments(bundle);
                fragmentManager = getFragmentManager();

                // 开启事务
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_fram, history);
                fragmentTransaction.commit();
            }
        });
        infoHardware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "info_hardware");
                history.setArguments(bundle);
                fragmentManager = getFragmentManager();

                // 开启事务
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_fram, history);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    /**
     * showDialog
     *
     * @return void
     * @throws null
     * @date 2023/3/8 10:11
     */
    public void showDialog() {
        myDialog = new LoginDialog(getContext());
        myDialog.setNoOnclickListener("取消", new LoginDialog.OnNoOnclickListener() {
            @Override
            public void onNoClick() {
                myDialog.dismiss();
            }
        });
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                myDialog.setYesOnclickListener("确定", new LoginDialog.OnYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        myDialog.yes.setEnabled(false);
                        Log.d(TAG, "点击登录: username---" + Admin.username
                                + "---password---" + Admin.username);
                        if (Admin.username.length() < 5 || Admin.username.length() > 15) {
                            Toast.makeText(getContext(), "用户名长度为5~15位"
                                    , Toast.LENGTH_SHORT).show();
                            myDialog.yes.setEnabled(true);
                            if (Admin.password.length() < 5 || Admin.password.length() > 15) {
                                Toast.makeText(getContext(), "密码长度为5~15位"
                                        , Toast.LENGTH_SHORT).show();
                                myDialog.yes.setEnabled(true);
                            }
                        } else {
                            // 发送后端登录验证请求
                            mThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    OkHttpUtils.builder().url(CacheConst.GLOBAL_IP
                                            + "/admin/loginAndReg")
                                            .addParam("adminName", Admin.username)
                                            .addParam("adminPasswd", Admin.password)
                                            .addHeader("Content-Type"
                                                    , "application/json; charset=utf-8")
                                            .post(true)
                                            .async(new OkHttpUtils.ICallBack() {
                                                @Override
                                                public void onSuccessful(Call call, String data) {
                                                    Log.e(TAG, "Admin.username: "
                                                            + Admin.username);
                                                    Log.e(TAG, "Admin.password: "
                                                            + Admin.password);
                                                    Log.d(TAG, "onSuccessful: data--" + data);
                                                    if (data.endsWith("成功")) {
                                                        Admin.adminName = data.split(" ")[1];
                                                        Log.d(TAG, "onSuccessful: Admin.adminName=="
                                                                + Admin.adminName);
                                                        Admin.status = "Success";
                                                        mMessage = mHandler.obtainMessage();
                                                        mMessage.what = 2;
                                                        mHandler.sendMessage(mMessage);
                                                        Looper.prepare();
                                                        Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();

                                                        // 验证成功后，跳转到主界面
                                                        Looper.loop();
                                                    } else {
                                                        Log.d(TAG, "onSuccessful: data==>"
                                                                + data);
                                                        mMessage = mHandler.obtainMessage();
                                                        mMessage.what = 1;
                                                        mHandler.sendMessage(mMessage);

                                                        Looper.prepare();
                                                        Toast.makeText(getContext(), data
                                                                , Toast.LENGTH_SHORT).show();
                                                        Looper.loop();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call call, String errorMsg) {
                                                    Log.d(TAG, "onFailure: errorMsg ==>"
                                                            + errorMsg);
                                                    mMessage = mHandler.obtainMessage();
                                                    mMessage.what = 1;
                                                    mHandler.sendMessage(mMessage);

                                                    Looper.prepare();
                                                    Toast.makeText(getContext(),
                                                            "遇见未知异常! " +
                                                                    "请检查网络后重新启动应用🙂 ",
                                                            Toast.LENGTH_SHORT).show();
                                                    Looper.loop();
                                                }
                                            });
                                }
                            });
                            mThread.start();
                        }
                    }
                });
            }
        });
        mThread.start();
        myDialog.show();
        Window dialogWindow = myDialog.getWindow();
        WindowManager manager = getActivity().getWindowManager();
        Display display = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        params.height = (int) (display.getHeight() * 0.9); // 高度设置为屏幕的0.6，根据实际情况调整
        params.width = (int) (display.getWidth() * 0.9); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(params);
    }
}
