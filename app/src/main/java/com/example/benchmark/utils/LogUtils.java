/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * LogUtils
 *
 * @version 1.0
 * @since 2023/3/7 17:24
 */
public class LogUtils {

    // TAG
    public static final String TAG = "ZXingLite";

    // VERTICAL
    public static final String VERTICAL = "|";

    /**
     * Priority constant for the println method;use System.out.println
     */
    public static final int PRINTLN = 1;

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.use Log.wtf.
     */
    public static final int ASSERT = 7;

    // TAG_FORMAT
    public static final String TAG_FORMAT = "%s.%s(%s:%d)";

    /**
     * 是否显示Log日志
     */
    private static boolean isShowLog = true;

    /**
     * Log日志优先权
     */
    private static int priority = 1;

    private LogUtils() {
        throw new AssertionError();
    }

    /**
     * setShowLog
     *
     * @param isShowLog description
     * @return void
     * @throws null
     * @date 2023/3/8 09:01
     */
    public static void setShowLog(boolean isShowLog) {
        LogUtils.isShowLog = isShowLog;
    }

    /**
     * isShowLog
     *
     * @return boolean
     * @throws null
     * @date 2023/3/8 09:01
     */
    public static boolean isShowLog() {
        return isShowLog;
    }

    /**
     * getPriority
     *
     * @return int
     * @throws null
     * @date 2023/3/8 09:01
     */
    public static int getPriority() {
        return priority;
    }

    /**
     * setPriority
     *
     * @param priority description
     * @return void
     * @throws null
     * @date 2023/3/8 09:01
     */
    public static void setPriority(int priority) {
        LogUtils.priority = priority;
    }

