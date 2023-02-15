package com.example.benchmark.Activity;

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

    private int launchMode = 0;  //0表示默认视频测试  1表示录屏测试

    private int FILE_REQUEST_CODE = 50;

    private Context mContext = this;

    private String path = "";

    //百分比显示
    private DecimalFormat df =  new DecimalFormat("0.00%");
    //SharePreferences测试结果保存
    private String STORE_NAME = "LastTestResult";
    //判断是否正在测试
    private boolean isTesting = false;

    private String eachFps = "";

    public static void start(Context context, String path){
        Intent intent = new Intent(context,TestSMActivity.class);
        intent.putExtra("path",path);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_smactivity);


        //获取显示文本
        textInfo = findViewById(R.id.textview);
        //初始化监听器
        fpsRunnalbe = new FpsRunnalbe();

        //设置启动模式
       // Toast.makeText(this,"mode"+this.launchMode,Toast.LENGTH_SHORT).show();

//        try {
//            glVideoRenderer.getMediaPlayer().setDataSource(TestSMActivity.this,Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video720p));
//            glVideoRenderer.getMediaPlayer().prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //初始化视频播放器
        glView = findViewById(R.id.surface_view);
        glView.setEGLContextClientVersion(2);
        glVideoRenderer = new GLVideoRenderer(this);//创建renderer
        glView.setRenderer(glVideoRenderer);//设置renderer
        glVideoRenderer.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("TWT", "onCompletion: 播放结束");
                try {
                    StopTest();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        path = getIntent().getStringExtra("path");
        //path = "/storage/emulated/0/ScreenRecorder/1657851464717.mp4";
        //Log.d("TWT", "path: "+path);
        try {
            glVideoRenderer.getMediaPlayer().reset();
            glVideoRenderer.getMediaPlayer().setDataSource(TestSMActivity.this,Uri.parse(path));
            //glVideoRenderer.getMediaPlayer().setDataSource(TestSMActivity.this,path);
            //glVideoRenderer.getMediaPlayer().setDataSource(path);
            glVideoRenderer.getMediaPlayer().prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //自动开始
        try {
            DoTest();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




//    private String queryInfoByFileName(String fileName){
//        SQLiteDatabase db = mhelper.getWritableDatabase();
//        // Cursor cursor = db.query("testinfo",null,"fileName ="+fileName,null,null,null,null);
//        Cursor cursor = db.rawQuery("select * from testinfos where fileName='"+fileName+"'",null);
//        if(cursor.getCount()==0){
//            return "暂无测试信息！！";
//        }else{
//            cursor.moveToFirst();
//            int id = cursor.getColumnIndex("id");
//            int datetime = cursor.getColumnIndex("datetime");
//            int avergeFPS = cursor.getColumnIndex("avergeFPS");
//            int frameShakingRate = cursor.getColumnIndex("frameShakingRate");
//            int lowFrameRate = cursor.getColumnIndex("lowFrameRate");
//            int frameInterval = cursor.getColumnIndex("frameInterval");
//            int jankCount = cursor.getColumnIndex("jankCount");
//            int stutterRate = cursor.getColumnIndex("stutterRate");
//            int totalFrame = cursor.getColumnIndex("totalFrame");
//            int durationTime = cursor.getColumnIndex("durationTime");
//            String res =
//                    "\n测试日期:"+cursor.getString(datetime)
//                            +"\n平均fps:"+cursor.getString(avergeFPS)
//                            +"\n帧抖动率:"+cursor.getString(frameShakingRate)
//                            +"\n低帧率:"+cursor.getString(lowFrameRate)
//                            +"\n平均帧间隔:"+cursor.getString(frameInterval)
//                            +"\njank发生次数:"+cursor.getString(jankCount)
//                            +"\n卡顿率:"+cursor.getString(stutterRate)
//                            +"\n总帧数:"+cursor.getString(totalFrame)
//                            +"\n测试总时长:"+cursor.getString(durationTime)
//                    ;
//            return res;
//        }
//    }




    public void DoTest() throws IOException {
        isTesting = true;
        Log.d("TWT", "DoTest: 开始播放");
        //glVideoRenderer.getMediaPlayer().setDataSource(this,Uri.parse("android.resource://"+this.getPackageName()+"/"+R.raw.video4k));
        fpsUtil.startMonitor(fpsRunnalbe);
        glVideoRenderer.getMediaPlayer().start();
    }

    public void StopTest() throws IOException {
        isTesting = false;

        //停止监听和播放
        fpsUtil.stopMonitor(fpsRunnalbe);
        glVideoRenderer.getMediaPlayer().stop();
        glVideoRenderer.getMediaPlayer().prepare();

        ScoreUtil.calcAndSaveFluencyScores(
                getRoundNumber((float)fpsUtil.getAvergeFps()),
                getRoundNumber((float)fpsUtil.getFrameShakingRate()),
                getRoundNumber((float)fpsUtil.getLowFrameRate()),
                getRoundNumber((float)fpsUtil.getFrameIntervalTime()),
                fpsUtil.getJankCount(),
                getRoundNumber((float)fpsUtil.getShtutterRate()),
                eachFps
                );
        Intent intent = new Intent(TestSMActivity.this,CePingActivity.class);
        intent.putExtra("isFluencyUntested",false);
        startActivity(intent);



        //glVideoRenderer.getMediaPlayer().seekTo(0);
        //输出结果数据到TextView
//        String result = " 平均FPS "+String.format("%.2f",fpsUtil.getAvergeFps())+"\n"
//                +" 帧抖动率 "+String.format("%.2f",fpsUtil.getFrameShakingRate())+"\n"
//                +" 低帧率 "+df.format(fpsUtil.getLowFrameRate())+"\n"
//                +" 平均帧间隔 "+String.format("%.2f",fpsUtil.getFrameIntervalTime())+"ms"+"\n"
//                +" jank发生次数 "+fpsUtil.getJankCount()+"\n"
//                +" 卡顿率 "+df.format(fpsUtil.getShtutterRate())+"\n"
//                +" 总帧数 "+fpsUtil.getTotalCount()+"\n"
//                +" 测试总时长 "+(fpsUtil.getDurationTime())+"ms\n";
//                +"低帧数:"+fpsUtil.getLowCount()+"\n"
//                +"jank率："+df.format(fpsUtil.getJankRate())+"\n"
//                +"卡顿时长："+fpsUtil.getShutterTime()+"ms\n"
       // textInfo.setText(result);



//        if(launchMode==0){
//            SharedPreferences lastTestResult =  getSharedPreferences(STORE_NAME,MODE_PRIVATE);
//            String type = lastTestResult.getString("type","");
//            Log.d("TWT", "type: "+type);
            //保存测试结果到JsonData中

//            try {
//                JsonData.data.put("avergeFPS",String.format("%.2f",fpsUtil.getAvergeFps()));
//                JsonData.data.put("frameShakingRate",String.format("%.2f",fpsUtil.getFrameShakingRate()));
//                JsonData.data.put("lowFrameRate",String.format("%.2f",fpsUtil.getLowFrameRate()));
//                JsonData.data.put("frameInterval",String.format("%.2f",fpsUtil.getFrameIntervalTime()));
//                JsonData.data.put("jankCount",""+fpsUtil.getJankCount());
//                JsonData.data.put("stutterRate",String.format("%.2f",fpsUtil.getShtutterRate()));
//                JsonData.data.put("totalFrame",""+fpsUtil.getTotalCount());
//                JsonData.data.put("durationTime",(fpsUtil.getDurationTime()));
//                JsonData.SmoothData = true;
//                Toast.makeText(TestSMActivity.this,"流畅性测试数据已保存...",Toast.LENGTH_SHORT).show();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            //上传测试结果到服务器
           /* OkHttpClient client = new OkHttpClient();
            String time = (String) new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
            FormBody formBody = new FormBody.Builder()
                    .add("type",type)
                    .add("avergeFPS",String.format("%.2f",fpsUtil.getAvergeFps()))
                    .add("frameShakingRate",String.format("%.2f",fpsUtil.getFrameShakingRate()))
                    .add("lowFrameRate",df.format(fpsUtil.getLowFrameRate()))
                    .add("frameInterval",String.format("%.2f",fpsUtil.getFrameIntervalTime())+"ms")
                    .add("jankCount",""+fpsUtil.getJankCount())
                    .add("stutterRate",df.format(fpsUtil.getShtutterRate()))
                    .add("totalFrame",""+fpsUtil.getTotalCount())
                    .add("durationTime",(fpsUtil.getDurationTime())+"ms")
                    .add("link_key",ConfigurationUtils.Link_key)
                    .add("date",time)
                    .build();
            final Request request = new Request.Builder()
                    .url(ConfigurationUtils.URL+"/updateSmoothTestData")
                    .post(formBody)
                    .build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    Log.e("TWT", e.toString());
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String res = response.body().string();
                    Looper.prepare();
                    Toast.makeText(TestSMActivity.this,res,Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    //Log.d("TWT", "res: "+res);
                }
            });*/


            //保存测试结果到sharepreferences
            //SharedPreferences lastTestResult =  getSharedPreferences(STORE_NAME,MODE_PRIVATE);
//            SharedPreferences.Editor editor = lastTestResult.edit();
//            editor.putString("result",result);
//            editor.commit();
//        }else if(launchMode==1){
//            //数据库保存测试结果
//            if(mhelper.isTestBefore(getIntent().getStringExtra("fileName"))){
//                //更新测试结果数据
//                mhelper.updateData(
//                        getIntent().getStringExtra("fileName"),
//                        ""+String.format("%.2f",fpsUtil.getAvergeFps()),
//                        ""+String.format("%.2f",fpsUtil.getFrameShakingRate()),
//                        ""+df.format(fpsUtil.getLowFrameRate()),
//                        ""+String.format("%.2f",fpsUtil.getFrameIntervalTime())+"ms",
//                        ""+fpsUtil.getJankCount(),
//                        ""+df.format(fpsUtil.getShtutterRate()),
//                        ""+fpsUtil.getTotalCount(),
//                        ""+fpsUtil.getDurationTime()
//                );
//            }else{
//                //插入测试结果数据
//                mhelper.insertData(
//                        getIntent().getStringExtra("fileName"),
//                        ""+String.format("%.2f",fpsUtil.getAvergeFps()),
//                        ""+String.format("%.2f",fpsUtil.getFrameShakingRate()),
//                        ""+df.format(fpsUtil.getLowFrameRate()),
//                        ""+String.format("%.2f",fpsUtil.getFrameIntervalTime())+"ms",
//                        ""+fpsUtil.getJankCount(),
//                        ""+df.format(fpsUtil.getShtutterRate()),
//                        ""+fpsUtil.getTotalCount(),
//                        ""+fpsUtil.getDurationTime()
//                );
//            }
//        }

    }

    private float getRoundNumber(float a){
            BigDecimal bd  =   new BigDecimal(a);
            float  res  =  bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            return res;
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

    class FpsRunnalbe implements Runnable{
        @Override
        public void run() {
            fpsUtil.updateBeforeGetInfo();
            //打印测试中数据到TextView上
            if(eachFps.equals("")){
                eachFps += String.valueOf(fpsUtil.getCount());
            }else {
                eachFps += ","+String.valueOf(fpsUtil.getCount());
            }
            Log.d("TWT", "runing.......... ");
            textInfo.setText(
                    "当前FPS"+fpsUtil.getCount()+"帧/秒\n"
                    +"帧抖动率"+String.format("%.2f",fpsUtil.getFrameShakingRate())+"\n"
                    +"低帧率"+df.format(fpsUtil.getLowFrameRate())+"\n"
                    +"当前帧间隔"+fpsUtil.getIntervalTime()+"ms\n"
                    +"jank发生次数"+fpsUtil.getJankCount()+"\n"
                    +"卡顿率"+df.format(fpsUtil.getShtutterRate())+"\n"
                    +"总帧数"+fpsUtil.getTotalCount()+"\n"
                    +"测试时长"+(fpsUtil.getSizeOfCountArray()+1)+"s\n"
//                            +"低帧数:"+fpsUtil.getLowCount()+"\n"
//                            +"帧间隔："+String.format("%.2f",fpsUtil.getFrameIntervalTime())+"ms"+"\n"
//                            +"jank率："+df.format(fpsUtil.getJankRate())+"\n"
//                            +"卡顿时长："+fpsUtil.getShutterTime()+"ms\n"
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
                    Log.d("TWT", "uri:"+uri.toString());
                    try {
                        glVideoRenderer.getMediaPlayer().reset();
                        glVideoRenderer.getMediaPlayer().setDataSource(TestSMActivity.this,uri);
                        glVideoRenderer.getMediaPlayer().prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //radio_group.clearCheck();
                    //String path = UriUtil.getPath(this, uri);
                    //String fileType = path.substring(path.indexOf(".") + 1);
                    //if (!fileType.contains("txt")) {
                        //不是想要的txt文件
                    //    return;
                    //}
                    //file = new File(path);
                } catch (Exception e) {

                }
            }
        }
    }


}