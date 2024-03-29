/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.render;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * GPURenderer
 *
 * @version 1.0
 * @since 2023/3/7 17:19
 */
public class GPURenderer implements GLSurfaceView.Renderer {
    private static final GPURenderer GPU_RENDERER = new GPURenderer();

    /**
     * GPU渲染器
     */
    private String glRenderer;

    /**
     * GPU供应商
     */
    private String glVendor;

    /**
     * GPU供应商
     */
    private String glVersion;

    /**
     * GPU扩展名
     */
    private String glExtensions;

    private GPURenderer() {
    }

    public static GPURenderer getInstance() {
        return GPU_RENDERER;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
        Log.d("SystemInfo", "GL_RENDERER = " + gl.glGetString(GL10.GL_RENDERER));
        Log.d("SystemInfo", "GL_VENDOR = " + gl.glGetString(GL10.GL_VENDOR));
        Log.d("SystemInfo", "GL_VERSION = " + gl.glGetString(GL10.GL_VERSION));
        Log.i("SystemInfo", "GL_EXTENSIONS = " + gl.glGetString(GL10.GL_EXTENSIONS));
        glRenderer = gl.glGetString(GL10.GL_RENDERER);
        glVendor = gl.glGetString(GL10.GL_VENDOR);
        glVersion = gl.glGetString(GL10.GL_VERSION);
        glExtensions = gl.glGetString(GL10.GL_EXTENSIONS);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i2, int i1) {
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
    }

    public String getGlRenderer() {
        return glRenderer;
    }

    public void setGlRenderer(String glRenderer) {
        this.glRenderer = glRenderer;
    }

    public String getGlVendor() {
        return glVendor;
    }

    public void setGlVendor(String glVendor) {
        this.glVendor = glVendor;
    }

    public String getGlVersion() {
        return glVersion;
    }

    public void setGlVersion(String glVersion) {
        this.glVersion = glVersion;
    }
}
