/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.util.Log;

/**
 * LogUtils
 *
 * @version 1.0
 * @since 2023/3/7 17:24
 */
public class LogUtils {
    public static final String TAG = "ZXingLite";

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

    public static void setShowLog(boolean isShowLog) {
        LogUtils.isShowLog = isShowLog;
    }

    public static boolean isShowLog() {
        return isShowLog;
    }

    public static int getPriority() {
        return priority;
    }

    public static void setPriority(int priority) {
        LogUtils.priority = priority;
    }

    /**
     * 根据堆栈生成TAG
     *
     * @return TAG|className.methodName(fileName:lineNumber)
     */
    private static String generateTag(StackTraceElement caller) {
        String tag = TAG_FORMAT;
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getFileName(), caller.getLineNumber());
        return new StringBuilder().append(TAG).append(VERTICAL).append(tag).toString();
    }

    /**
     * 获取堆栈
     *
     * @param num num=0		VMStack
     *            num=1		Thread
     *            num=3		CurrentStack
     *            num=4		CallerStack
     *            ...
     * @return
     */
    public static StackTraceElement getStackTraceElement(int num) {
        return Thread.currentThread().getStackTrace()[num];
    }

    /**
     * 获取调用方的堆栈TAG
     *
     * @return
     */
    private static String getCallerStackLogTag() {
        return generateTag(getStackTraceElement(5));
    }

    /**
     * @param th
     * @return
     */
    private static String getStackTraceString(Throwable th) {
        return Log.getStackTraceString(th);
    }

    // -----------------------------------Log.v

    /**
     * Log.v
     *
     * @param msg
     */
    public static void logV(String msg) {
        if (isShowLog && priority <= VERBOSE) {
            Log.v(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    public static void logV(Throwable th) {
        if (isShowLog && priority <= VERBOSE) {
            Log.v(getCallerStackLogTag(), getStackTraceString(th));
        }
    }

    public static void logV(String msg, Throwable th) {
        if (isShowLog && priority <= VERBOSE) {
            Log.v(getCallerStackLogTag(), String.valueOf(msg), th);
        }
    }

    // -----------------------------------Log.d

    /**
     * Log.d
     *
     * @param msg
     */
    public static void logD(String msg) {
        if (isShowLog && priority <= DEBUG) {
            Log.d(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    public static void logD(Throwable th) {
        if (isShowLog && priority <= DEBUG) {
            Log.d(getCallerStackLogTag(), getStackTraceString(th));
        }
    }

    public static void logD(String msg, Throwable th) {
        if (isShowLog && priority <= DEBUG) {
            Log.d(getCallerStackLogTag(), String.valueOf(msg), th);
        }
    }

    // -----------------------------------Log.i

    /**
     * Log.i
     *
     * @param msg
     */
    public static void logI(String msg) {
        if (isShowLog && priority <= INFO) {
            Log.i(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    public static void logI(Throwable th) {
        if (isShowLog && priority <= INFO) {
            Log.i(getCallerStackLogTag(), getStackTraceString(th));
        }
    }

    public static void logI(String msg, Throwable th) {
        if (isShowLog && priority <= INFO) {
            Log.i(getCallerStackLogTag(), String.valueOf(msg), th);
        }
    }

    // -----------------------------------Log.w

    /**
     * Log.w
     *
     * @param msg
     */
    public static void logW(String msg) {
        if (isShowLog && priority <= WARN) {
            Log.w(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    public static void logW(Throwable th) {
        if (isShowLog && priority <= WARN) {
            Log.w(getCallerStackLogTag(), getStackTraceString(th));
        }
    }

    public static void logW(String msg, Throwable th) {
        if (isShowLog && priority <= WARN) {
            Log.w(getCallerStackLogTag(), String.valueOf(msg), th);
        }
    }

    // -----------------------------------Log.e

    /**
     * Log.e
     *
     * @param msg
     */
    public static void logE(String msg) {
        if (isShowLog && priority <= ERROR) {
            Log.e(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    public static void logE(Throwable th) {
        if (isShowLog && priority <= ERROR) {
            Log.e(getCallerStackLogTag(), getStackTraceString(th));
        }
    }

    public static void logE(String msg, Throwable th) {
        if (isShowLog && priority <= ERROR) {
            Log.e(getCallerStackLogTag(), String.valueOf(msg), th);
        }
    }

    // -----------------------------------Log.wtf

    /**
     * Log.wtf
     *
     * @param msg
     */
    public static void wtf(String msg) {
        if (isShowLog && priority <= ASSERT) {
            Log.wtf(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    public static void wtf(Throwable th) {
        if (isShowLog && priority <= ASSERT) {
            Log.wtf(getCallerStackLogTag(), getStackTraceString(th));
        }
    }

    public static void wtf(String msg, Throwable th) {
        if (isShowLog && priority <= ASSERT) {
            Log.wtf(getCallerStackLogTag(), String.valueOf(msg), th);
        }
    }

    // -----------------------------------System.out.print

    /**
     * System.out.print
     *
     * @param msg
     */
    public static void print(String msg) {
        if (isShowLog && priority <= PRINTLN) {
            Log.d(TAG, "msg: " + msg);
        }
    }

    public static void print(Object obj) {
        if (isShowLog && priority <= PRINTLN) {
            Log.d(TAG, "obj: " + obj);
        }
    }

    // -----------------------------------System.out.printf

    /**
     * System.out.printf
     *
     * @param msg
     */
    public static void printf(String msg) {
        if (isShowLog && priority <= PRINTLN) {
            Log.d(TAG, "msg: " + msg);
        }
    }

    // -----------------------------------System.out.println

    /**
     * System.out.println
     *
     * @param msg
     */
    public static void println(String msg) {
        if (isShowLog && priority <= PRINTLN) {
            Log.d(TAG, "msg: " + msg);
        }
    }

    public static void println(Object obj) {
        if (isShowLog && priority <= PRINTLN) {
            Log.d(TAG, "msg: " + obj);
        }
    }
}
