package com.example.benchmark.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.benchmark.Data.YinHuaData;
import com.example.benchmark.Fragment.SettingFragment;
import com.example.benchmark.Fragment.TishiFragment;
import com.example.benchmark.Fragment.ZhuyeFragment;
import com.example.benchmark.R;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private FragmentManager fragmentManager;
    private RadioGroup main_menu;
    private ImageButton menu, qr_code;
    private Context mContext = this;

    private String tmp_url = "https://1d2f09a7.r2.cpolar.top";

    private String latestVersion = null;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==101){
//                Toast.makeText(this,msg.getData().getString("result"),Toast.LENGTH_SHORT).show();
                Log.d("TWT", "handleMessage: "+msg.getData().getString("result"));
            }else if(msg.what==111){
                Toast.makeText(mContext,"已是最新版本!",Toast.LENGTH_SHORT).show();
            }else if(msg.what==222){
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setIcon(R.mipmap.icon)//设置标题的图片
                        .setTitle("checkUpdate")//设置对话框的标题
                        .setMessage("检测到应用有新版本，请前往浏览器更新")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(MainActivity.this, "点击了取消按钮", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(MainActivity.this, "点击了确定的按钮", Toast.LENGTH_SHORT).show();
                                openBrowser(mContext,tmp_url+"/upgrade/update?version="+latestVersion+"&platform=Local");
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();

                Toast.makeText(mContext,"检测到有新版本，请前往浏览器下载最新版。",Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            checkUpdate();
        } catch (IOException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        init();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fram, new ZhuyeFragment());
        fragmentTransaction.commit();
        main_menu.setOnCheckedChangeListener(this::onCheckedChanged);
        // 测量屏幕长宽
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        CacheUtil.put(CacheConst.KEY_SCREEN_WIDTH, dm.widthPixels);
        CacheUtil.put(CacheConst.KEY_SCREEN_HEIGHT, dm.heightPixels);
        CacheUtil.put(CacheConst.KEY_SCREEN_DPI, dm.densityDpi);

    }

    private void init() {
        main_menu = findViewById(R.id.main_select_menu);
        menu = findViewById(R.id.menu);
        qr_code = findViewById(R.id.qr_code);
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
                ChangeFragment(new ZhuyeFragment(), true);
                break;
            }
            case R.id.main_tishi: {
                ChangeFragment(new TishiFragment(), true);
                break;
            }
            case R.id.main_setting: {
                ChangeFragment(new SettingFragment(), true);
                break;
            }

        }
    }

    public void ChangeFragment(Fragment fragment, boolean isFisrt) {
        fragmentManager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fram, fragment);
        if (!isFisrt) {
            fragmentTransaction.addToBackStack(null);
            ;
        }
        fragmentTransaction.commit();
    }

    private void checkUpdate() throws IOException, PackageManager.NameNotFoundException {
        String localVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        Log.e("TWT", "localVersion: "+localVersion );
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("http://69e1dc69.r3.vip.cpolar.cn/upgrade/hint")
//                .build();
//        Response response = client.newCall(request).execute();
//        String responseData = response.body().string();
//
//        Log.e("TWT", "checkUpdate: "+responseData );

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = tmp_url+"/upgrade/hint?version="+localVersion+"&platform=Local";
        // 创建一个request对象
        Request request = new Request.Builder()
                .url(url)
                .build();
        // 执行和回调
        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                Log.e("TWT", "onFailure: "+e );

            }

            public void onResponse(Call call, Response response)
                    throws IOException {
                String str = response.body().string();
                System.out.println("OkHttp的get()请求方式" + str);
                if(str.equals("当前已是最新版本")){
                    //
                    Message msg = new Message();
                    msg.what = 111;
                    handler.sendMessage(msg);
                }else{
                    latestVersion = str;
                    Message msg = new Message();
                    msg.what = 222;
                    handler.sendMessage(msg);

                }
            }
        });
    }

    private void downApk(){
        //final String downUrl = CacheConst.GLOBAL_IP+"/getLocalApk";
        final String downUrl = tmp_url+"/upgrade/update?version="+latestVersion+"&platform=Local";
        final Message msg = new Message();
        msg.what = 101;
        final Bundle bundle = new Bundle();
        //这里有一些验证信息，我用的是webdav，如果你不需要，直接把header拿掉就好
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
                    //File fapk = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), downUrl.substring(downUrl.lastIndexOf("/") + 1));
                    //File fapk = new File(path, "local.apk");
                    File fapk = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "local.apk");
                    Log.e("TWT", "fapk: "+fapk );
                    sink = Okio.sink(fapk);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());
                    bufferedSink.close();
                    sink.close();
                    bundle.putString("result", "下载成功！");
                    installApk(fapk);
                }catch (Exception e){
                    bundle.putString("result", "error: " + e.getMessage());
                }finally {
                    if(bufferedSink != null) bufferedSink.close();
                    if(sink != null) sink.close();
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
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager()); // 打印Log   ComponentName到底是什么 L.d("componentName = " + componentName.getClassName());
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(context.getApplicationContext(), "请下载浏览器", Toast.LENGTH_SHORT).show();
        }
    }

    private void installApk(File fapk){
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(fapk), "application/vnd.android.package-archive");
//        startActivity(intent);

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Uri uri = FileProvider.getUriForFile(this, this.getPackageName() + ".fileprovider", fapk);
//            intent.setDataAndType(uri, "application/vnd.android.package-archive");
//        } else {
//            intent.setDataAndType(Uri.fromFile(fapk), "application/vnd.android.package-archive");
//        }
//        startActivity(intent);

        if (!fapk.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//安装完成后打开新版本
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 给目标应用一个临时授权
        System.out.println("00002");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
            //如果SDK版本>=24，即：Build.VERSION.SDK_INT >= 24，使用FileProvider兼容安装apk
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
        android.os.Process.killProcess(android.os.Process.myPid());//安装完之后会提示”完成” “打开”。

    }



}