    /**
     * 根据堆栈生成TAG
     *
     * @return TAG|className.methodName(fileName:lineNumber)
     */
    @SuppressLint("DefaultLocale")
    private static String generateTag(StackTraceElement caller) {
        String tag = TAG_FORMAT;
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getFileName()
                , caller.getLineNumber());
        return new StringBuilder().append(TAG).append(VERTICAL).append(tag).toString();
    }

    /**
     * getStackTraceElement
     *
     * @param num num
     * @return java.lang.StackTraceElement
     * @throws null
     * @date 2023/3/8 09:00
     */
    public static StackTraceElement getStackTraceElement(int num) {
        return Thread.currentThread().getStackTrace()[num];
    }

    /**
     * getCallerStackLogTag
     *
     * @return java.lang.String
     * @throws null
     * @date 2023/3/8 09:00
     */
    private static String getCallerStackLogTag() {
        return generateTag(getStackTraceElement(5));
    }

    /**
     * getStackTraceString
     *
     * @param th description
     * @return java.lang.String
     * @throws null
     * @date 2023/3/8 09:00
     */
    private static String getStackTraceString(Throwable th) {
        return Log.getStackTraceString(th);
    }

    /**
     * logV
     *
     * @param msg description
     * @return void
     * @throws null
     * @date 2023/3/8 08:58
     */
    public static void logV(String msg) {
        if (isShowLog && priority <= VERBOSE) {
            Log.v(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    /**
     * logV
     *
     * @param th description
     * @return void
     * @throws null
     * @date 2023/3/8 08:58
     */
    public static void logV(Throwable th) {
        if (isShowLog && priority <= VERBOSE) {
            Log.v(getCallerStackLogTag(), getStackTraceString(th));
        }
    }

    /**
     * logV
     *
     * @param msg description
     * @param th  description
     * @return void
     * @throws null
     * @date 2023/3/8 08:58
     */
    public static void logV(String msg, Throwable th) {
        if (isShowLog && priority <= VERBOSE) {
            Log.v(getCallerStackLogTag(), String.valueOf(msg), th);
        }
    }

    /**
     * logD
     *
     * @param msg description
     * @return void
     * @throws null
     * @date 2023/3/8 08:58
     */
    public static void logD(String msg) {
        if (isShowLog && priority <= DEBUG) {
            Log.d(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    /**
     * logD
     *
     * @param th description
     * @return void
     * @throws null
     * @date 2023/3/8 08:58
     */
    public static void logD(Throwable th) {
        if (isShowLog && priority <= DEBUG) {
            Log.d(getCallerStackLogTag(), getStackTraceString(th));
        }
    }

    /**
     * logD
     *
     * @param msg description
     * @param th  description
     * @return void
     * @throws null
     * @date 2023/3/8 08:58
     */
    public static void logD(String msg, Throwable th) {
        if (isShowLog && priority <= DEBUG) {
            Log.d(getCallerStackLogTag(), String.valueOf(msg), th);
        }
    }

    /**
     * logI
     *
     * @param msg description
     * @return void
     * @throws null
     * @date 2023/3/8 08:58
     */
    public static void logI(String msg) {
        if (isShowLog && priority <= INFO) {
            Log.i(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    /**
     * logI
     *
     * @param th description
     * @return void
     * @throws null
     * @date 2023/3/8 08:59
     */
    public static void logI(Throwable th) {
        if (isShowLog && priority <= INFO) {
            Log.i(getCallerStackLogTag(), getStackTraceString(th));
        }
    }

    /**
     * logI
     *
     * @param msg description
     * @param th  description
     * @return void
     * @throws null
     * @date 2023/3/8 08:59
     */
    public static void logI(String msg, Throwable th) {
        if (isShowLog && priority <= INFO) {
            Log.i(getCallerStackLogTag(), String.valueOf(msg), th);
        }
    }

    /**
     * logW
     *
     * @param msg description
     * @return void
     * @throws null
     * @date 2023/3/8 08:59
     */
    public static void logW(String msg) {
        if (isShowLog && priority <= WARN) {
            Log.w(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    /**
     * logW
     *
     * @param th description
     * @return void
     * @throws null
     * @date 2023/3/8 08:59
     */
    public static void logW(Throwable th) {
        if (isShowLog && priority <= WARN) {
            Log.w(getCallerStackLogTag(), getStackTraceString(th));
        }
    }

    /**
     * logW
     *
     * @param msg description
     * @param th  description
     * @return void
     * @throws null
     * @date 2023/3/8 08:59
     */
    public static void logW(String msg, Throwable th) {
        if (isShowLog && priority <= WARN) {
            Log.w(getCallerStackLogTag(), String.valueOf(msg), th);
        }
    }

    /**
     * logE
     *
     * @param msg description
     * @return void
     * @throws null
     * @date 2023/3/8 08:59
     */
    public static void logE(String msg) {
        if (isShowLog && priority <= ERROR) {
            Log.e(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    /**
     * logE
     *
     * @param th description
     * @return void
     * @throws null
     * @date 2023/3/8 08:59
     */
    public static void logE(Throwable th) {
        if (isShowLog && priority <= ERROR) {
            Log.e(getCallerStackLogTag(), getStackTraceString(th));
        }
    }

    /**
     * logE
     *
     * @param msg description
     * @param th  description
     * @return void
     * @throws null
     * @date 2023/3/8 08:59
     */
    public static void logE(String msg, Throwable th) {
        if (isShowLog && priority <= ERROR) {
            Log.e(getCallerStackLogTag(), String.valueOf(msg), th);
        }
    }

    /**
     * wtf
     *
     * @param msg description
     * @return void
     * @throws null
     * @date 2023/3/8 08:59
     */
    public static void wtf(String msg) {
        if (isShowLog && priority <= ASSERT) {
            Log.wtf(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    /**
     * wtf
     *
     * @param th description
     * @return void
     * @throws null
     * @date 2023/3/8 08:59
     */
    public static void wtf(Throwable th) {
        if (isShowLog && priority <= ASSERT) {
            Log.wtf(getCallerStackLogTag(), getStackTraceString(th));
        }
    }

    /**
     * wtf
     *
     * @param msg description
     * @param th  description
     * @return void
     * @throws null
     * @date 2023/3/8 08:59
     */
    public static void wtf(String msg, Throwable th) {
        if (isShowLog && priority <= ASSERT) {
            Log.wtf(getCallerStackLogTag(), String.valueOf(msg), th);
        }
    }

    /**
     * print
     *
     * @param msg description
     * @return void
     * @throws null
     * @date 2023/3/8 08:59
     */
    public static void print(String msg) {
        if (isShowLog && priority <= PRINTLN) {
            Log.d(TAG, "msg: " + msg);
        }
    }

    /**
     * print
     *
     * @param obj description
     * @return void
     * @throws null
     * @date 2023/3/8 09:00
     */
    public static void print(Object obj) {
        if (isShowLog && priority <= PRINTLN) {
            Log.d(TAG, "obj: " + obj);
        }
    }

    /**
     * printf
     *
     * @param msg description
     * @return void
     * @throws null
     * @date 2023/3/8 09:00
     */
    public static void printf(String msg) {
        if (isShowLog && priority <= PRINTLN) {
            Log.d(TAG, "msg: " + msg);
        }
    }

    /**
     * println
     *
     * @param msg description
     * @return void
     * @throws null
     * @date 2023/3/8 09:00
     */
    public static void println(String msg) {
        if (isShowLog && priority <= PRINTLN) {
            Log.d(TAG, "msg: " + msg);
        }
    }

    /**
     * println
     *
     * @param obj description
     * @return void
     * @throws null
     * @date 2023/3/8 09:00
     */
    public static void println(Object obj) {
        if (isShowLog && priority <= PRINTLN) {
            Log.d(TAG, "msg: " + obj);
        }
    }
}
