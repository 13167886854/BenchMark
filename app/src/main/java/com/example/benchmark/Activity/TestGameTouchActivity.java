package com.example.benchmark.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.example.benchmark.R;
import com.example.benchmark.utils.GameTouchUtil;

public class TestGameTouchActivity extends AppCompatActivity {
    private static final String TAG = "TWT";
    //private RetrieverUtil util = RetrieverUtil.getUtil();
    private GameTouchUtil gameTouchUtil = GameTouchUtil.getGameTouchUtil();

    private final int HANDLING_FRAME = 0;
    private final int TEST_COMPLETED = 1;

    private int last_rgb = 0,this_rgb = 0;
    private long count = 0,duration = 0;

    private String path;

    private TextView handling_tv;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLING_FRAME:
                    handling_tv.setText("测试中----("+msg.arg1+"/"+count+")");
                    break;
                case TEST_COMPLETED:
                    handling_tv.setText(gameTouchUtil.getDelayTime()+"\navgTime:"+gameTouchUtil.getAvgTime(10));
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_game_touch);

        handling_tv = findViewById(R.id.handling);
        String[] permissions = {
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE"};
        if(Build.VERSION.SDK_INT>=23){
            requestPermissions(permissions,1);
        }




        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                String path = "/storage/emulated/0/ScreenRecorder/dotest.mp4";
                path = getIntent().getStringExtra("path");
                retriever.setDataSource(path);
                String count_s = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
                String duration_s = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                Log.d(TAG, "duration_s: "+duration_s);
                count = Long.valueOf(count_s);
                duration = Long.valueOf(duration_s);
                float frameTime = (float)duration  / count;

                Bitmap bitmap1 = retriever.getFrameAtIndex(0);
                this_rgb = bitmap1.getPixel(420,1255);
                last_rgb = this_rgb;

                for(int i=1;i<count;i++){
                    Bitmap bitmap = retriever.getFrameAtIndex(i);
                    this_rgb = bitmap.getPixel(420,1255);
                    Log.d(TAG, "Image: <"+i+"> :"+this_rgb);
                    Message m = new Message();
                    m.arg1 = (i+1);
                    m.what = HANDLING_FRAME;
                    handler.sendMessage(m);


                    if(Math.abs(this_rgb-last_rgb)>10000000){
                        Log.e(TAG, "rgb changed!!! : " +i);
                        gameTouchUtil.getUpdateTime(gameTouchUtil.getVideoStartTime()+(long)(duration*((float)(i)/count)));
                        gameTouchUtil.getTestTime((long)(duration*((float)(i)/count)));
                    }
                    last_rgb = this_rgb;
                }


//                for(int i=0;i<count;i++){
//                    Bitmap bitmap = retriever.getFrameAtIndex(i);
//                    this_rgb = bitmap.getPixel(420,1255);
//                    Log.d(TAG, "Image: <"+i+"> :"+this_rgb);
//                    Message m = new Message();
//                    m.arg1 = (i+1);
//                    m.what = HANDLING_FRAME;
//                    handler.sendMessage(m);
//
//                    if(Math.abs(this_rgb-last_rgb)>10000000){
//                        //Log.d(TAG, "rgb changed!!! :" +i);
//                        Log.e(TAG, "RGB BIG-changed when TIME is:" +frameTime*(i+1));
//                        //gameTouchUtil.getUpdateTime(gameTouchUtil.getVideoStartTime()+(long)(duration*((float)(i)/count)));
//                        gameTouchUtil.getUpdateTime(gameTouchUtil.getVideoStartTime() + (long)(frameTime*(i+1)));
//                    }
//                    last_rgb = this_rgb;
//                }


//                gameTouchUtil.printFrameUpdateTime();
//                gameTouchUtil.printAutoTapTime();
//
//                gameTouchUtil.printDelayTime();
//                gameTouchUtil.printAvgTime(20);
                gameTouchUtil.printTestTime();

                handler.sendEmptyMessage(TEST_COMPLETED);

                Log.d(TAG, "gameTouchUtil.readyToTapTime:"+ gameTouchUtil.readyToTapTime);
                Log.d(TAG, "gameTouchUtil.vedioStartTime:"+ gameTouchUtil.getVideoStartTime());


            }
        }).start();;










    }
}