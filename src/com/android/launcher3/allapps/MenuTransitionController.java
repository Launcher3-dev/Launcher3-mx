package com.android.launcher3.allapps;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.Property;
import android.view.animation.Interpolator;

import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DeviceProfile.OnDeviceProfileChangeListener;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.LauncherStateManager.AnimationConfig;
import com.android.launcher3.LauncherStateManager.StateHandler;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorSetBuilder;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.menu.CircleMenuView;

import static com.android.launcher3.LauncherState.OVERVIEW;
import static com.android.launcher3.anim.AnimatorSetBuilder.ANIM_OVERVIEW_SCALE;
import static com.android.launcher3.anim.AnimatorSetBuilder.ANIM_VERTICAL_PROGRESS;
import static com.android.launcher3.anim.Interpolators.FAST_OUT_SLOW_IN;
import static com.android.launcher3.anim.Interpolators.LINEAR;
import static com.android.launcher3.anim.PropertySetter.NO_ANIM_PROPERTY_SETTER;

/**
 * Handles AllApps view transition.
 * 1) Slides all apps view using direct manipulation
 * 2) When finger is released, animate to either top or bottom accordingly.
 * <p/>
 * Algorithm:
 * If release velocity > THRES1, snap according to the direction of movement.
 * If release velocity < THRES1, snap according to either top or bottom depending on whether it's
 * closer to top or closer to the page indicator.
 */
public class MenuTransitionController implements StateHandler, OnDeviceProfileChangeListener {

    public static final Property<MenuTransitionController, Float> CIRCLE_MENU_VIEW_PROGRESS =
            new Property<MenuTransitionController, Float>(Float.class, "allAppsProgress") {

                @Override
                public Float get(MenuTransitionController controller) {
                    return controller.mProgress;
                }

                @Override
                public void set(MenuTransitionController controller, Float progress) {
                    controller.setProgress(progress);
                }
            };

    private CircleMenuView mCircleMenuView;

    private final Launcher mLauncher;
    private boolean mIsVerticalLayout;

    // Animation in this class is controlled by a single variable {@link mProgress}.
    // Visually, it represents top y coordinate of the all apps container if multiplied with
    // {@link mShiftRange}.

    // When {@link mProgress} is 0, all apps container is pulled up.
    // When {@link mProgress} is 1, all apps container is pulled down.
    private float mShiftRange;      // changes depending on the orientation
    private float mProgress;        // [0, 1], mShiftRange * mProgress = shiftCurrent

    private float mScrollRangeDelta = 0;

    public MenuTransitionController(Launcher l) {
        mLauncher = l;
        mShiftRange = mLauncher.getDeviceProfile().heightPx;
        mProgress = 1f;

        mIsVerticalLayout = mLauncher.getDeviceProfile().isVerticalBarLayout();
        mLauncher.addOnDeviceProfileChangeListener(this);
    }

    // add by codemx.cn ---- 20180919 ---- start
    public void setMenuView(CircleMenuView circleMenuView) {
        this.mCircleMenuView = circleMenuView;
    }
    // add by codemx.cn ---- 20180919 ---- end

    public float getShiftRange() {
        return mShiftRange;
    }

    private void onProgressAnimationStart() {
        // Initialize values that should not change until #onDragEnd

    }

    @Override
    public void onDeviceProfileChanged(DeviceProfile dp) {
        mIsVerticalLayout = dp.isVerticalBarLayout();
        setScrollRangeDelta(mScrollRangeDelta);

        if (mIsVerticalLayout) {
            mLauncher.getHotseat().setTranslationY(0);
            mLauncher.getWorkspace().getPageIndicator().setTranslationY(0);
        }
    }

    /**
     * Note this method should not be called outside this class. This is public because it is used
     * in xml-based animations which also handle updating the appropriate UI.
     *
     * @param progress value between 0 and 1, 0 shows all apps and 1 shows workspace
     *
     * @see #setState(LauncherState)
     * @see #setStateWithAnimation(LauncherState, AnimatorSetBuilder, AnimationConfig)
     */
    public void setProgress(float progress) {
        mProgress = progress;
        float shiftCurrent = progress * mShiftRange;

        // add by codemx.cn ---- 20180919 ---- start
        mCircleMenuView.setTranslationY(shiftCurrent);
        // add by codemx.cn ---- 20180919 ---- start

        float hotseatTranslation = -mShiftRange + shiftCurrent;

        if (!mIsVerticalLayout) {
            mLauncher.getHotseat().setTranslationY(hotseatTranslation);
            mLauncher.getWorkspace().getPageIndicator().setTranslationY(hotseatTranslation);
        }

    }

    public float getProgress() {
        return mProgress;
    }

    /**
     * Sets the vertical transition progress to {@param state} and updates all the dependent UI
     * accordingly.
     */
    @Override
    public void setState(LauncherState state) {
        setProgress(state.getVerticalProgress(mLauncher));
        setAlphas(state, NO_ANIM_PROPERTY_SETTER);
        onProgressAnimationEnd();
    }

    /**
     * Creates an animation which updates the vertical transition progress and updates all the
     * dependent UI using various animation events
     */
    @Override
    public void setStateWithAnimation(LauncherState toState,
                                      AnimatorSetBuilder builder, AnimationConfig config) {
        float targetProgress = toState.getVerticalProgress(mLauncher);
        if (Float.compare(mProgress, targetProgress) == 0) {
            setAlphas(toState, config.getPropertySetter(builder));
            // Fail fast
            onProgressAnimationEnd();
            return;
        }

        if (!config.playNonAtomicComponent()) {
            // There is no atomic component for the all apps transition, so just return early.
            return;
        }

        Interpolator interpolator = config.userControlled ? LINEAR : toState == OVERVIEW
                ? builder.getInterpolator(ANIM_OVERVIEW_SCALE, FAST_OUT_SLOW_IN)
                : FAST_OUT_SLOW_IN;
        ObjectAnimator anim =
                ObjectAnimator.ofFloat(this, CIRCLE_MENU_VIEW_PROGRESS, mProgress, targetProgress);
        anim.setDuration(config.duration);
        anim.setInterpolator(builder.getInterpolator(ANIM_VERTICAL_PROGRESS, interpolator));
        anim.addListener(getProgressAnimatorListener());

        builder.play(anim);

        setAlphas(toState, config.getPropertySetter(builder));
    }

    private void setAlphas(LauncherState toState, PropertySetter setter) {
        int visibleElements = toState.getVisibleElements(mLauncher);


    }

    public AnimatorListenerAdapter getProgressAnimatorListener() {
        return new AnimationSuccessListener() {
            @Override
            public void onAnimationSuccess(Animator animator) {
                onProgressAnimationEnd();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                onProgressAnimationStart();
            }
        };
    }

    /**
     * Updates the total scroll range but does not update the UI.
     */
    public void setScrollRangeDelta(float delta) {
        mScrollRangeDelta = delta;
        mShiftRange = mLauncher.getDeviceProfile().heightPx - mScrollRangeDelta;


    }

    /**
     * Set the final view states based on the progress.
     * TODO: This logic should go in {@link LauncherState}
     */
    private void onProgressAnimationEnd() {
        if (Float.compare(mProgress, 1f) == 0) {

        } else if (Float.compare(mProgress, 0f) == 0) {

        } else {

        }
    }
}
