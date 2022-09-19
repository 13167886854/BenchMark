package com.example.benchmark.Fragment;

import android.content.Intent;
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

import com.example.benchmark.Activity.LoginActivity;
import com.example.benchmark.Activity.MainActivity;
import com.example.benchmark.Activity.TestInfoActivity;
import com.example.benchmark.Data.Admin;
import com.example.benchmark.DiaLog.LoginDialog;
import com.example.benchmark.R;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.OkHttpUtils;

import okhttp3.Call;

public class TishiFragment extends Fragment {

    private Button info_fluency;
    private Button info_stability;
    private Button info_touch;
    private Button info_audio_video;
    private Button info_hardware;
    private LoginDialog myDialog;
    private Message mMessage;
    private Thread mThread;
    private FragmentManager fragmentManager;
    private HistoryFragment history;
    private static final String TAG = "login";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                myDialog.yes.setEnabled(true);
            } else if (msg.what == 2) {
                myDialog.yes.setEnabled(true);
                myDialog.dismiss();

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tishi_fragment, container, false);
        info_fluency = view.findViewById(R.id.info_fluency);
        info_stability = view.findViewById(R.id.info_stability);
        info_touch = view.findViewById(R.id.info_touch);
        info_audio_video = view.findViewById(R.id.info_audio_video);
        info_hardware = view.findViewById(R.id.info_hardware);

        history = new HistoryFragment();

        if (!Admin.STATUS.equals("Success")) {
            showDialog();
        }

        info_fluency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), TestInfoActivity.class);
//                intent.putExtra("type", "info_fluency");
//                getContext().startActivity(intent);
                Bundle bundle = new Bundle();
                bundle.putString("type", "info_fluency");
                history.setArguments(bundle);
                fragmentManager = getFragmentManager();;
                //开启事务
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_fram, history);
                fragmentTransaction.commit();
            }
        });
        info_stability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), TestInfoActivity.class);
//                intent.putExtra("type", "");
//                getContext().startActivity(intent);
                Bundle bundle = new Bundle();
                bundle.putString("type", "info_stability");
                history.setArguments(bundle);
                fragmentManager = getFragmentManager();;
                //开启事务
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_fram, history);
                fragmentTransaction.commit();
            }
        });
        info_touch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), TestInfoActivity.class);
//                intent.putExtra("type", "info_touch");
//                getContext().startActivity(intent);
                Bundle bundle = new Bundle();
                bundle.putString("type", "info_touch");
                history.setArguments(bundle);
                fragmentManager = getFragmentManager();;
                //开启事务
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_fram, history);
                fragmentTransaction.commit();
            }
        });
        info_audio_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), TestInfoActivity.class);
//                intent.putExtra("type", "info_audio_video");
//                getContext().startActivity(intent);
                Bundle bundle = new Bundle();
                bundle.putString("type", "info_audio_video");
                history.setArguments(bundle);
                fragmentManager = getFragmentManager();;
                //开启事务
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_fram, history);
                fragmentTransaction.commit();
            }
        });
        info_hardware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), TestInfoActivity.class);
//                intent.putExtra("type", "info_hardware");
//                getContext().startActivity(intent);
                Bundle bundle = new Bundle();
                bundle.putString("type", "info_hardware");
                history.setArguments(bundle);
                fragmentManager = getFragmentManager();;
                //开启事务
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_fram, history);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public void showDialog() {
        myDialog = new LoginDialog(getContext());
        myDialog.setNoOnclickListener("取消", new LoginDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                myDialog.dismiss();
            }
        });
        //Toast.makeText(getContext(), (Admin.adminName + "----" + Admin.platformName), Toast.LENGTH_SHORT).show();
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                myDialog.setYesOnclickListener("确定", new LoginDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        myDialog.yes.setEnabled(false);
                        Log.d(TAG, "点击登录: username---" + Admin.username + "---password---" + Admin.username);
                        if (Admin.username.length() < 5 || Admin.username.length() > 15) {
                            Toast.makeText(getContext(), "用户名长度为5~15位", Toast.LENGTH_SHORT).show();
                            myDialog.yes.setEnabled(true);
                            if (Admin.password.length() < 5 || Admin.password.length() > 15) {
                                Toast.makeText(getContext(), "密码长度为5~15位", Toast.LENGTH_SHORT).show();
                                myDialog.yes.setEnabled(true);
                            }
                        } else {
                            // 发送后端登录验证请求
                            mThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    OkHttpUtils.builder().url(CacheConst.GLOBAL_IP+"/admin/loginAndReg")
                                            .addParam("adminName", Admin.username)
                                            .addParam("adminPasswd", Admin.password)
                                            .addHeader("Content-Type", "application/json; charset=utf-8")
                                            .post(true)
                                            .async(new OkHttpUtils.ICallBack() {
                                                @Override
                                                public void onSuccessful(Call call, String data) {
                                                    Log.d(TAG, "onSuccessful: data--" + data);
                                                    if (data.endsWith("成功")) {
                                                        Admin.adminName = data.split(" ")[1];
                                                        Log.d(TAG, "onSuccessful: Admin.adminName==" + Admin.adminName);
                                                        Admin.STATUS = "Success";
                                                        mMessage = mHandler.obtainMessage();
                                                        mMessage.what = 2;
                                                        mHandler.sendMessage(mMessage);
                                                        Looper.prepare();
                                                        Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
                                                        // 验证成功后，跳转到主界面
                                                        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                        Looper.loop();
                                                    } else {
                                                        Log.d(TAG, "onSuccessful: data==>" + data);
                                                        mMessage = mHandler.obtainMessage();
                                                        mMessage.what = 1;
                                                        mHandler.sendMessage(mMessage);

                                                        Looper.prepare();
                                                        Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
                                                        Looper.loop();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call call, String errorMsg) {
                                                    Log.d(TAG, "onFailure: errorMsg ==>" + errorMsg);
                                                    mMessage = mHandler.obtainMessage();
                                                    mMessage.what = 1;
                                                    mHandler.sendMessage(mMessage);

                                                    Looper.prepare();
                                                    Toast.makeText(getContext(), "遇见未知异常! 请检查网络后重新启动应用🙂 ", Toast.LENGTH_SHORT).show();
                                                    Looper.loop();
                                                }
                                            });
                                }
                            });
                            mThread.start();
                        }

                        //if (Admin.STATUS.equals("Success")) {
                        //    mMessage = mHandler.obtainMessage();
                        //    mMessage.what = 1;
                        //    mHandler.sendMessage(mMessage);
                        //}
                    }
                });
            }
        });
        mThread.start();
        myDialog.show();
        Window dialogWindow = myDialog.getWindow();
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.9); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);
    }
}
