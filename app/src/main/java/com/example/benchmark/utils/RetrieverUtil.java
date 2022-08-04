package com.example.benchmark.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class RetrieverUtil {

    private MediaMetadataRetriever retriever;
    //单例模式
    private static RetrieverUtil retrieverUtil = new RetrieverUtil();
    private RetrieverUtil(){
        init();
    }
    public static RetrieverUtil getUtil(String path){
        if(retrieverUtil==null){
            retrieverUtil = new RetrieverUtil();
        }
        retrieverUtil.retriever.setDataSource(path);
        return retrieverUtil;
    }

    private void init(){
        retriever = new MediaMetadataRetriever();
    }


    public long getCount(){
        String count_s = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
        Log.d("TWT", "count_s: "+count_s);
        long count = Long.valueOf(count_s);
        return count;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public Bitmap getBitmapByIndex(int index){
        Bitmap bitmap = retriever.getFrameAtIndex(index);
        return bitmap;
    }


}
