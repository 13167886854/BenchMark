package com.example.benchmark.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

public class ImageUtil {

    private static String text;

    public static String getTextFromImage(Context context, Bitmap bitmap) {
        Thread ocrImageThread = new Thread(() -> {
            TessBaseAPI tessBaseAPI = new TessBaseAPI();
            Log.e("QT", context.getExternalFilesDir("") + "/");
            tessBaseAPI.init(context.getExternalFilesDir("") + "/", "chi_sim");
            tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);
            tessBaseAPI.setImage(bitmap);
            text = tessBaseAPI.getUTF8Text();
            // 打印出结果
            Log.e("QT-OCR", text);
            tessBaseAPI.end();
        });
        ocrImageThread.start();
        return text;
    }
}
