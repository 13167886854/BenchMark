/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.thread;

import android.app.Activity;
import android.content.Context;
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

import com.example.benchmark.data.YinHuaData;
import com.example.benchmark.utils.SpeedManager;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * VideoDecodeThread
 *
 * @version 1.0
 * @since 2023/3/7 17:22
 */
public class VideoDecodeThread extends Thread implements Runnable {
    private static final String TAG = VideoDecodeThread.class.getSimpleName();

    private MediaCodec mVideoDecoder;
    private MediaExtractor mMediaExtractor;
    private int mVideoTrackIndex = -1;
    private SurfaceView mSurfaceView;
    private String mMp4FilePath;
    private Context context;
    private long videocurtime;

    /**
     * VideoDecodeThread
     *
     * @param path    description
     * @param context description
     * @date 2023/3/9 16:40
     */
    public VideoDecodeThread(String path, Context context) {
        this.context = context;
        mMp4FilePath = path;
        mMediaExtractor = new MediaExtractor();
        try {
            mMediaExtractor.setDataSource(mMp4FilePath);
        } catch (IOException ex) {
            Log.e(TAG, "VideoDecodeThread: ", ex);
        }
    }

    /**
     * VideoDecodeThread
     *
     * @param path description
     * @param context description
     * @param name description
     * @return
     * @date 2023/3/15 10:35
     */
    public VideoDecodeThread(String path, Context context, String name) {
        super(name);
        this.context = context;
        mMp4FilePath = path;
        mMediaExtractor = new MediaExtractor();
        try {
            mMediaExtractor.setDataSource(mMp4FilePath);
        } catch (IOException ex) {
            Log.e(TAG, "VideoDecodeThread: ", ex);
        }
    }

    /**
     * setSurfaceView
     *
     * @param surfaceView description
     * @return void
     * @date 2023/3/9 16:40
     */
    public void setSurfaceView(SurfaceView surfaceView) {
        this.mSurfaceView = surfaceView;
    }

    /**
     * run
     *
     * @return void
     * @date 2023/3/9 16:41
     */
    @Override
    public void run() {
        try {
            MediaFormat videoFormat;
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
                videoFormat = null;
            }
            videoFormat = mMediaExtractor.getTrackFormat(mVideoTrackIndex);
            int frameRate = videoFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
            if (videoFormat == null) {
                return;
            }

            // 如果设置为SurfaceView，那就动态调整它的高度，保持原视频的宽高比
            // If it's set to SurfaceView, dynamically adjust its height to keep the aspect ratio of the original video
            if (mSurfaceView != null) {
                run2(videoFormat);
            }
            String videoMime = videoFormat.getString(MediaFormat.KEY_MIME);
            mVideoDecoder = MediaCodec.createDecoderByType(videoMime);
            mVideoDecoder.configure(videoFormat, mSurfaceView.getHolder().getSurface(), null, 0);
            mVideoDecoder.start();
            videocurtime = mMediaExtractor.getSampleTime();
            int maxInputSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
            ByteBuffer byteBuffer = ByteBuffer.allocate(maxInputSize);
            int sampleSize = 0;
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mMediaExtractor.selectTrack(mVideoTrackIndex);
            SpeedManager mSpeedManager = new SpeedManager(); // 音视频同步器
            while (sampleSize != -1 && !YinHuaData.getInstance().isTestOver()) {
                sampleSize = run3(byteBuffer, bufferInfo, mSpeedManager);
            }
            mSpeedManager.reset();
            mMediaExtractor.unselectTrack(mVideoTrackIndex);
            mMediaExtractor.release();
            mVideoDecoder.stop();
            mVideoDecoder.release();
        } catch (IOException e) {
            Log.e(TAG, "run: ", e);
        }
    }

    private int run3(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo, SpeedManager mSpeedManager) {
        int sampleSize;
        sampleSize = mMediaExtractor.readSampleData(byteBuffer, 0);

        // 填充要解码的数据  Populate the data to be decoded
        if (sampleSize != -1) {
            if (sampleSize >= 0) {
                if1(byteBuffer, mSpeedManager, sampleSize);
            }
        }
        // 解码已填充的数据
        int outputBufferIndex = mVideoDecoder.dequeueOutputBuffer(bufferInfo, 0);
        if (outputBufferIndex >= 0) {
            // 控制帧率在24帧左右
            mVideoDecoder.releaseOutputBuffer(outputBufferIndex, mSurfaceView != null);
        }
        return sampleSize;
    }

    private void if1(ByteBuffer byteBuffer, SpeedManager mSpeedManager, int sampleSize) {
        long sampleTime = mMediaExtractor.getSampleTime();
        videocurtime = mMediaExtractor.getSampleTime();
        if (sampleTime >= 0) {
            int inputBufferIndex = mVideoDecoder.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = mVideoDecoder.getInputBuffer(inputBufferIndex);
                if (inputBuffer != null) {
                    inputBuffer.clear();
                    inputBuffer.put(byteBuffer);
                    mVideoDecoder.queueInputBuffer(inputBufferIndex,
                            0, sampleSize, sampleTime, 0);
                    mSpeedManager.preRender(sampleTime);

                    mMediaExtractor.advance();
                }
            }
        }
    }

    private void run2(MediaFormat videoFormat) {
        Context contextTemp = mSurfaceView.getContext();
        if (contextTemp instanceof Activity) {
            ((Activity) contextTemp).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 按视频大小动态调整SurfaceView的高度  Dynamically adjust the height of the SurfaceView by video size
                    Resources resources = mSurfaceView.getResources();

                    final int videoWith = videoFormat.getInteger(MediaFormat.KEY_WIDTH);
                    final int videoHeight = videoFormat.getInteger(MediaFormat.KEY_HEIGHT);

                    int measuredWidth = mSurfaceView.getMeasuredWidth();
                    int measuredHeight = mSurfaceView.getMeasuredHeight();

                    // 纵屏，宽充满，高按比例缩放  Vertical screen, full width, scaled height
                    int showVideoHeight = videoHeight * measuredWidth / videoWith;

                    // 横屏，高充满，宽按比例缩放  Landscape, full height, scale width
                    int showVideoWidth = videoWith * measuredHeight / videoHeight;

                    if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        mSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, showVideoHeight));
                    } else if (resources.getConfiguration()
                            .orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                showVideoWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                        params.gravity = Gravity.CENTER;
                        mSurfaceView.setLayoutParams(params);
                    } else {
                        Log.d(TAG, "run: lastElse");
                    }
                }
            });
        }
    }

    /**
     * getcurTime
     *
     * @return long
     * @date 2023/3/8 15:38
     */
    public long getcurTime() {
        return videocurtime;
    }
}