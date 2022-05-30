package com.example.benchmark.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
<<<<<<< HEAD
=======
import android.view.Window;
import android.view.WindowManager;
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.benchmark.R;
<<<<<<< HEAD
=======
import com.example.benchmark.utils.PreferenceUtils;

>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
public class StartActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
=======
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
        setContentView(R.layout.activity_start);
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                return true;

            }
        }).sendEmptyMessageDelayed(1,1000);
    }
}
