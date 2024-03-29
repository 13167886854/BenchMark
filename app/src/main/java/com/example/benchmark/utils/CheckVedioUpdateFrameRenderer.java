/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.example.benchmark.R;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * CheckVedioUpdateFrameRenderer
 *
 * @version 1.0
 * @since 2023/3/7 17:23
 */
public class CheckVedioUpdateFrameRenderer implements GLSurfaceView.Renderer,
        SurfaceTexture.OnFrameAvailableListener, MediaPlayer.OnVideoSizeChangedListener {
    private static final String TAG = "GLRenderer";
    private final int timerRelax = 1;
    private final int timerWork = 2;
    private final float[] projectionMatrix = new float[16];
    private final float[] textureVertexData = {
            1f, 0f,
            0f, 0f,
            1f, 1f,
            0f, 1f
    };
    private final float[] vertexData = {
            1f, -1f, 0f,
            -1f, -1f, 0f,
            1f, 1f, 0f,
            -1f, 1f, 0f
    };

    private float[] mSTMatrix = new float[16];
    private int uSTMMatrixHandle;
    private boolean isUpdateSurface;
    private int screenWidth;
    private int screenHeight;
    private boolean isTimerRelaxing = false;
    private int aPositionLocation;
    private int programId;
    private int uTextureSamplerLocation;
    private int aTextureCoordLocation;
    private int textureId;
    private int uMatrixLocation;

    private Context context;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureVertexBuffer;
    private GameTouchUtil gameTouchUtil = GameTouchUtil.getGameTouchUtil();
    private SurfaceTexture surfaceTexture;
    private MediaPlayer mediaPlayer;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case timerRelax:
                    isTimerRelaxing = true;
                    handler.sendEmptyMessageDelayed(timerWork, 1000);
                    break;
                case timerWork:
                    isTimerRelaxing = false;
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * CheckVedioUpdateFrameRenderer
     *
     * @param context description
     * @return
     * @throws null
     * @date 2023/3/8 10:02
     */
    public CheckVedioUpdateFrameRenderer(Context context) {
        this.context = context;
        synchronized (this) {
            isUpdateSurface = false;
        }
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureVertexBuffer.position(0);
        initMediaPlayer();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vertexShader = ShaderUtils.readRawTextFile(context, R.raw.vetext_sharder);
        String fragmentShader = ShaderUtils.readRawTextFile(context, R.raw.fragment_sharder);
        programId = ShaderUtils.createProgram(vertexShader, fragmentShader);
        aPositionLocation = GLES20.glGetAttribLocation(programId, "aPosition");
        uMatrixLocation = GLES20.glGetUniformLocation(programId, "uMatrix");
        uSTMMatrixHandle = GLES20.glGetUniformLocation(programId, "uSTMatrix");
        uTextureSamplerLocation = GLES20.glGetUniformLocation(programId, "sTexture");
        aTextureCoordLocation = GLES20.glGetAttribLocation(programId, "aTexCoord");
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        textureId = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        ShaderUtils.checkGlError("glBindTexture mTextureID");
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        surfaceTexture = new SurfaceTexture(textureId);

        // 监听是否有新的一帧数据到来  Listen for a new frame of data
        surfaceTexture.setOnFrameAvailableListener(this);
        Surface surface = new Surface(surfaceTexture);
        mediaPlayer.setSurface(surface);
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnVideoSizeChangedListener(this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged: " + width + " " + height);
        screenWidth = width;
        screenHeight = height;
        try {
            mediaPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            Log.e("TWT", "onSurfaceChanged: " + e.toString());
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        synchronized (this) {
            if (isUpdateSurface) {
                // 获取新数据  Get new data
                surfaceTexture.updateTexImage();

                // 让新的纹理和纹理坐标系能够正确的对应,mSTMatrix的定义是和projectionMatrix完全一样的。
                // The definition of mSTMatrix is exactly the same as that of projectionMatrix
                // so that the new texture and texture coordinate system can correspond correctly.
                surfaceTexture.getTransformMatrix(mSTMatrix);
                isUpdateSurface = false;
                Log.d("TWT", "onDrawFrame: 画面更新！");

                // 编写测试FPS代码  Write the test FPS code
                if (!isTimerRelaxing) {
                    gameTouchUtil.getUpdateTime(System.currentTimeMillis());
                }
            }
        }
        GLES20.glUseProgram(programId);
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1,
                false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uSTMMatrixHandle, 1,
                false, mSTMatrix, 0);
        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, 3, GLES20.GL_FLOAT, false,
                12, vertexBuffer);
        textureVertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
        GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 8, textureVertexBuffer);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(uTextureSamplerLocation, 0);
        GLES20.glViewport(0, 0, screenWidth, screenHeight);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    @Override
    public synchronized void onFrameAvailable(SurfaceTexture surface) {
        isUpdateSurface = true;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.d(TAG, "onVideoSizeChanged: " + width + " " + height);
        updateProjection(width, height);
    }

    private void updateProjection(int videoWidth, int videoHeight) {
        BigDecimal screenRatio = BigDecimal.valueOf(Float.parseFloat(String.valueOf(screenWidth / screenHeight)));
        BigDecimal videoRatio = BigDecimal.valueOf(Float.parseFloat(String.valueOf(videoHeight / videoWidth)));
        if (videoRatio.compareTo(screenRatio) == 1) {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f,
                    videoRatio.divide(screenRatio).multiply(BigDecimal.valueOf(-1)).floatValue(),
                    videoRatio.divide(screenRatio).floatValue(), -1f, 1f);
        } else {
            Matrix.orthoM(projectionMatrix, 0,
                    videoRatio.divide(screenRatio).multiply(BigDecimal.valueOf(-1)).floatValue(),
                    videoRatio.divide(screenRatio).floatValue(), -1f, 1f, -1f, 1f);
        }
    }

    /**
     * getMediaPlayer
     *
     * @return android.media.MediaPlayer
     * @throws null
     * @date 2023/3/8 10:03
     */
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
