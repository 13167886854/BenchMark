package com.example.benchmark.thread;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.benchmark.R;
import com.example.benchmark.utils.SpeedManager;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoDecodeThread extends Thread implements Runnable {
    private static final String TAG = VideoDecodeThread.class.getSimpleName();
    private MediaCodec mVideoDecoder;
    private MediaExtractor mMediaExtractor;
    private int mVideoTrackIndex = -1;
    private SurfaceView mSurfaceView;
    private String mMp4FilePath;
    private Context context;
    private long videocurtime;


    public VideoDecodeThread(String path,Context context) {
        this.context=context;
        mMp4FilePath = path;
        mMediaExtractor = new MediaExtractor();
        //AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.minions);
        try {
            //mMediaExtractor.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mMediaExtractor.setDataSource(mMp4FilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        this.mSurfaceView = surfaceView;
    }

    @Override
    public void run() {
        try {

            int trackCount = mMediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mMediaExtractor.getTrackFormat(i);
                String mime = trackFormat.getString(MediaFormat.KEY_MIME);
                if (mime.contains("video")) {
                    mVideoTrackIndex = i;
                    break;
                }
            }
            if (mVideoTrackIndex == -1) {
                mMediaExtractor.release();
                return;
            }
            final MediaFormat videoFormat = mMediaExtractor.getTrackFormat(mVideoTrackIndex);
            Log.e(TAG, "视频编码器 run: " + videoFormat.toString());
            String videoMime = videoFormat.getString(MediaFormat.KEY_MIME);
            int frameRate = videoFormat.getInteger(MediaFormat.KEY_FRAME_RATE);//24
            int maxInputSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);//45591


            //如果设置为SurfaceView，那就动态调整它的高度，保持原视频的宽高比
            if (mSurfaceView != null) {
                Context context = mSurfaceView.getContext();
                if (context instanceof Activity) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //按视频大小动态调整SurfaceView的高度
                            Resources resources = mSurfaceView.getResources();

                            final int videoWith = videoFormat.getInteger(MediaFormat.KEY_WIDTH);//1920
                            final int videoHeight = videoFormat.getInteger(MediaFormat.KEY_HEIGHT);//1080

                            int measuredWidth = mSurfaceView.getMeasuredWidth();//2064
                            int measuredHeight = mSurfaceView.getMeasuredHeight();//912




                            //纵屏，宽充满，高按比例缩放
                            int showVideoHeight = videoHeight * measuredWidth / videoWith;
                            //横屏，高充满，宽按比例缩放
                            int showVideoWidth = videoWith * measuredHeight / videoHeight;

                            if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                mSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, showVideoHeight));
                            } else if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(showVideoWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                                params.gravity = Gravity.CENTER;
                                mSurfaceView.setLayoutParams(params);
                            }
                        }
                    });
                }
            }

            mVideoDecoder = MediaCodec.createDecoderByType(videoMime);
            mVideoDecoder.configure(videoFormat, mSurfaceView.getHolder().getSurface(), null, 0);
            mVideoDecoder.start();
            videocurtime = mMediaExtractor.getSampleTime();

            ByteBuffer byteBuffer = ByteBuffer.allocate(maxInputSize);
            int sampleSize = 0;
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mMediaExtractor.selectTrack(mVideoTrackIndex);
            SpeedManager mSpeedManager = new SpeedManager();//音视频同步器
            while (sampleSize != -1 && AudioDecodeThread.ok==1) {
                sampleSize = mMediaExtractor.readSampleData(byteBuffer, 0);
                //填充要解码的数据
                if (sampleSize != -1) {
                    if (sampleSize >= 0) {
                        long sampleTime = mMediaExtractor.getSampleTime();
                        videocurtime=mMediaExtractor.getSampleTime();
                        if (sampleTime >= 0) {
                            int inputBufferIndex = mVideoDecoder.dequeueInputBuffer(-1);
                            if (inputBufferIndex >= 0) {
                                ByteBuffer inputBuffer = mVideoDecoder.getInputBuffer(inputBufferIndex);
                                if (inputBuffer != null) {
                                    inputBuffer.clear();
                                    inputBuffer.put(byteBuffer);
                                    mVideoDecoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, sampleTime, 0);
                                    mSpeedManager.preRender(sampleTime);

                                    mMediaExtractor.advance();
                                }
                            }
                        }
                    }
                }
                //解码已填充的数据
                int outputBufferIndex = mVideoDecoder.dequeueOutputBuffer(bufferInfo, 0);
                if (outputBufferIndex >= 0) {
                   // Thread.sleep(frameRate);//控制帧率在24帧左右
                    mVideoDecoder.releaseOutputBuffer(outputBufferIndex, mSurfaceView != null);
                }
            }
            mSpeedManager.reset();
            mMediaExtractor.unselectTrack(mVideoTrackIndex);
            mMediaExtractor.release();

            mVideoDecoder.stop();
            mVideoDecoder.release();
       // } catch (IOException | InterruptedException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取当前帧时间戳
     */
    public long getcurTime(){
//        return mMediaExtractor.getSampleTime();
        return  videocurtime;
    }
}