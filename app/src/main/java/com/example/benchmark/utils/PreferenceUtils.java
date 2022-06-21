package com.example.benchmark.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


//用来判断用户是不是第一次登录，如果是第一次登录，则跳转到引导
//页面，如果不是，则跳转到主界面
public class PreferenceUtils {
    private  static final String FILE_NAME="BenchMark";
    private static final String MODE_NAME="isFirst";

    //获取是否市第一次进入app的Boolean值
    public static  boolean get_isFirst_come(Context context){
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getBoolean(MODE_NAME,false);
    }

    //写入Boolean的值
    public static void put_isFirst_boolean(Context context, boolean isFirst){
        //获取用户偏好写入对象
        @SuppressLint("WrongConstant")
        SharedPreferences.Editor editor= context.getSharedPreferences(FILE_NAME, Context.MODE_APPEND).edit();
        //写入boolean值
        editor.putBoolean(MODE_NAME,isFirst);
        editor.apply();
    }

}
