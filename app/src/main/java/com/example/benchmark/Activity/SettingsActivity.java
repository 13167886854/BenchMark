package com.example.benchmark.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import com.example.benchmark.R;
import com.example.benchmark.utils.TapUtil;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "TWT";
    Button num ;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    private int TestNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        num = findViewById(R.id.num);
        sharedPreferences = getSharedPreferences("Setting", this.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        TestNum = sharedPreferences.getInt("TestNum",5);
        num.setText(String.valueOf(TestNum));
        Log.e(TAG, "onCreate+num: "+num );


    }


    public void sub(View view){
        Log.d(TAG, "sub");
        if(TestNum==1){
            return;
        }
        TestNum--;
        num.setText(String.valueOf(TestNum));
    }



    public void add(View view) {
        Log.d(TAG, "add: add");
        TestNum++;
        num.setText(String.valueOf(TestNum));

    }

    public void save(View view){
        TapUtil.mWholeMonitorNum = TestNum;
        editor.putInt("TestNum",TestNum);
        editor.apply();
    }
}