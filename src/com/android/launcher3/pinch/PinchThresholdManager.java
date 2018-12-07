package com.android.launcher3.pinch;


import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;

/**
 * Keeps track of when thresholds（阈值） are passed during a pinch gesture,
 * used to inform（通知） {@link PinchAnimationManager} throughout（全部）.
 *
 * @see PinchToOverviewListener
 * @see PinchAnimationManager
 *
 * add by codemx.cn  20181026
 */
public class PinchThresholdManager {

    public static final float THRESHOLD_ZERO = 0.0f;
    public static final float THRESHOLD_ONE = 0.40f;
    public static final float THRESHOLD_TWO = 0.70f;
    public static final float THRESHOLD_THREE = 0.95f;

    private Workspace mWorkspace;

    private float mPassedThreshold = THRESHOLD_ZERO;

    public PinchThresholdManager(Workspace workspace) {
        mWorkspace = workspace;
    }

    /**
     * Uses the pinch progress to determine whether a threshold has been passed,
     * and asks the {@param animationManager} to animate if so.
     *
     * @param progress         From 0 to 1, where 0 is overview and 1 is workspace.
     * @param animationManager Animates the threshold change if one is passed.
     *
     * @return The last passed threshold, one of
     * {@link PinchThresholdManager#THRESHOLD_ZERO},
     * {@link PinchThresholdManager#THRESHOLD_ONE},
     * {@link PinchThresholdManager#THRESHOLD_TWO}, or
     * {@link PinchThresholdManager#THRESHOLD_THREE}
     */
    public float updateAndAnimatePassedThreshold(float progress,
                                                 PinchAnimationManager animationManager) {
        if (!mWorkspace.isInOverviewMode()) {
            // Invert the progress, because going from workspace to overview is 1 to 0.
            progress = 1f - progress;
        }

        float previousPassedThreshold = mPassedThreshold;

        if (progress < THRESHOLD_ONE) {
            mPassedThreshold = THRESHOLD_ZERO;
        } else if (progress < THRESHOLD_TWO) {
            mPassedThreshold = THRESHOLD_ONE;
        } else if (progress < THRESHOLD_THREE) {
            mPassedThreshold = THRESHOLD_TWO;
        } else {
            mPassedThreshold = THRESHOLD_THREE;
        }

        if (mPassedThreshold != previousPassedThreshold) {
            LauncherState fromState = mWorkspace.isInOverviewMode() ? LauncherState.OVERVIEW
                    : LauncherState.NORMAL;
            LauncherState toState = mWorkspace.isInOverviewMode() ? LauncherState.NORMAL
                    : LauncherState.OVERVIEW;
            float thresholdToAnimate = mPassedThreshold;
            if (mPassedThreshold < previousPassedThreshold) {
                // User reversed pinch, so heading back to the state that they started from.
                toState = fromState;
                thresholdToAnimate = previousPassedThreshold;
            }
            animationManager.animateThreshold(thresholdToAnimate, fromState, toState);
        }
        return mPassedThreshold;
    }

    public float getPassedThreshold() {
        return mPassedThreshold;
    }

    public void reset() {
        mPassedThreshold = THRESHOLD_ZERO;
    }

}
