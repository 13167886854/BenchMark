package com.example.benchmark.activity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

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
import com.example.benchmark.utils.FpsUtils;
import com.example.benchmark.render.GLVideoRenderer;
import com.example.benchmark.utils.ScoreUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class TestSMActivity extends AppCompatActivity {
    private GLSurfaceView glView;
    private GLVideoRenderer glVideoRenderer;


    private FpsUtils fpsUtil = FpsUtils.getFpsUtils();
    private TextView textInfo;
    private FpsRunnalbe fpsRunnalbe;

    // 0表示默认视频测试  1表示录屏测试
    private int launchMode = 0;

    private int FILE_REQUEST_CODE = 50;
    private Context mContext = this;
    private String path = "";

    // 百分比显示
    private DecimalFormat df = new DecimalFormat("0.00%");

    // SharePreferences测试结果保存
    private String STORE_NAME = "LastTestResult";

    // 判断是否正在测试
    private boolean isTesting = false;
    private String eachFps = "";

    public static void start(Context context, String path) {
        Intent intent = new Intent(context, TestSMActivity.class);
        intent.putExtra("path", path);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_smactivity);


        // 获取显示文本
        textInfo = findViewById(R.id.textview);

        // 初始化监听器
        fpsRunnalbe = new FpsRunnalbe();

        // 初始化视频播放器
        glView = findViewById(R.id.surface_view);
        glView.setEGLContextClientVersion(2);
        glVideoRenderer = new GLVideoRenderer(this);

        // 创建renderer
        glView.setRenderer(glVideoRenderer);

        // 设置renderer
        glVideoRenderer.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("TWT", "onCompletion: 播放结束");
                try {
                    StopTest();
                } catch (IOException e) {
                    Log.e("TestSMActivity: ", e.toString());
                }
            }
        });
        path = getIntent().getStringExtra("path");
        try {
            glVideoRenderer.getMediaPlayer().reset();
            glVideoRenderer.getMediaPlayer().setDataSource(TestSMActivity.this, Uri.parse(path));
            glVideoRenderer.getMediaPlayer().prepare();
        } catch (IOException e) {
            Log.e("TestSMActivity: ", e.toString());
        }

        //自动开始
        try {
            DoTest();
        } catch (IOException e) {
            Log.e("TestSMActivity: ", e.toString());
        }
    }

    public void DoTest() throws IOException {
        isTesting = true;
        Log.d("TWT", "DoTest: 开始播放");
        fpsUtil.startMonitor(fpsRunnalbe);
        glVideoRenderer.getMediaPlayer().start();
    }

    public void StopTest() throws IOException {
        isTesting = false;

        // 停止监听和播放
        fpsUtil.stopMonitor(fpsRunnalbe);
        glVideoRenderer.getMediaPlayer().stop();
        glVideoRenderer.getMediaPlayer().prepare();

        ScoreUtil.calcAndSaveFluencyScores(
                getRoundNumber((float) fpsUtil.getAvergeFps()),
                getRoundNumber((float) fpsUtil.getFrameShakingRate()),
                getRoundNumber((float) fpsUtil.getLowFrameRate()),
                getRoundNumber((float) fpsUtil.getFrameIntervalTime()),
                fpsUtil.getJankCount(),
                getRoundNumber((float) fpsUtil.getShtutterRate()),
                eachFps
        );
        Intent intent = new Intent(TestSMActivity.this, CePingActivity.class);
        intent.putExtra("isFluencyUntested", false);
        startActivity(intent);
    }

    private float getRoundNumber(float a) {
        BigDecimal bd = new BigDecimal(a);
        float res = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return res;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 判断是否正在测试
        if (isTesting) {
            glVideoRenderer.getMediaPlayer().stop();
            try {
                StopTest();
            } catch (IOException e) {
                Log.e("TestSMActivity: ", e.toString());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    class FpsRunnalbe implements Runnable {
        @Override
        public void run() {
            fpsUtil.updateBeforeGetInfo();

            // 打印测试中数据到TextView上
            if (eachFps.equals("")) {
                eachFps += String.valueOf(fpsUtil.getCount());
            } else {
                eachFps += "," + String.valueOf(fpsUtil.getCount());
            }
            Log.d("TWT", "runing.......... ");
            textInfo.setText(
                    "当前FPS" + fpsUtil.getCount() + "帧/秒" + "\n"
                            + "帧抖动率" + String.format("%.2f", fpsUtil.getFrameShakingRate()) + "\n"
                            + "低帧率" + df.format(fpsUtil.getLowFrameRate()) + "\n"
                            + "当前帧间隔" + fpsUtil.getIntervalTime() + "ms" + "\n"
                            + "jank发生次数" + fpsUtil.getJankCount() + "\n"
                            + "卡顿率" + df.format(fpsUtil.getShtutterRate()) + "\n"
                            + "总帧数" + fpsUtil.getTotalCount() + "\n"
                            + "测试时长" + (fpsUtil.getSizeOfCountArray() + 1) + "s" + "\n"
            );
            fpsUtil.updateAfterGetInfo();

            //记录绘制次数和绘制时间，用于计算FPS
            FpsUtils.mainHandler.postDelayed(this, FpsUtils.FPS_INTERVAL_TIME);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == FILE_REQUEST_CODE) {
            if (data.getData() != null) {
                try {
                    Uri uri = data.getData();
                    Log.d("TWT", "uri:" + uri.toString());
                    try {
                        glVideoRenderer.getMediaPlayer().reset();
                        glVideoRenderer.getMediaPlayer().setDataSource(TestSMActivity.this, uri);
                        glVideoRenderer.getMediaPlayer().prepare();
                    } catch (IOException e) {
                        Log.e("TestSMActivity: ", e.toString());
                    }
                } catch (Exception e) {
                    Log.e("TestSMActivity: ", e.toString());
                }
            }
        }
    }
}