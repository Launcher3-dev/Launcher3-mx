/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3.menu;

import android.view.View;

/**
 * This class differs from the framework {@link android.widget.Scroller} in that
 * you can modify the Interpolator post-construction.
 */
public class MenuSweeper {

    /**
     * 如果移动角度达到该值，则屏蔽点击
     */
    private static final int SWEEP_SLOT = 3;

    private int mStartAngle = 0;
    private boolean isFling = false;
    private long mDownTime;
    private int mTmpAngle;
    private float mAngleVelocity;
    private View mView;
    private AutoFlingRunnable mFlingRunnable;

    MenuSweeper(View view) {
        this.mView = view;
    }

    void abortSweeperAnimation() {
        isFling = false;
        mView.removeCallbacks(mFlingRunnable);
    }

    /**
     * 根据触摸的位置，计算角度
     *
     * @param diameter 触摸点的x坐标值
     * @param xTouch   触摸点的x坐标值
     * @param yTouch   触摸点的y坐标值
     *
     * @return 触摸点相对中点的角度
     */
    float getAngle(int diameter, float xTouch, float yTouch) {
        double x = xTouch - (diameter / 2d);
        double y = yTouch - (diameter / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据当前位置计算象限
     *
     * @param diameter 菜单直径
     * @param x        x坐标值
     * @param y        y坐标值
     *
     * @return 坐标所在象限
     */
    int getQuadrant(int diameter, float x, float y) {
        int tmpX = (int) (x - diameter / 2);
        int tmpY = (int) (y - diameter / 2);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }
    }

    boolean isQuadrant1Or4(int diameter, float x, float y) {
        int quadrant = getQuadrant(diameter, x, y);
        return quadrant == 1 || quadrant == 4;
    }

    public void startFling(float deltaAngle) {
        mStartAngle += deltaAngle;
        mTmpAngle += deltaAngle;
    }

    void initActionDown() {
        mTmpAngle = 0;
        mAngleVelocity = 0f;
        mDownTime = System.currentTimeMillis();
    }

    boolean isFastSweep() {
        // 计算，每秒移动的角度
        mAngleVelocity = mTmpAngle * 1000f
                / (System.currentTimeMillis() - mDownTime);
        return Math.abs(mAngleVelocity) > mFlingAbleValue;
    }

    boolean isNoClick() {
        return Math.abs(mTmpAngle) > SWEEP_SLOT;
    }

    private long mFlingAbleValue = 100;

    /**
     * 如果每秒旋转角度到达该值，则认为是自动滚动
     *
     * @param flingAbleValue fling阈值
     */
    public void setFlingAbleValue(long flingAbleValue) {
        this.mFlingAbleValue = flingAbleValue;
    }

    public boolean isFling() {
        return isFling;
    }

    int getStartAngle() {
        return mStartAngle;
    }

    public void fling() {
        mFlingRunnable = new AutoFlingRunnable(mCallback, mAngleVelocity);
        mView.post(mFlingRunnable);
    }

    private FlingCallback mCallback = new FlingCallback() {
        @Override
        public void onFling() {
            mView.postDelayed(mFlingRunnable, 30);
            // 重新布局
            mView.requestLayout();
        }
    };

    /**
     * 自动滚动的任务
     *
     * @author zhy
     */
    private class AutoFlingRunnable implements Runnable {

        private FlingCallback flingCallback;
        private float flingVelocity;

        AutoFlingRunnable(FlingCallback flingCallback, float velocity) {
            this.flingCallback = flingCallback;
            this.flingVelocity = velocity;
        }

        public void run() {
            // 如果小于20,则停止
            if ((int) Math.abs(flingVelocity) < 20) {
                isFling = false;
                return;
            }
            isFling = true;
            // 不断改变mStartAngle，让其滚动，/30为了避免滚动太快
            mStartAngle += (flingVelocity / 30);
            // 逐渐减小这个值
            flingVelocity /= 1.0666F;
            if (flingCallback != null) {
                flingCallback.onFling();
            }
        }
    }

    interface FlingCallback {
        void onFling();
    }

}
