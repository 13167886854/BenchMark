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

public class SettingFragment extends Fragment {
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
                Toast.makeText(getContext(),"已是最新版本!",Toast.LENGTH_SHORT).show();
            }else if(msg.what==222){
                AlertDialog dialog = new AlertDialog.Builder(getContext())
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
                                openBrowser(getContext(),tmp_url+"/upgrade/update?version="+latestVersion+"&platform=Local");
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();

                Toast.makeText(getContext(),"检测到有新版本，请前往浏览器下载最新版。",Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        RelativeLayout relativeLayout = view.findViewById(R.id.set_shiyongshuom);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ShuoMingActivity.class));
            }
        });
        RelativeLayout paraSet = view.findViewById(R.id.set_paraSet);
        paraSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
//        RelativeLayout history = view.findViewById(R.id.history);
//        paraSet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
//            }
//        });
        RelativeLayout set_system_update = view.findViewById(R.id.set_system_update);
        set_system_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    checkUpdate();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });


        return view;
    }


    private void checkUpdate() throws IOException, PackageManager.NameNotFoundException {
        String localVersion = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
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

    public static void openBrowser(Context context, String url){
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
}
