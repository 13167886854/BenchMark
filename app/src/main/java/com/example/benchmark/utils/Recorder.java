/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.benchmark.data.YinHuaData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Recorder
 *
 * @version 1.0
 * @since 2023/3/7 17:28
 */
public class Recorder {
    /**
     * Recorder TAG标签
     */
    public static final String TAG = "Recorder";

    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * isRecording
     */
    private boolean isRecording = false;

    private AudioRecord mRecorder;
    private int bufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    private int bytesPerElement = 2; // 2 bytes in 16bit format
    private String file;
    private File root;
    private File cache;
    private File rawOutput;

    /**
     * start
     *
     * @param context     description
     * @param mProjection description
     * @return boolean
     * @date 2023/3/10 16:42
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean start(Context context, MediaProjection mProjection) {
        // 判断平台
        String platformKind = YinHuaData.getInstance().getPlatformType();

        // 如果是云手机平台
        if (platformKind.equals(CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_E_CLOUD_PHONE)) {
            file = CacheConst.AUDIO_PHONE_NAME;
        } else if (platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME)) {
            file = CacheConst.AUDIO_PHONE_NAME;
        } else {
            file = CacheConst.AUDIO_GAME_NAME;
        }

        if (mRecorder == null) {
            AudioPlaybackCaptureConfiguration config;
            try {
                config = new AudioPlaybackCaptureConfiguration.Builder(mProjection)
                        .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                        .addMatchingUsage(AudioAttributes.USAGE_GAME)
                        .addMatchingUsage(AudioAttributes.USAGE_UNKNOWN)
                        .build();
            } catch (NoClassDefFoundError e) {
                Toast.makeText(context,
                        "System Audio Capture is not Supported on this Device", Toast.LENGTH_LONG).show();
                return false;
            }
            AudioFormat format = new AudioFormat.Builder()
                    .setEncoding(RECORDER_AUDIO_ENCODING)
                    .setSampleRate(RECORDER_SAMPLERATE)
                    .setChannelMask(RECORDER_CHANNELS)
                    .build();

            mRecorder = new AudioRecord.Builder()
                    .setAudioFormat(format)
                    .setBufferSizeInBytes(bufferElements2Rec * bytesPerElement)
                    .setAudioPlaybackCaptureConfig(config)
                    .build();
            isRecording = true;
            mRecorder.startRecording();
            createAudioFile(context);

            ThreadPoolUtil.getPool().execute(new Runnable() {
                @Override
                public void run() {
                    writeAudioFile();
                }
            });
        }
        return true;
    }

    /**
     * createAudioFile
     *
     * @param context description
     * @return void
     * @date 2023/3/10 16:42
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void createAudioFile(Context context) {
        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "AudioRecorder");
        CacheConst.getInstance().setAudioPath(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "AudioRecorder");
        Log.e(TAG, "root: " + root);
        cache = new File(context.getCacheDir().getAbsolutePath(), File.separator + "RawData");
        if (!root.exists()) {
            root.mkdir();
            root.setWritable(true);
        }
        if (!cache.exists()) {
            cache.mkdir();
            cache.setWritable(true);
            cache.setReadable(true);
        }
        rawOutput = new File(root, file);
        rawOutput.setWritable(true);
        rawOutput.setReadable(true);
        try {
            rawOutput.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "createAudioFile: " + e.toString());
        }
    }

    private void rawToWave(final File rawFile, final File waveFile) throws IOException {
        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, RECORDER_SAMPLERATE); // sample rate
            writeInt(output, RECORDER_SAMPLERATE); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size

            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short ss : shorts) {
                bytes.putShort(ss);
            }
            output.write(fullyReadFileToBytes(rawFile));
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    private byte[] fullyReadFileToBytes(File file) throws IOException {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        byte[] tmpBuff = new byte[size];
        FileInputStream fis = new FileInputStream(file);
        try {
            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } catch (IOException ex) {
            Log.e(TAG, "fullyReadFileToBytes: ", ex);
        } finally {
            fis.close();
        }
        return bytes;
    }

    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }

    private byte[] shortToByte(short[] data) {
        int arraySize = data.length;
        byte[] bytes = new byte[arraySize * 2];
        for (int i = 0; i < arraySize; i++) {
            bytes[i * 2] = (byte) (data[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (data[i] >> 8);
            data[i] = 0;
        }
        return bytes;
    }

    private void writeAudioFile() {
        try {
            String canonicalPath = rawOutput.getCanonicalPath();
            FileOutputStream outputStream = new FileOutputStream(canonicalPath);
            short[] data = new short[bufferElements2Rec];
            while (isRecording) {
                mRecorder.read(data, 0, bufferElements2Rec);
                Log.d(TAG, "AUDIO: writeAudioFile: " + data.toString());
                ByteBuffer buffer = ByteBuffer.allocate(8 * 1024);
                outputStream.write(shortToByte(data),
                        0,
                        bufferElements2Rec * bytesPerElement);
            }
            outputStream.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File Not Found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "IO Exception: " + e.toString());
        }
    }

    /**
     * startProcessing
     *
     * @throws IOException ex
     * @description: startProcessing
     * @date 2023/3/7 14:55
     */
    public void startProcessing() throws IOException {
        isRecording = false;
        mRecorder.stop();
        mRecorder.release();
        String fileName = file + ".mp3";

        // Convert To mp3 from raw data i.e pcm
        File output = new File(root, fileName);
        try {
            output.createNewFile();
        } catch (IOException ex) {
            Log.e(TAG, "startProcessing: " + ex);
        }
        try {
            rawToWave(rawOutput, output);
        } catch (IOException ex) {
            Log.e(TAG, "startProcessing: ", ex);
        }
    }
}
