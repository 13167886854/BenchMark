package com.example.benchmark.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.benchmark.R;
import com.example.benchmark.utils.CheckVedioUpdateFrameRenderer;
import com.example.benchmark.utils.GameTouchUtil;

import java.io.IOException;
import java.text.DecimalFormat;

public class CheckFrameUpateActivity extends AppCompatActivity {
    private GLSurfaceView glView;
    private CheckVedioUpdateFrameRenderer glVideoRenderer;

    private GameTouchUtil gameTouchUtil = GameTouchUtil.getGameTouchUtil();

    private TextView textInfo;

    private int FILE_REQUEST_CODE = 50;

   // private final int
    private Context mContext = this;
    private String path = "";

    //百分比显示
    private DecimalFormat df =  new DecimalFormat("0.00%");
    //SharePreferences测试结果保存
    private String STORE_NAME = "LastTestResult";
    //判断是否正在测试
    private boolean isTesting = false;






    public static void start(Context context, String path){
        Intent intent = new Intent(context, CheckFrameUpateActivity.class);
        intent.putExtra("path",path);
        context.startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkframeupdate);


        //获取显示文本
        textInfo = findViewById(R.id.textview);


        //初始化视频播放器
        glView = findViewById(R.id.play_surface_view);
        glView.setEGLContextClientVersion(2);
        glVideoRenderer = new CheckVedioUpdateFrameRenderer(this);//创建renderer
        glView.setRenderer(glVideoRenderer);//设置renderer
        glVideoRenderer.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("TWT", "onCompletion: 播放结束");
                try {
                    StopTest();
                } catch (IOException e) {
                    Log.e("TWT", e.toString());
                }
            }
        });
        path = getIntent().getStringExtra("path");
        //path = "/storage/emulated/0/ScreenRecorder/1657851464717.mp4";
        //Log.d("TWT", "path: "+path);
        try {
            glVideoRenderer.getMediaPlayer().reset();
            glVideoRenderer.getMediaPlayer().setDataSource(CheckFrameUpateActivity.this,Uri.parse(path));
            //glVideoRenderer.getMediaPlayer().setDataSource(TestSMActivity.this,path);
            //glVideoRenderer.getMediaPlayer().setDataSource(path);
            glVideoRenderer.getMediaPlayer().prepare();
        } catch (IOException e) {
            Log.e("TWT", e.toString());
        }

        //自动开始
        try {
            DoTest();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void DoTest() throws IOException {
        isTesting = true;
        Log.d("TWT", "DoTest: 开始播放");
        Log.d("TWT", "DoTest: 开始播放时间戳:"+System.currentTimeMillis());
        glVideoRenderer.getMediaPlayer().start();
    }

    public void StopTest() throws IOException {
        isTesting = false;
        glVideoRenderer.getMediaPlayer().stop();
        glVideoRenderer.getMediaPlayer().prepare();

        gameTouchUtil.printFrameUpdateTime();
        gameTouchUtil.printAutoTapTime();
        gameTouchUtil.clear();

        Intent intent = new Intent(CheckFrameUpateActivity.this,CePingActivity.class);
        intent.putExtra("isFluencyUntested",false);
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        //判断是否正在测试
        if(isTesting){
            glVideoRenderer.getMediaPlayer().stop();
            try {
                StopTest();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //glVideoRenderer.getMediaPlayer().start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == FILE_REQUEST_CODE) {
            if (data.getData() != null) {
                try {
                    Uri uri = data.getData();
                    Log.d("TWT", "uri:"+uri.toString());
                    try {
                        glVideoRenderer.getMediaPlayer().reset();
                        glVideoRenderer.getMediaPlayer().setDataSource(CheckFrameUpateActivity.this,uri);
                        glVideoRenderer.getMediaPlayer().prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {

                }
            }
        }
    }


}