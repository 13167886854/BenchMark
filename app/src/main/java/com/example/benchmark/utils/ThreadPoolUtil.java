/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolUtil.java
 *
 * @Author benchmark
 * @Version 1.0
 * @since 2023/3/14 19:30
 */
public class ThreadPoolUtil {
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            8, 15, 60, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(4),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable runnable) {
                    return null;
                }
            }, new ThreadPoolExecutor.AbortPolicy());

    private ThreadPoolUtil() {

    }

    /**
     * getPool
     *
     * @return java.util.concurrent.ThreadPoolExecutor
     * @date 2023/3/14 19:30
     */
    public static ThreadPoolExecutor getPool() {
        return EXECUTOR;
    }
}
