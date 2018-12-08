package com.android.launcher3.pinch;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.android.launcher3.Hotseat;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.AnimationLayerSet;
import com.android.launcher3.menu.MenuLayout;
import com.android.launcher3.userevent.nano.LauncherLogProto;

/**
 * Manages the animations that play as the user pinches to/from overview mode.
 * <p>
 * It will look like this pinching in:
 * - Workspace scales down
 * - At some threshold 1, hotseat and QSB fade out (full animation)
 * - At a later threshold 2, panel buttons fade in and scrim fades in
 * - At a final threshold 3, snap to overview
 * <p>
 * Pinching out:
 * - Workspace scales up
 * - At threshold 1, panel buttons fade out
 * - At threshold 2, hotseat and QSB fade in and scrim fades out
 * - At threshold 3, snap to workspace
 *
 * @see PinchToOverviewListener
 * @see PinchThresholdManager
 *
 * add by codemx.cn  20181026
 */
public class PinchAnimationManager {

    private static final String TAG = "PinchAnimationManager";

    private static final int THRESHOLD_ANIM_DURATION = 150;
    private static final LinearInterpolator INTERPOLATOR = new LinearInterpolator();

    private final Animator[] mAnimators = new Animator[4];

    private Launcher mLauncher;
    private Workspace mWorkspace;
    private Hotseat mHotseat;
    private MenuLayout mMenuLayout;

    private float mOverviewScale;
    private float mMenuTranslationY;
    private int mNormalOverviewTransitionDuration;
    private boolean mIsAnimating;

    public PinchAnimationManager(Launcher launcher) {
        mLauncher = launcher;
        mWorkspace = launcher.getWorkspace();
        mHotseat = launcher.getHotseat();
        mMenuLayout = launcher.getMenuLayout();

        mOverviewScale = mWorkspace.getOverviewModeShrinkFactor();
        mMenuTranslationY = mMenuLayout.getMenuLayoutShowTranslationY();
        mNormalOverviewTransitionDuration = mWorkspace.getStateTransitionAnimation()
                .mOverviewTransitionTime;
    }

    public int getNormalOverviewTransitionDuration() {
        return mNormalOverviewTransitionDuration;
    }

