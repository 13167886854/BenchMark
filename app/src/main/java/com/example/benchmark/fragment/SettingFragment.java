/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.fragment;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.benchmark.activity.SettingsActivity;
import com.example.benchmark.activity.ShuoMingActivity;
import com.example.benchmark.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * SettingFragment
 *
 * @version 1.0
 * @since 2023/3/7 15:17
 */
public class SettingFragment extends Fragment {
    private String tmpUrl = "https://1d2f09a7.r2.cpolar.top";
    private String latestVersion = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 101) {
                Log.d("TWT", "handleMessage: " + msg.getData().getString("result"));
            } else if (msg.what == 111) {
                Toast.makeText(getContext(), "已是最新版本!", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 222) {
                AlertDialog dialog = new AlertDialog.Builder(getContext())

                        // 设置标题的图片
                        .setIcon(R.mipmap.icon)

                        // 设置对话框的标题
                        .setTitle("checkUpdate")

                        // 设置对话框的内容
                        .setMessage("检测到应用有新版本，请前往浏览器更新")

                        // 设置对话框的按钮
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openBrowser(getContext(), tmpUrl
                                        + "/upgrade/update?version="
                                        + latestVersion + "&platform=Local");
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
                Toast.makeText(getContext(), "检测到有新版本，请前往浏览器下载最新版。", Toast.LENGTH_SHORT).show();
            }
            else {
                System.out.println(";");
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
        @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        RelativeLayout relativeLayout = view.findViewById(R.id.set_shiyongshuom);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                startActivity(new Intent(getActivity(), ShuoMingActivity.class));
            }
        });
        RelativeLayout paraSet = view.findViewById(R.id.set_paraSet);
        paraSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        RelativeLayout setSystemUpdate = view.findViewById(R.id.set_system_update);
        setSystemUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 打印信息 do nothing checkUpdate();
                Toast.makeText(getContext(), "已是最新版本!", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void checkUpdate() throws IOException, PackageManager.NameNotFoundException {
        String localVersion = getContext().getPackageManager()
                .getPackageInfo(getContext().getPackageName(), 0).versionName;
        Log.e("TWT", "localVersion: " + localVersion);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = tmpUrl + "/upgrade/hint?version=" + localVersion + "&platform=Local";
        // 创建一个request对象
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            /**
             * onFailure
             *
             * @param call description
             * @param exception description
             * @return void
             * @throws null
             * @date 2023/3/8 09:58
             */
            public void onFailure(Call call, IOException exception) {
                Log.e("TWT", "onFailure: " + exception);
            }

            /**
             * onResponse
             *
             * @param call description
             * @param response description
             * @return void
             * @throws null
             * @date 2023/3/8 09:58
             */
            public void onResponse(Call call, Response response)
                    throws IOException {
                String str = response.body().string();
                if (str.equals("当前已是最新版本")) {
                    Message msg = new Message();
                    msg.what = 111;
                    handler.sendMessage(msg);
                } else {
                    latestVersion = str;
                    Message msg = new Message();
                    msg.what = 222;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    /**
     * openBrowser
     *
     * @param context description
     * @param url     description
     * @return void
     * @throws null
     * @date 2023/3/8 09:58
     */
    public static void openBrowser(Context context, String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(context.getApplicationContext(), "请下载浏览器", Toast.LENGTH_SHORT).show();
        }
    }
}
