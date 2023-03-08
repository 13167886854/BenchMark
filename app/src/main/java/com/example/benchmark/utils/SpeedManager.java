/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.util.Log;

/**
 * SpeedManager
 *
 * @version 1.0
 * @since 2023/3/7 17:29
 */
public class SpeedManager {
    private static final String TAG = SpeedManager.class.getSimpleName();
    private static final boolean CHECK_SLEEP_TIME = false;
    private static final long ONE_MILLION = 1000000L;
    private long mPrevPresentUsec;
    private long mPrevMonoUsec;
    private long mFixedFrameDurationUsec;
    private boolean mLoopReset;

    /**
     * setFixedPlaybackRate
     *
     * @param fps description
     * @return void
     * @throws null
     * @date 2023/3/8 09:34
     */
    public void setFixedPlaybackRate(int fps) {
        mFixedFrameDurationUsec = ONE_MILLION / fps;
    }

    /**
     * preRender
     *
     * @param presentationTimeUsec description
     * @return void
     * @throws null
     * @date 2023/3/8 09:34
     */
    public void preRender(long presentationTimeUsec) {
        if (mPrevMonoUsec == 0) {
            mPrevMonoUsec = System.nanoTime() / 1000;
            mPrevPresentUsec = presentationTimeUsec;
        } else {

            // Compute the desired time delta between the previous frame and this frame.
            long frameDelta;
            if (mLoopReset) {
                mPrevPresentUsec = presentationTimeUsec - ONE_MILLION / 30;
                mLoopReset = false;
            }
            if (mFixedFrameDurationUsec != 0) {

                // Caller requested a fixed frame rate.  Ignore PTS.
                frameDelta = mFixedFrameDurationUsec;
            } else {
                frameDelta = presentationTimeUsec - mPrevPresentUsec;
            }
            if (frameDelta < 0) {
                frameDelta = 0;
            } else if (frameDelta == 0) {
                Log.e(TAG, "preRender: frameDelta == 0");
            } else if (frameDelta > 10 * ONE_MILLION) {
                frameDelta = 5 * ONE_MILLION;
            }

            // when we want to wake up
            long desiredUsec = mPrevMonoUsec + frameDelta;
            long nowUsec = System.nanoTime() / 1000;
            while (nowUsec < (desiredUsec - 100)) {
                long sleepTimeUsec = desiredUsec - nowUsec;
                if (sleepTimeUsec > 500000) {
                    sleepTimeUsec = 500000;
                }
                try {
                    if (CHECK_SLEEP_TIME) {
                        long startNsec = System.nanoTime();
                        Thread.sleep(sleepTimeUsec / 1000
                                , (int) (sleepTimeUsec % 1000) * 1000);
                        long actualSleepNsec = System.nanoTime() - startNsec;
                    } else {
                        long time = sleepTimeUsec / 1000;
                        Thread.sleep(time, (int) (sleepTimeUsec % 1000) * 1000);
                    }
                } catch (InterruptedException ie) {
                    Log.e(TAG, "InterruptedException: " + ie);
                }
                nowUsec = System.nanoTime() / 1000;
            }
            mPrevMonoUsec += frameDelta;
            mPrevPresentUsec += frameDelta;
        }
    }

    /**
     * loopReset
     *
     * @return void
     * @throws null
     * @date 2023/3/8 09:34
     */
    public void loopReset() {
        mLoopReset = true;
    }

    /**
     * reset
     *
     * @return void
     * @throws null
     * @date 2023/3/8 09:34
     */
    public void reset() {
        mPrevPresentUsec = 0;
        mPrevMonoUsec = 0;
        mFixedFrameDurationUsec = 0;
        mLoopReset = false;
    }
}
