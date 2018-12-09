package com.android.launcher3.menu;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.menu.view.HorizontalPageScrollView;
import com.android.launcher3.menu.view.MenuLayout;
import com.android.mxlibrary.util.XLog;

/**
 * Created by CodeMX
 * DATE 2018/1/16
 * TIME 15:24
 */

public class MenuStateTransitionAnimation {

    public static final int MENU_DURATION = 600;
    public static final int BACKGROUND_FADE_OUT_DURATION = 600;

    private MenuLayout mMenuLayout;
    private AnimatorSet mStateAnimator;

    public MenuStateTransitionAnimation(Context context, MenuLayout menuLayout) {
        this.mMenuLayout = menuLayout;
    }

    public void startAnimationToNewMenuLayoutState(final MenuLayout.State fromState,
                                                   MenuLayout.State toState,
                                                   boolean animated) {
        XLog.e(XLog.getTag(), XLog.TAG_GU + "fromState==  " + fromState + "  toState==  " + toState);
        if (mStateAnimator != null) {
            return;
        }
        if (animated) {
            mStateAnimator = LauncherAnimUtils.createAnimatorSet();
        }
        TransitionStates states = new TransitionStates(fromState, toState);
        int menuDuration = getAnimationDuration(states);
        animateMenuLayout(states, animated, menuDuration);
    }

    private void animateMenuLayout(TransitionStates states, boolean animated, int menuDuration) {
        float finalAlpha = 1.0f;
        HorizontalPageScrollView menuView = null;
        if (states.menuToWidget || states.menuToEffect) {
            menuView = mMenuLayout.getMenuListLayout();
        } else if (states.effectToMenu || states.widgetToMenu || states.widgetToWidgetList) {
            menuView = mMenuLayout.getMenuWidgetAndEffectLayout();
        } else if (states.widgetListToWidget) {
            menuView = mMenuLayout.getMenuWidgetListLayout();
        }
        if (animated) {
            animateBackgroundGradient(menuView, finalAlpha, 0, menuDuration);
        } else {
            mMenuLayout.setVisibility(View.GONE);
            if (menuView != null) {
                menuView.setAlpha(finalAlpha);
            }
        }
    }

    private void setMenuAnimationProgress(HorizontalPageScrollView menuList, float progress) {
        final int childCount = menuList.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = menuList.getChildAt(i);
            child.setScaleX(progress);
            child.setScaleY(progress);
        }
    }

    public void animateBackgroundGradient(final View view, float startAlpha, float finalAlpha, int duration) {
        if (view == null) {
            mStateAnimator = null;
            return;
        }
        ValueAnimator alpha = ValueAnimator.ofFloat(startAlpha, finalAlpha);
        alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                view.setAlpha(progress);
                setMenuAnimationProgress((HorizontalPageScrollView) view, progress);
            }
        });
        alpha.addListener(new MenuAnimatorUpdateListener(view, true));
        alpha.setInterpolator(new DecelerateInterpolator());
        alpha.setDuration(duration);
        mStateAnimator.play(alpha);
        mStateAnimator.start();
    }

    private class MenuAnimatorUpdateListener implements Animator.AnimatorListener {
        View view;
        boolean isNeedHide;

        public MenuAnimatorUpdateListener(View view, boolean isNeedHide) {
            this.view = view;
            this.isNeedHide = isNeedHide;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (isNeedHide && view != null) {
                view.setVisibility(View.GONE);
                setMenuAnimationProgress((HorizontalPageScrollView) view, 1);
            }
            mStateAnimator.getChildAnimations().clear();
            mStateAnimator = null;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mStateAnimator = null;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

    }

    private int getAnimationDuration(TransitionStates states) {
        if (states.noneToMenu || states.menuToNone) {
            return MENU_DURATION;
        } else if (states.menuToWidget || states.menuToEffect || states.widgetToMenu
                || states.effectToMenu || states.widgetToWidgetList || states.widgetListToWidget) {
            return MENU_DURATION;
        } else {
            return MENU_DURATION;
        }
    }

    public boolean isMenuAnimationRunning() {
        return mStateAnimator != null;
    }
}

class TransitionStates {

    final boolean oldStateIsNone;
    final boolean oldStateIsMenu;
    final boolean oldStateIsWidget;
    final boolean oldStateIsWidgetList;
    final boolean oldStateIsEffect;

    final boolean stateIsNone;
    final boolean stateIsMenu;
    final boolean stateIsWidget;
    final boolean stateIsWidgetList;
    final boolean stateIsEffect;

    final boolean noneToMenu;
    final boolean menuToNone;
    final boolean menuToWidget;
    final boolean widgetToMenu;
    final boolean menuToEffect;
    final boolean effectToMenu;
    final boolean widgetToWidgetList;
    final boolean widgetListToWidget;


    public TransitionStates(final MenuLayout.State fromState, final MenuLayout.State toState) {

        XLog.e(XLog.getTag(), XLog.TAG_GU + "  fromState==  " + fromState + "  toState==  " + toState);
        oldStateIsNone = (fromState == MenuLayout.State.NONE);
        oldStateIsMenu = (fromState == MenuLayout.State.MENU);
        oldStateIsWidget = (fromState == MenuLayout.State.WIDGET);
        oldStateIsWidgetList = (fromState == MenuLayout.State.WIDGET_LIST);
        oldStateIsEffect = (fromState == MenuLayout.State.EFFECT);

        stateIsNone = (toState == MenuLayout.State.NONE);
        stateIsMenu = (toState == MenuLayout.State.MENU);
        stateIsWidget = (toState == MenuLayout.State.WIDGET);
        stateIsWidgetList = (toState == MenuLayout.State.WIDGET_LIST);
        stateIsEffect = (toState == MenuLayout.State.EFFECT);

        noneToMenu = (oldStateIsNone && stateIsMenu);
        menuToNone = (oldStateIsMenu && stateIsNone);
        menuToWidget = (oldStateIsMenu && stateIsWidget);
        widgetToMenu = (oldStateIsWidget && stateIsMenu);
        menuToEffect = (oldStateIsMenu && stateIsEffect);
        effectToMenu = (oldStateIsEffect && stateIsMenu);
        widgetToWidgetList = (oldStateIsWidget && stateIsWidgetList);
        widgetListToWidget = (oldStateIsWidgetList && stateIsWidget);

    }
}