    /**
     * Interpolate from {@param currentProgress} to {@param toProgress}, calling
     * {@link #setAnimationProgress(float)} throughout the duration. If duration is -1,
     * the default overview transition duration is used.
     */
    public void animateToProgress(float currentProgress, float toProgress, int duration,
                                  final PinchThresholdManager thresholdManager) {
        if (duration == -1) {
            duration = mNormalOverviewTransitionDuration;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(currentProgress, toProgress);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                       @Override
                                       public void onAnimationUpdate(ValueAnimator animation) {
                                           float pinchProgress = (Float) animation.getAnimatedValue();
                                           setAnimationProgress(pinchProgress);
                                           thresholdManager.updateAndAnimatePassedThreshold(pinchProgress,
                                                   PinchAnimationManager.this);
                                       }
                                   }
        );
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
                thresholdManager.reset();
                mWorkspace.onEndStateTransition();
            }
        });
        animator.setDuration(duration).start();
        mIsAnimating = true;
    }

    public boolean isAnimating() {
        return mIsAnimating;
    }

    /**
     * Animates to the specified progress. This should be called repeatedly throughout the pinch
     * gesture to run animations that interpolate throughout the gesture.
     *
     * @param interpolatedProgress The progress from 0 to 1, where 0 is overview and 1 is workspace.
     */
    public void setAnimationProgress(float interpolatedProgress) {
        float interpolatedScale = interpolatedProgress * (1f - mOverviewScale) + mOverviewScale;
        float menuTranslationY = (1f - interpolatedProgress) * mMenuTranslationY;
        setWorkspaceAnimationProgress(interpolatedScale);
        setHotseatAnimationProgress(menuTranslationY);
        setPageIndicatorAnimationProgress(interpolatedProgress);
        setMenuAnimationProgress(menuTranslationY);
    }

    /**
     * 两只捏掐缩放屏幕，每个CellLayout单独缩放，每个CellLayout占据一个屏幕
     *
     * @param progress 缩放进度
     */
    private void setWorkspaceAnimationProgress(float progress) {
        int childCount = mWorkspace.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mWorkspace.getChildAt(i);
            view.setScaleX(progress);
            view.setScaleY(progress);
        }
    }

    /**
     * 缩放过程中Hotseat的移动
     *
     * @param interpolatedProgress 移动百分比
     */
    private void setPageIndicatorAnimationProgress(float interpolatedProgress) {
        mWorkspace.getPageIndicator().setTranslationY(mWorkspace.getPageIndicatorTranslationY() * (1f - interpolatedProgress));
    }

    /**
     * 缩放过程中Hotseat的移动
     *
     * @param hotseatTranslationY 移动距离
     */
    private void setHotseatAnimationProgress(float hotseatTranslationY) {
        mHotseat.setTranslationY(hotseatTranslationY);
    }

    /**
     * 缩放过程中MenuLayout的移动
     *
     * @param menuTranslationY 移动距离
     */
    private void setMenuAnimationProgress(float menuTranslationY) {
        mMenuLayout.setAlpha(1.0f);
        mMenuLayout.setVisibility(View.VISIBLE);
        mMenuLayout.setTranslationY(-menuTranslationY);
    }

    /**
     * Animates certain properties based on which threshold was passed, and in what direction. The
     * starting state must also be taken into account because the thresholds mean different things
     * when going from workspace to overview and vice versa.
     *
     * @param threshold    One of {@link PinchThresholdManager#THRESHOLD_ONE},
     *                     {@link PinchThresholdManager#THRESHOLD_TWO}, or
     *                     {@link PinchThresholdManager#THRESHOLD_THREE}
     * @param startState   {@link LauncherState#NORMAL} or {@link LauncherState#OVERVIEW}.
     * @param goingTowards {@link LauncherState#NORMAL} or {@link LauncherState#OVERVIEW}.
     *                     Note that this doesn't have to be the opposite of startState;
     */
    public void animateThreshold(float threshold, LauncherState startState,
                                 LauncherState goingTowards) {
        if (threshold == PinchThresholdManager.THRESHOLD_ONE) {

        } else if (threshold == PinchThresholdManager.THRESHOLD_TWO) {
            if (startState == LauncherState.OVERVIEW) {

            } else if (startState == LauncherState.NORMAL) {

            }
        } else if (threshold == PinchThresholdManager.THRESHOLD_THREE) {
            // Passing threshold 3 ends the pinch and snaps to the new state.
            if (startState == LauncherState.OVERVIEW && goingTowards == LauncherState.NORMAL) {
                mLauncher.getUserEventDispatcher().logActionOnContainer(
                        LauncherLogProto.Action.Touch.PINCH, LauncherLogProto.Action.Direction.NONE,
                        LauncherLogProto.ContainerType.OVERVIEW, mWorkspace.getCurrentPage());
                // 显示桌面
                mLauncher.getStateManager().goToState(LauncherState.NORMAL);
            } else if (startState == LauncherState.NORMAL && goingTowards == LauncherState.OVERVIEW) {
                mLauncher.getUserEventDispatcher().logActionOnContainer(
                        LauncherLogProto.Action.Touch.PINCH, LauncherLogProto.Action.Direction.NONE,
                        LauncherLogProto.ContainerType.WORKSPACE, mWorkspace.getCurrentPage());
                // 显示预览模式
                mLauncher.getStateManager().goToState(LauncherState.OVERVIEW);
            }
        } else {
            Log.e(TAG, "Received unknown threshold to animate: " + threshold);
        }
    }

    private void animateShowHideView(int index, final View view, boolean show) {
        Animator animator = ObjectAnimator.ofFloat(view, View.ALPHA, show ? 1 : 0);
        animator.addListener(new AnimationLayerSet(view));
        if (show) {
            view.setVisibility(View.VISIBLE);
        } else {
            animator.addListener(new AnimatorListenerAdapter() {
                private boolean mCancelled = false;

                @Override
                public void onAnimationCancel(Animator animation) {
                    mCancelled = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!mCancelled) {
                        view.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        startAnimator(index, animator, THRESHOLD_ANIM_DURATION);
    }

    private void startAnimator(int index, Animator animator, long duration) {
        if (mAnimators[index] != null) {
            mAnimators[index].cancel();
        }
        mAnimators[index] = animator;
        mAnimators[index].setInterpolator(INTERPOLATOR);
        mAnimators[index].setDuration(duration).start();
    }

}
