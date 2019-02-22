package com.android.launcher3.pinch;

import android.animation.TimeInterpolator;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.LogAccelerateInterpolator;
import com.android.launcher3.LogDecelerateInterpolator;
import com.android.launcher3.Workspace;
import com.android.launcher3.util.TouchController;

/**
 * Detects pinches and animates the Workspace to/from overview mode.
 * <p>
 * Usage: Pass MotionEvents to onInterceptTouchEvent() and onTouchEvent(). This class will handle
 * the pinch detection, and use {@link PinchAnimationManager} to handle the animations.
 *
 * @see PinchThresholdManager
 * @see PinchAnimationManager
 * <p>
 * add by codemx.cn  20181026
 */
public class PinchToOverviewListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
        implements TouchController {
    private static final float OVERVIEW_PROGRESS = 0f;
    private static final float WORKSPACE_PROGRESS = 1f;
    /**
     * The velocity threshold at which a pinch will be completed instead of canceled,
     * even if the first threshold has not been passed. Measured in progress / millisecond
     */
    private static final float FLING_VELOCITY = 0.003f;

    private ScaleGestureDetector mPinchDetector;
    private Launcher mLauncher;
    private Workspace mWorkspace = null;
    private boolean mPinchStarted = false;
    private float mPreviousProgress;
    private float mProgressDelta;
    private long mPreviousTimeMillis;
    // 缩放时间
    private long mTimeDelta;
    private boolean mPinchCanceled = false;
    private TimeInterpolator mInterpolator;

    private PinchThresholdManager mThresholdManager;
    private PinchAnimationManager mAnimationManager;

    public PinchToOverviewListener(Launcher launcher) {
        mLauncher = launcher;
        mPinchDetector = new ScaleGestureDetector(mLauncher, this);
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        mPinchDetector.onTouchEvent(ev);
        return mPinchStarted;
    }

    public boolean onControllerTouchEvent(MotionEvent ev) {
        if (mPinchStarted) {
            if (ev.getPointerCount() > 2) {
                // Using more than two fingers causes weird behavior, so just cancel the pinch.
                cancelPinch(mPreviousProgress, -1);
            } else {
                return mPinchDetector.onTouchEvent(ev);
            }
        }
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        if (mLauncher.getStateManager().getState() == LauncherState.MENU) {
            // Don't listen for the pinch gesture if on all apps, widget picker, -1, etc.
            return false;
        }
        if (mAnimationManager != null && mAnimationManager.isAnimating()) {
            // Don't listen for the pinch gesture if we are already animating from a previous one.
            return false;
        }
        if (mLauncher.isWorkspaceLocked()) {
            // Don't listen for the pinch gesture if the workspace isn't ready.
            return false;
        }
        if (mWorkspace == null) {
            mWorkspace = mLauncher.getWorkspace();
            mThresholdManager = new PinchThresholdManager(mWorkspace);
            mAnimationManager = new PinchAnimationManager(mLauncher);
        }
        if (mWorkspace.isSwitchingState() || mWorkspace.mScrollInteractionBegan) {
            // Don't listen for the pinch gesture while switching state, as it will cause a jump
            // once the state switching animation is complete.
            return false;
        }
        if (AbstractFloatingView.getTopOpenView(mLauncher) != null) {
            // Don't listen for the pinch gesture if a floating view is open.
            return false;
        }

        mPreviousProgress = mWorkspace.isInOverviewMode() ? OVERVIEW_PROGRESS : WORKSPACE_PROGRESS;
        mPreviousTimeMillis = System.currentTimeMillis();
        mInterpolator = mWorkspace.isInOverviewMode() ? new LogDecelerateInterpolator(100, 0)
                : new LogAccelerateInterpolator(100, 0);
        mPinchStarted = true;
        mWorkspace.getTransitionEffect().clearRotation();
        mWorkspace.getTransitionEffect().clearTranslationX();
        mWorkspace.onPrepareStateTransition(true);
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        super.onScaleEnd(detector);
        // 缩放百分比速度
        float progressVelocity = mProgressDelta / mTimeDelta;
        // 最近的已经经过的阈值
        float passedThreshold = mThresholdManager.getPassedThreshold();
        // 是否存在惯性滑动
        boolean isFling = mWorkspace.isInOverviewMode() && progressVelocity >= FLING_VELOCITY
                || !mWorkspace.isInOverviewMode() && progressVelocity <= -FLING_VELOCITY;
        // 是否取消缩放（没有惯性滑动，并且最近经过阈值小于0.4f）
        boolean shouldCancelPinch = !isFling && passedThreshold < PinchThresholdManager.THRESHOLD_ONE;
        // If we are going towards overview, mPreviousProgress is how much further we need to
        // go, since it is going from 1 to 0. If we are going to workspace, we want
        // 1 - mPreviousProgress.
        float remainingProgress = mPreviousProgress;
        if (mWorkspace.isInOverviewMode() || shouldCancelPinch) {
            remainingProgress = 1f - mPreviousProgress;
        }
        // 计算
        int duration = computeDuration(remainingProgress, progressVelocity);
        if (shouldCancelPinch) {
            // 取消缩放，恢复到初始位置
            cancelPinch(mPreviousProgress, duration);
        } else if (passedThreshold < PinchThresholdManager.THRESHOLD_THREE) {// 阈值在0.4-0.95之间
            float toProgress = mWorkspace.isInOverviewMode() ?
                    WORKSPACE_PROGRESS : OVERVIEW_PROGRESS;
            mAnimationManager.animateToProgress(mPreviousProgress, toProgress, duration,
                    mThresholdManager);
        } else {// 阈值大于0.95
            mThresholdManager.reset();
            mWorkspace.onEndStateTransition();
        }
        mPinchStarted = false;
        mPinchCanceled = false;
    }

    /**
     * Compute the amount of time required to complete the transition based on the current pinch
     * speed. If this time is too long, instead return the normal duration, ignoring the speed.
     */
    private int computeDuration(float remainingProgress, float progressVelocity) {
        float progressSpeed = Math.abs(progressVelocity);
        int remainingMillis = (int) (remainingProgress / progressSpeed);
        return Math.min(remainingMillis, mAnimationManager.getNormalOverviewTransitionDuration());
    }

    /**
     * Cancels the current pinch, returning back to where the pinch started (either workspace or
     * overview). If duration is -1, the default overview transition duration is used.
     */
    private void cancelPinch(float currentProgress, int duration) {
        if (mPinchCanceled) return;
        mPinchCanceled = true;
        float toProgress = mWorkspace.isInOverviewMode() ? OVERVIEW_PROGRESS : WORKSPACE_PROGRESS;
        mAnimationManager.animateToProgress(currentProgress, toProgress, duration,
                mThresholdManager);
        mPinchStarted = false;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        // 当缩放阈值到达0.95时就不在跟随手指缩放了，而是自动完成剩下部分的缩放（操作在PinchAnimationManager.animateThreshold方法执行）
        if (mThresholdManager.getPassedThreshold() == PinchThresholdManager.THRESHOLD_THREE) {
            // We completed the pinch, so stop listening to further movement until user lets go.
            return true;
        }
        if (mLauncher.getDragController().isDragging()) {
            mLauncher.getDragController().cancelDrag();
        }

        // 两个手指缩放滑动距离:缩放后比初始距离小--为负值，大--为正值
        float pinchDist = detector.getCurrentSpan() - detector.getPreviousSpan();
        if (pinchDist < 0 && mWorkspace.isInOverviewMode() ||
                pinchDist > 0 && !mWorkspace.isInOverviewMode()) {
            // Pinching the wrong way, so ignore.
            return false;
        }
        // Pinch distance must equal the workspace width before switching states.
        int pinchDistanceToCompleteTransition = mWorkspace.getWidth();
        // 预览模式缩放因子
        float overviewScale = mWorkspace.getOverviewModeShrinkFactor();
        // 当前缩放因子
        float initialWorkspaceScale = mWorkspace.isInOverviewMode() ? overviewScale : 1f;
        float pinchScale = initialWorkspaceScale + pinchDist / pinchDistanceToCompleteTransition;
        // Bound the scale between the overview scale and the normal workspace scale (1f).
        pinchScale = Math.max(overviewScale, Math.min(pinchScale, 1f));
        // Progress ranges from 0 to 1, where 0 corresponds to the overview scale and 1
        // corresponds to the normal workspace scale (1f).
        float progress = (pinchScale - overviewScale) / (1f - overviewScale);
        float interpolatedProgress = mInterpolator.getInterpolation(progress);

        mAnimationManager.setAnimationProgress(interpolatedProgress);
        float passedThreshold = mThresholdManager.updateAndAnimatePassedThreshold(
                interpolatedProgress, mAnimationManager);
        if (passedThreshold == PinchThresholdManager.THRESHOLD_THREE) {
            return true;
        }

        mProgressDelta = interpolatedProgress - mPreviousProgress;
        mPreviousProgress = interpolatedProgress;
        mTimeDelta = System.currentTimeMillis() - mPreviousTimeMillis;
        mPreviousTimeMillis = System.currentTimeMillis();
        return false;
    }

}
