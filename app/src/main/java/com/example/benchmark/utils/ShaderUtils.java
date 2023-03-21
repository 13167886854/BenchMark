/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ShaderUtils
 *
 * @version 1.0
 * @since 2023/3/7 17:28
 */
public class ShaderUtils {
    /**
     * loadShader
     *
     * @param shaderType description
     * @param source     description
     * @return int
     * @throws null
     * @date 2023/3/8 09:25
     */
    public static int loadShader
    (
            // shader的类型  GLES20.GL_VERTEX_SHADER   GLES20.GL_FRAGMENT_SHADER
            // The type of shader is GLES20.GL_VERTEX_SHADER GLES20.GL_FRAGMENT_SHADER
            int shaderType,

            // shader的脚本字符串  Script string for shader
            String source
    ) {
        // 创建一个新shader  Create a new shader
        int shader = GLES20.glCreateShader(shaderType);

        // 若创建成功则加载shader  If the shader is created successfully, the shader is loaded
        if (shader != 0) {
            // 加载shader的源代码  Load the shader source code
            GLES20.glShaderSource(shader, source);

            // 编译shader  Compile shader
            GLES20.glCompileShader(shader);

            // 存放编译成功shader数量的数组  An array containing the number of successfully compiled shaders
            int[] compiled = new int[1];

            // 获取Shader的编译情况  Gets the compilation of the Shader
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                // 若编译失败则显示错误日志并删除此shader  If the compilation fails, the error log is displayed and the shader is deleted
                Log.e("ES20_ERROR", "Could not compile shader " + shaderType + ":");
                Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    /**
     * readRawTextFile
     *
     * @param context description
     * @param rid description
     * @return java.lang.String
     * @throws null
     * @date 2023/3/8 09:25
     */
    public static String readRawTextFile(Context context, int rid) {
        String result = null;
        try {
            InputStream input = context.getResources().openRawResource(rid);
            int ch = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = input.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            input.close();
            result = new String(buff, "UTF-8");
            result = result.replaceAll("\\r\\n", "\n");
        } catch (IOException ioe) {
            Log.e("ShaderUtils", "readRawTextFile: ", ioe);
        }
        return result;
    }

    /**
     * createProgram
     *
     * @param vertexSource description
     * @param fragmentShader description
     * @return int
     * @throws null
     * @date 2023/3/8 09:25
     */
    public static int createProgram(String vertexSource, String fragmentShader) {
        // 加载顶点着色器  Load the vertex shader
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        // 加载片元着色器  Load the slice shader
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
        if (pixelShader == 0) {
            return 0;
        }

        // 创建程序  creator
        int program = GLES20.glCreateProgram();

        // 若程序创建成功则向程序中加入顶点着色器与片元着色器
        // Add vertex shaders and slice shaders to the program if the program is created successfully
        if (program != 0) {
            // 向程序中加入顶点着色器  Adds a vertex shader to the program
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");

            // 向程序中加入片元着色器  Adds a slice shader to the program
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");

            // 链接程序  chained program
            GLES20.glLinkProgram(program);

            // 存放链接成功program数量的数组  An array containing the number of linked successful programs
            int[] linkStatus = new int[1];

            // 获取program的链接情况  Get the link status of the program
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // 若链接失败则报错并删除程序  If the link fails, an error is reported and the program is deleted
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e("ES20_ERROR", "Could not link program: ");
                Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     * checkGlError
     *
     * @param str description
     * @return void
     * @throws null
     * @date 2023/3/8 09:25
     */
    public static void checkGlError(String str) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("ES20_ERROR", str + ": glError " + error);
            throw new RuntimeException(str + ": glError " + error);
        }
    }
}
