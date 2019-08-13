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

import android.animation.TimeInterpolator;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Build;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

/**
 * This class differs from the framework {@link android.widget.Scroller} in that
 * you can modify the Interpolator post-construction.
 */
public class MenuSweeper {
    private int mMode;

    // 开始角度
    private int mStartAngel;
    // 最终角度
    private int mFinalAngel;

    // 最小角度
    private int mMinAngel;
    // 最大角度
    private int mMaxAngel;

    // 旋转过程中当前速度
    private int mCurrAngel;
    // 角度变量
    private float mDeltaAngel;

    // 开始时间
    private long mStartTime;
    // 旋转时间
    private int mDuration;
    // 旋转时间的倒数
    private float mDurationReciprocal;

    // 是否结束旋转
    private boolean mFinished;
    // 时间插入器
    private TimeInterpolator mInterpolator;
    private boolean mFlywheel;

    private float mVelocity;
    private float mCurrVelocity;
    private int mDistance;

    // 获取惯性摩擦因子
    private float mFlingFriction = ViewConfiguration.getScrollFriction();

    private static final int DEFAULT_DURATION = 250;
    private static final int SWEEP_MODE = 0;// 手动旋转
    private static final int FLING_MODE = 1;// 惯性旋转

    private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));
    private static final float INFLEXION = 0.35f; // Tension lines cross at (INFLEXION, 1)
    private static final float START_TENSION = 0.5f;
    private static final float END_TENSION = 1.0f;
    private static final float P1 = START_TENSION * INFLEXION;
    private static final float P2 = 1.0f - END_TENSION * (1.0f - INFLEXION);

    private static final int NB_SAMPLES = 100;
    private static final float[] SPLINE_POSITION = new float[NB_SAMPLES + 1];

    private float mDeceleration;
    // 屏幕密度
    private final float mPpi;

    // A context-specific coefficient adjusted to physical values.
    private float mPhysicalCoeff;

    static {
        float x_min = 0.0f;
        for (int i = 0; i < NB_SAMPLES; i++) {
            final float alpha = (float) i / NB_SAMPLES;

            float x_max = 1.0f;
            float x, tx, coef;
            while (true) {
                x = x_min + (x_max - x_min) / 2.0f;
                coef = 3.0f * x * (1.0f - x);
                tx = coef * ((1.0f - x) * P1 + x * P2) + x * x * x;
                if (Math.abs(tx - alpha) < 1E-5) break;
                if (tx > alpha) x_max = x;
                else x_min = x;
            }
            SPLINE_POSITION[i] = coef * ((1.0f - x) * START_TENSION + x) + x * x * x;
        }
        SPLINE_POSITION[NB_SAMPLES] = 1.0f;
        // This controls the viscous fluid effect (how much of it)
        sViscousFluidScale = 8.0f;
        // must be set to 1.0 (used in viscousFluid())
        sViscousFluidNormalize = 1.0f;
        sViscousFluidNormalize = 1.0f / viscousFluid(1.0f);

    }

    private static float sViscousFluidScale;
    private static float sViscousFluidNormalize;

    public void setInterpolator(TimeInterpolator interpolator) {
        mInterpolator = interpolator;
    }

    /**
     * Create a Scroller with the default duration and interpolator.
     */
    public MenuSweeper(Context context) {
        this(context, null);
    }

    /**
     * Create a Scroller with the specified interpolator. If the interpolator is
     * null, the default (viscous) interpolator will be used. "Flywheel" behavior will
     * be in effect for apps targeting Honeycomb or newer.
     */
    public MenuSweeper(Context context, Interpolator interpolator) {
        this(context, interpolator,
                context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.HONEYCOMB);
    }

    /**
     * Create a Scroller with the specified interpolator. If the interpolator is
     * null, the default (viscous) interpolator will be used. Specify whether or
     * not to support progressive "flywheel" behavior in flinging.
     */
    public MenuSweeper(Context context, Interpolator interpolator, boolean flywheel) {
        mFinished = true;
        mInterpolator = interpolator;
        mPpi = context.getResources().getDisplayMetrics().density * 160.0f;
        mDeceleration = computeDeceleration(ViewConfiguration.getScrollFriction());
        mFlywheel = flywheel;
        mPhysicalCoeff = computeDeceleration(0.84f); // look and feel tuning
    }

    /**
     * The amount of friction applied to flings. The default value
     * is {@link ViewConfiguration#getScrollFriction}.
     *
     * @param friction A scalar dimension-less value representing the coefficient of
     *                 friction.
     */
    public final void setFriction(float friction) {
        mDeceleration = computeDeceleration(friction);
        mFlingFriction = friction;
    }

    /**
     * 计算减速
     *
     * @param friction 减速因子
     *
     * @return 减速度
     */
    private float computeDeceleration(float friction) {
        return SensorManager.GRAVITY_EARTH   // g (m/s^2)
                * 39.37f               // inch/meter
                * mPpi                 // pixels per inch
                * friction;
    }

    /**
     * Returns whether the scroller has finished scrolling.
     *
     * @return True if the scroller has finished scrolling, false otherwise.
     */
    public final boolean isFinished() {
        return mFinished;
    }

    /**
     * Force the finished field to a particular value.
     *
     * @param finished The new finished value.
     */
    public final void forceFinished(boolean finished) {
        mFinished = finished;
    }

    /**
     * Returns how long the scroll event will take, in milliseconds.
     *
     * @return The duration of the scroll in milliseconds.
     */
    public final int getDuration() {
        return mDuration;
    }

    /**
     * Returns the current X offset in the scroll.
     *
     * @return The new X offset as an absolute distance from the origin.
     */
    public final int getCurrAngel() {
        return mCurrAngel;
    }

    /**
     * Returns the current velocity.
     *
     * @return The original velocity less the deceleration. Result may be
     * negative.
     */
    public float getCurrVelocity() {
        return mMode == FLING_MODE ?
                mCurrVelocity : mVelocity - mDeceleration * timePassed() / 2000.0f;
    }

    /**
     * Returns the start X offset in the scroll.
     *
     * @return The start X offset as an absolute distance from the origin.
     */
    public final int getStartAngel() {
        return mStartAngel;
    }

    /**
     * Returns where the scroll will end. Valid only for "fling" scrolls.
     *
     * @return The final X offset as an absolute distance from the origin.
     */
    public final int getFinalAngel() {
        return mFinalAngel;
    }

    /**
     * Call this when you want to know the new location.  If it returns true,
     * the animation is not yet finished.
     */
    public boolean computeSweepOffset() {
        if (mFinished) {
            return false;
        }

        // 执行动画经过的时间
        int timePassed = (int) (AnimationUtils.currentAnimationTimeMillis() - mStartTime);

        if (timePassed < mDuration) {
            switch (mMode) {
                case SWEEP_MODE:
                    // 已经扫过的角度
                    float x = timePassed * mDurationReciprocal;

                    if (mInterpolator == null)
                        x = viscousFluid(x);
                    else
                        x = mInterpolator.getInterpolation(x);

                    // 四舍五入
                    mCurrAngel = mStartAngel + Math.round(x * mDeltaAngel);
                    break;
                case FLING_MODE:
                    final float t = (float) timePassed / mDuration;
                    final int index = (int) (NB_SAMPLES * t);
                    float distanceCoef = 1.f;
                    float velocityCoef = 0.f;
                    if (index < NB_SAMPLES) {
                        final float t_inf = (float) index / NB_SAMPLES;
                        final float t_sup = (float) (index + 1) / NB_SAMPLES;
                        final float d_inf = SPLINE_POSITION[index];
                        final float d_sup = SPLINE_POSITION[index + 1];
                        velocityCoef = (d_sup - d_inf) / (t_sup - t_inf);
                        distanceCoef = d_inf + (t - t_inf) * velocityCoef;
                    }

                    mCurrVelocity = velocityCoef * mDistance / mDuration * 1000.0f;

                    mCurrAngel = mStartAngel + Math.round(distanceCoef * (mFinalAngel - mStartAngel));
                    // Pin to mMinX <= mCurrX <= mMaxX
                    mCurrAngel = Math.min(mCurrAngel, mMaxAngel);
                    mCurrAngel = Math.max(mCurrAngel, mMinAngel);

                    if (mCurrAngel == mFinalAngel) {
                        mFinished = true;
                    }

                    break;
            }
        } else {
            mCurrAngel = mFinalAngel;
            mFinished = true;
        }
        return true;
    }

    /**
     * Start scrolling by providing a starting point and the distance to travel.
     * The scroll will use the default value of 250 milliseconds for the
     * duration.
     *
     * @param startAngel Starting horizontal scroll offset in pixels. Positive
     *                   numbers will scroll the content to the left.
     * @param deltaAngel Horizontal distance to travel. Positive numbers will scroll the
     *                   content to the left.
     */
    public void startSweep(int startAngel, int deltaAngel) {
        startSweep(startAngel, deltaAngel, DEFAULT_DURATION);
    }

    /**
     * Start scrolling by providing a starting point, the distance to travel,
     * and the duration of the scroll.
     *
     * @param startAngel 开始旋转时的角度
     * @param deltaAngel Angel to sweep.
     * @param duration   Duration of the scroll in milliseconds.
     */
    public void startSweep(int startAngel, int deltaAngel, int duration) {
        mMode = SWEEP_MODE;
        mFinished = false;
        mDuration = duration;
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
        mStartAngel = startAngel;
        mFinalAngel = startAngel + deltaAngel;
        mDeltaAngel = deltaAngel;
        mDurationReciprocal = 1.0f / (float) mDuration;
    }

    /**
     * Start scrolling based on a fling gesture. The distance travelled will
     * depend on the initial velocity of the fling.
     *
     * @param startAngel    Starting point of the scroll (X)
     * @param velocityAngel Initial velocity of the fling (X) measured in pixels per
     *                      second.
     * @param minX          Minimum X value. The scroller will not scroll past this
     *                      point.
     * @param maxX          Maximum X value. The scroller will not scroll past this
     *                      point.
     */
    public void fling(int startAngel, int velocityAngel, int minX, int maxX) {
        // Continue a scroll or fling in progress
        if (mFlywheel && !mFinished) {
            float oldVel = getCurrVelocity();
            velocityAngel += oldVel;
        }

        mMode = FLING_MODE;
        mFinished = false;

        mVelocity = velocityAngel;
        mDuration = getSplineFlingDuration(velocityAngel);
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
        mStartAngel = startAngel;

        // 惯性滑动总距离
        mDistance = (int) getSplineFlingDistance(velocityAngel);

        mMinAngel = minX;
        mMaxAngel = maxX;

        // Math.round：四舍五入
        mFinalAngel = startAngel + mDistance;
        // Pin to mMinX <= mFinalX <= mMaxX
        mFinalAngel = Math.min(mFinalAngel, mMaxAngel);
        mFinalAngel = Math.max(mFinalAngel, mMinAngel);
    }

    private double getSplineDeceleration(float velocity) {
        return Math.log(INFLEXION * Math.abs(velocity) / (mFlingFriction * mPhysicalCoeff));
    }

    private int getSplineFlingDuration(float velocity) {
        final double l = getSplineDeceleration(velocity);
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        return (int) (1000.0 * Math.exp(l / decelMinusOne));
    }

    private double getSplineFlingDistance(float velocity) {
        final double l = getSplineDeceleration(velocity);
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        return mFlingFriction * mPhysicalCoeff * Math.exp(DECELERATION_RATE / decelMinusOne * l);
    }

    static float viscousFluid(float x) {
        x *= sViscousFluidScale;
        if (x < 1.0f) {
            x -= (1.0f - (float) Math.exp(-x));
        } else {
            float start = 0.36787944117f;   // 1/e == exp(-1)
            x = 1.0f - (float) Math.exp(1.0f - x);
            x = start + x * (1.0f - start);
        }
        x *= sViscousFluidNormalize;
        return x;
    }

    /**
     * Stops the animation. Contrary to {@link #forceFinished(boolean)},
     * aborting the animating cause the scroller to move to the final x and y
     * position
     *
     * @see #forceFinished(boolean)
     */
    public void abortAnimation() {
        mCurrAngel = mFinalAngel;
        mFinished = true;
    }

    /**
     * Extend the scroll animation. This allows a running animation to scroll
     * further and longer, when used with {@link #setFinalX(int)}.
     *
     * @param extend Additional time to scroll in milliseconds.
     *
     * @see #setFinalX(int)
     */
    public void extendDuration(int extend) {
        int passed = timePassed();
        mDuration = passed + extend;
        mDurationReciprocal = 1.0f / mDuration;
        mFinished = false;
    }

    /**
     * Returns the time elapsed since the beginning of the scrolling.
     *
     * @return The elapsed time in milliseconds.
     */
    public int timePassed() {
        return (int) (AnimationUtils.currentAnimationTimeMillis() - mStartTime);
    }

    /**
     * Sets the final position (X) for this scroller.
     *
     * @param newX The new X offset as an absolute distance from the origin.
     *
     * @see #extendDuration(int)
     */
    public void setFinalX(int newX) {
        mFinalAngel = newX;
        mDeltaAngel = mFinalAngel - mStartAngel;
        mFinished = false;
    }

    public boolean isScrollingInDirection(float xVel) {
        return !mFinished && Math.signum(xVel) == Math.signum(mFinalAngel - mStartAngel);
    }
}
