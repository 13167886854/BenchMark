package com.example.benchmark.render;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GPURenderer implements GLSurfaceView.Renderer {

    //GPU 渲染器
    public static String gl_renderer;

    //GPU 供应商
    public static String gl_vendor;

    //GPU 版本
    public static String gl_version;

    //GPU  扩展名
    public static String gl_extensions;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {

        Log.d("SystemInfo", "GL_RENDERER = " +gl.glGetString(GL10.GL_RENDERER));
        Log.d("SystemInfo", "GL_VENDOR = " + gl.glGetString(GL10.GL_VENDOR));
        Log.d("SystemInfo", "GL_VERSION = " + gl.glGetString(GL10.GL_VERSION));
        Log.i("SystemInfo", "GL_EXTENSIONS = " + gl.glGetString(GL10.GL_EXTENSIONS));

        gl_renderer = gl.glGetString(GL10.GL_RENDERER);
        gl_vendor = gl.glGetString(GL10.GL_VENDOR);
        gl_version = gl.glGetString(GL10.GL_VERSION);
        gl_extensions = gl.glGetString(GL10.GL_EXTENSIONS);


    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }
}
