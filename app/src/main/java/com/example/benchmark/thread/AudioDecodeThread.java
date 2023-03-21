/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.thread;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.benchmark.activity.AudioVideoActivity;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * AudioDecodeThread
 *
 * @version 1.0
 * @since 2023/3/7 17:22
 */
public class AudioDecodeThread extends Thread implements Runnable {
    private static final String TAG = AudioDecodeThread.class.getSimpleName();

    private int mAudioTrackIndex = -1;
    private long audiocurtime;
    private String mMp4FilePath;
    private int mSessionId;

    private Context context;
    private MediaCodec mAudioDecoder;
    private MediaExtractor mMediaExtractor;

    /**
     * AudioDecodeThread
     *
     * @param name description
     * @return
     * @throws null
     * @date 2023/3/8 09:42
     */
    public AudioDecodeThread(String name) {
        super(name);
    }

    /**
     * AudioDecodeThread
     *
     * @param path    description
     * @param context description
     * @return
     * @throws null
     * @date 2023/3/8 09:42
     */
    public AudioDecodeThread(String path, Context context) {
        mMp4FilePath = path;
        this.context = context;
        mMediaExtractor = new MediaExtractor();
        try {
            mMediaExtractor.setDataSource(mMp4FilePath);
        } catch (IOException ex) {
            Log.e(TAG, "AudioDecodeThread: ", ex);
        }
    }

    /**
     * setSessionId
     *
     * @param sessionId description
     * @return void
     * @throws null
     * @date 2023/3/8 09:42
     */
    public void setSessionId(int sessionId) {
        this.mSessionId = sessionId;
    }

