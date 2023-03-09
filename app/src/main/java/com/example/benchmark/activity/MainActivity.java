/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.benchmark.fragment.SettingFragment;
import com.example.benchmark.fragment.TishiFragment;
import com.example.benchmark.fragment.ZhuyeFragment;
import com.example.benchmark.R;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private FragmentManager fragmentManager;
    private RadioGroup mainMenu;
    private ImageButton menu;
    private ImageButton qrCode;
    private Context mContext = this;
    private String tmpUrl = "https://1d2f09a7.r2.cpolar.top";
    private String latestVersion = null;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 101) {
                Log.d("TWT", "handleMessage: " + msg.getData().getString("result"));
            } else if (msg.what == 111) {
                Toast.makeText(mContext, "已是最新版本!", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 222) {
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setIcon(R.mipmap.icon) // 设置标题的图片
                        .setTitle("checkUpdate") // 设置对话框的标题
                        .setMessage("检测到应用有新版本，请前往浏览器更新") // 设置对话框的内容
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }) // 设置对话框的按钮
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openBrowser(mContext, tmpUrl+"/upgrade/update?version=" + latestVersion + "&platform=Local");
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
                Toast.makeText(mContext, "检测到有新版本，请前往浏览器下载最新版。", Toast.LENGTH_SHORT).show();
            }
            else {
                System.out.println("else");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fram, new ZhuyeFragment());
        fragmentTransaction.commit();
        mainMenu.setOnCheckedChangeListener(this::onCheckedChanged);
        // 测量屏幕长宽
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        CacheUtil.put(CacheConst.KEY_SCREEN_WIDTH, dm.widthPixels);
        CacheUtil.put(CacheConst.KEY_SCREEN_HEIGHT, dm.heightPixels);
        CacheUtil.put(CacheConst.KEY_SCREEN_DPI, dm.densityDpi);
    }

    private void init() {
        mainMenu = findViewById(R.id.main_select_menu);
        menu = findViewById(R.id.menu);
        qrCode = findViewById(R.id.qr_code);
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.main_zhuye: {
                changeFragment(new ZhuyeFragment(), true);
                break;
            }
            case R.id.main_tishi: {
                changeFragment(new TishiFragment(), true);
                break;
            }
            case R.id.main_setting: {
                changeFragment(new SettingFragment(), true);
                break;
            }
        }
    }
    public void changeFragment(Fragment fragment, boolean isFisrt) {
        fragmentManager = getSupportFragmentManager();
        // 开启事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fram, fragment);
        if (!isFisrt) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }
    private void checkUpdate() throws IOException, PackageManager.NameNotFoundException {
        String localVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        Log.e("TWT", "localVersion: " + localVersion);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = tmpUrl + "/upgrade/hint?version=" + localVersion + "&platform=Local";
        // 创建一个request对象
        Request request = new Request.Builder()
                .url(url)
                .build();
        // 执行和回调
        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                Log.e("TWT", "onFailure: " + e);
            }
            public void onResponse(Call call, Response response)
                    throws IOException {
                String str = response.body().string();
                System.out.println("OkHttp的get()请求方式" + str);
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
    private void downApk() {
        final String downUrl = tmpUrl + "/upgrade/update?version=" + latestVersion + "&platform=Local";
        final Message msg = new Message();
        msg.what = 101;
        final Bundle bundle = new Bundle();
        Request request = new Request.Builder().url(downUrl).build();
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000, TimeUnit.MILLISECONDS)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                bundle.putString("result", "下载失败！");
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    String path;
                    if (Build.VERSION.SDK_INT > 29) {
                        path = mContext.getExternalFilesDir(null).getAbsolutePath();
                    } else {
                        path = Environment.getExternalStorageDirectory().getPath();
                    }
                    File fapk = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "local.apk");
                    Log.e("TWT", "fapk: " + fapk);
                    sink = Okio.sink(fapk);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());
                    bufferedSink.close();
                    sink.close();
                    bundle.putString("result", "下载成功！");
                    installApk(fapk);
                } catch (Exception e) {
                    bundle.putString("result", "error: " + e.getMessage());
                } finally {
                    if (bufferedSink != null) {
                        bufferedSink.close();
                    }
                    if(sink != null) {
                        sink.close();
                    }
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }
        });
    }
    public static void openBrowser(Context context,String url){
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(context.getApplicationContext(), "请下载浏览器", Toast.LENGTH_SHORT).show();
        }
    }

    private void installApk(File fapk){
        if (!fapk.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 安装完成后打开新版本
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 给目标应用一个临时授权
        System.out.println("00002");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 判断版本大于等于7.0
            // 如果SDK版本>=24，即：Build.VERSION.SDK_INT >= 24，使用FileProvider兼容安装apk
            String packageName = mContext.getApplicationContext().getPackageName();
            String authority = new StringBuilder(packageName).append(".fileprovider").toString();
            Log.e("TWT", "authority: "+authority);
            Uri apkUri = FileProvider.getUriForFile(mContext, authority, fapk);
            Log.e("TWT", "apkUri: "+apkUri);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(fapk), "application/vnd.android.package-archive");
        }
        Log.e("TWT", "intent: "+intent.toString());
        Log.e("TWT", " mContext.startActivity(intent);");
        mContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid()); // 安装完之后会提示”完成”“打开”。
    }
}