package com.example.benchmark.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.benchmark.R;
import com.example.benchmark.Service.BothRecordService;
import com.example.benchmark.utils.ApkUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.ServiceUtil;

import java.util.Timer;
import java.util.TimerTask;



public class TestActivity extends AppCompatActivity {
    private static final int RECORD_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;
    private static final int AUDIO_REQUEST_CODE = 103;
    private static final int REQUEST_FX = 1;


    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private ServiceConnection connection;
    private BothRecordService bothRecordService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        startFxService();
        startRecordService();


    }

    public void onClick(View view) {
        //开始录制
        Toast.makeText(TestActivity.this,"开始录制",Toast.LENGTH_SHORT);
        bothRecordService.startVideoRecord();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                bothRecordService.stopVideoRecord();
                Looper.prepare();
                Toast.makeText(TestActivity.this,"录制结束",Toast.LENGTH_SHORT);
            }
        };
        timer.schedule(task,3000);
    }



    private void startFxService() {
        MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager)
                this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mMediaProjectionManager != null) {
            this.startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_FX);
        }
    }

    private void startRecordService(){
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                BothRecordService.RecordBinder binder = (BothRecordService.RecordBinder) service;
                bothRecordService = binder.getRecordService();
                //start.setText(recordService.isRunning() ? "停止录制" : "开始录制");
            }
            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };
        //录屏权限申请
        if (ContextCompat.checkSelfPermission(TestActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(TestActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
        }
        Intent intent = new Intent(this, BothRecordService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, RECORD_REQUEST_CODE);

        IntentFilter filter=new IntentFilter();//注册广播接收器
        filter.addAction("com.example.benchmark");
        this.registerReceiver(new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onReceive(Context context, Intent intent) {
                if(bothRecordService.isRunning()==false){
                    bothRecordService.startVideoRecord();
                    Log.e("TWT", "run: startRecord" );
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            bothRecordService.stopVideoRecord();
                            Log.e("TWT", "run: stopRecord" );
                        }
                    };
                    timer.schedule(task,3000);
                    //bothRecordService.startVideoRecord();
                }
            }
        },filter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_CODE || requestCode == AUDIO_REQUEST_CODE) {
            //Log.e("TWT", "onRequestPermissionsResult: "+grantResults.length );
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            Log.e("TWT", "onActivityResult: 456");
            bothRecordService.setMediaProject(mediaProjection);
        }else if (requestCode == REQUEST_FX && resultCode == RESULT_OK) {
            ServiceUtil.startFxService(this, "云手机", resultCode, data, false,true);
        }
    }

}