    @Override
    public void run() {
        try {
            if (run4()) {
                return;
            }
            MediaFormat format = mMediaExtractor.getTrackFormat(mAudioTrackIndex);
            Log.e(TAG, "音频编码器 run: " + format.toString());
            String audioMime = format.getString(MediaFormat.KEY_MIME);
            Log.e(TAG, "音频mimeType： " + audioMime);
            int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            Log.e(TAG, "采样率： " + sampleRate);
            long duration = format.getLong(MediaFormat.KEY_DURATION);
            Log.e(TAG, "音频长度： " + duration);
            int channelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            Log.e(TAG, "通道数： " + channelCount);
            String language = format.getString(MediaFormat.KEY_LANGUAGE);
            Log.e(TAG, "语言： " + language);
            int aacProfile = 0;
            if (format.containsKey(MediaFormat.KEY_AAC_PROFILE)) {
                aacProfile = format.getInteger(MediaFormat.KEY_AAC_PROFILE);
            }
            Log.e(TAG, "AAC配置类型： " + aacProfile);
            getOptionalValue(format);
            mAudioDecoder = MediaCodec.createDecoderByType(audioMime);
            mAudioDecoder.configure(format, null, null, 0);
            mAudioDecoder.start();
            int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
            if (channelCount >= 2) {
                channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
            }
            int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
            int minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    channelConfig,
                    audioFormat
            );
            run3(sampleRate, channelConfig, audioFormat, minBufferSize);
        } catch (IOException ex) {
            Log.e(TAG, "run: ", ex);
        }
    }

    private boolean run4() {
        int trackCount = mMediaExtractor.getTrackCount();
        for (int i = 0; i < trackCount; i++) {
            MediaFormat trackFormat = mMediaExtractor.getTrackFormat(i);
            String mime = trackFormat.getString(MediaFormat.KEY_MIME);
            if (mime.contains("audio")) {
                mAudioTrackIndex = i;
                // 开始读数据前一定要先选择媒体轨道，否则读取不到数据
                // Before you start reading data, you must select the media track; otherwise, the data cannot be read
                mMediaExtractor.selectTrack(mAudioTrackIndex);
                break;
            }
        }
        if (mAudioTrackIndex == -1) {
            mMediaExtractor.release();
            return true;
        }
        return false;
    }

    private void run3(int sampleRate, int channelConfig, int audioFormat, int minBufferSize) {
        AudioTrack audioTrack = run1(sampleRate, channelConfig, audioFormat, minBufferSize);
        audioTrack.play();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        ByteBuffer byteBuffer = ByteBuffer.allocate(minBufferSize);
        int sampleSize = 0;
        run2(audioTrack, bufferInfo, byteBuffer, sampleSize);
        mMediaExtractor.unselectTrack(mAudioTrackIndex);
        mMediaExtractor.release();
        audioTrack.release();
    }

    private void run2(AudioTrack audioTrack, MediaCodec.BufferInfo bufferInfo, ByteBuffer byteBuffer, int sampleSize) {
        while (sampleSize != -1 && !AudioVideoActivity.isTestOver) {
            sampleSize = mMediaExtractor.readSampleData(byteBuffer, 0);

            // 填充要解码的数据  Populate the data to be decoded
            if (sampleSize != -1) {
                int inputBufferIndex = mAudioDecoder.dequeueInputBuffer(0);
                if (inputBufferIndex >= 0) {
                    ByteBuffer inputBuffer = mAudioDecoder.getInputBuffer(inputBufferIndex);
                    if (inputBuffer != null) {
                        inputBuffer.put(byteBuffer);
                        mAudioDecoder.queueInputBuffer(inputBufferIndex,
                                0, sampleSize, mMediaExtractor.getSampleTime(), 0);
                        audiocurtime = mMediaExtractor.getSampleTime();
                        mMediaExtractor.advance();
                    }
                }
            }

            // 解码已填充的数据  Decode the populated data
            int outputBufferIndex = mAudioDecoder.dequeueOutputBuffer(bufferInfo, 0);
            if (outputBufferIndex >= 0) {
                ByteBuffer outputBuffer = mAudioDecoder.getOutputBuffer(outputBufferIndex);
                if (outputBuffer != null) {
                    byte[] bytes = new byte[bufferInfo.size];
                    outputBuffer.position(0);
                    outputBuffer.get(bytes);
                    outputBuffer.clear();
                    audioTrack.write(bytes, 0, bufferInfo.size);
                    mAudioDecoder.releaseOutputBuffer(outputBufferIndex, false);
                }
            }
        }
    }

    @NonNull
    private AudioTrack run1(int sampleRate, int channelConfig, int audioFormat, int minBufferSize) {
        AudioTrack audioTrack;
        if (mSessionId != 0) {
            Log.e(TAG, "run: 新方式，初始化音频播放器");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .build();
            AudioFormat.Builder builder = new AudioFormat.Builder();
            AudioFormat audioTrackFormat = builder
                    /*
                        部分音频必须设置采样率，部分音频必须乘声道数，否则播放速度不对 Part of the audio must be set sampling rate,
                        part of the audio must be multiplied by the number of channels, otherwise the playback speed is wrong
                     */
                    .setSampleRate(sampleRate)
                    .setEncoding(audioFormat)
                    .setChannelMask(channelConfig)
                    .build();
            audioTrack = new AudioTrack(
                    audioAttributes,
                    audioTrackFormat,
                    minBufferSize,
                    AudioTrack.MODE_STREAM,
                    mSessionId
            );
        } else {
            Log.e(TAG, "run: 旧方式，初始化音频播放器");
            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    minBufferSize,
                    AudioTrack.MODE_STREAM
            );
        }
        return audioTrack;
    }

    private void getOptionalValue(MediaFormat format) {
        int bitRate = 0;
        if (format.containsKey(MediaFormat.KEY_BIT_RATE)) {
            bitRate = format.getInteger(MediaFormat.KEY_BIT_RATE);
        }
        Log.e(TAG, "比特率： " + bitRate);
        int profile = 0;
        if (format.containsKey(MediaFormat.KEY_PROFILE)) {
            profile = format.getInteger(MediaFormat.KEY_PROFILE);
        }
        Log.e(TAG, "音频配置类型： " + profile);
        int maxBitrate = 0;
        if (format.containsKey("max-bitrate")) {
            maxBitrate = format.getInteger("max-bitrate");
        }
        Log.e(TAG, "最大比特率： " + maxBitrate);
        int trackId = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            trackId = format.getInteger(MediaFormat.KEY_TRACK_ID);
        }
        Log.e(TAG, "轨道Id： " + trackId);
        int maxInputSize = 0;
        if (format.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
            maxInputSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
        }
        Log.e(TAG, "最大输入量： " + maxInputSize);
        ByteBuffer heapByteBuffer = format.getByteBuffer("csd-0");
        byte[] bytes = heapByteBuffer.array();
        int position = heapByteBuffer.position();
        int limit = heapByteBuffer.limit();
        int capacity = heapByteBuffer.capacity();
    }

    /**
     * getcurTime
     *
     * @return long
     * @throws null
     * @date 2023/3/8 09:43
     */
    public long getcurTime() {
        return audiocurtime;
    }
}