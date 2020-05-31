package com.android.launcher3.menu.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Insettable;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.anim.RoundedRectRevealOutlineProvider;
import com.android.launcher3.menu.controller.MenuController;
import com.android.launcher3.widget.WidgetListRowEntry;
import com.android.mxlibrary.util.XLog;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by CodeMX
 * DATE 2018/1/16
 * TIME 16:43
 */

public class MenuLayout extends AbstractFloatingView implements Insettable {

    private MenuController mMenuController;
    private HorizontalPageScrollView mMenuListLayout;
    private HorizontalPageScrollView mMenuWidgetAndEffectLayout;
    private HorizontalPageScrollView mMenuWidgetListLayout;
    private State mState = State.NONE;
    private Launcher mLauncher;

    @Override
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    public enum State {
        NONE,
        MENU,
        WIDGET,
        WIDGET_LIST,
        EFFECT
    }

    public MenuLayout(@NonNull Context context) {
        this(context, null);
    }

    public MenuLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mOutlineRadius = getResources().getDimension(R.dimen.bg_round_rect_radius);
        XLog.e(XLog.getTag(), XLog.TAG_GU_STATE);
        mLauncher = Launcher.getLauncher(context);
    }

    @Override
    protected void handleClose(boolean animate) {

    }

    @Override
    public void logActionCommand(int command) {

    }

    @Override
    protected boolean isOfType(int type) {
        return false;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMenuListLayout = findViewById(R.id.mx_menu);
        mMenuWidgetAndEffectLayout = findViewById(R.id.menu_widget_effect);
        mMenuWidgetListLayout = findViewById(R.id.menu_widget_list);
    }

    public void setup(MenuController menuController) {
        this.mMenuController = menuController;
        mMenuController.setLayoutAnimation(mMenuListLayout);
        mMenuController.setLayoutAnimation(mMenuWidgetAndEffectLayout);
        mMenuController.setLayoutAnimation(mMenuWidgetListLayout);
    }

    public void setWidgets(ArrayList<WidgetListRowEntry> allWidgets) {
        if (mMenuController != null) {
            mMenuController.setWidgets(allWidgets);
        }
    }

    public void setPadding(int left, int top, int right, int bottom) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view instanceof HorizontalPageScrollView) {
                view.setPadding(left, top, right, bottom);
            }
        }
    }

    public void showMenuList() {
        XLog.e(XLog.getTag(), XLog.TAG_GU);
        if (mMenuController != null) {
            mMenuController.showView();
        }
    }

    // 恢复初始设置
    public void resetMenuLayout() {
        mMenuListLayout.removeAllViews();
        mMenuListLayout.setVisibility(VISIBLE);
        mMenuListLayout.resetPage();
        mMenuController.clearLayoutAnimation(mMenuListLayout);
        mMenuController.loadMenuList();
        mMenuWidgetAndEffectLayout.removeAllViews();
        mMenuWidgetAndEffectLayout.setVisibility(GONE);
        mMenuWidgetAndEffectLayout.resetPage();
        mMenuWidgetListLayout.removeAllViews();
        mMenuWidgetListLayout.setVisibility(GONE);
        mMenuWidgetListLayout.resetPage();
    }

    public void setState(State state) {
        mState = state;
    }

    public State getState() {
        return mState;
    }

    public boolean isMenuInNoneState() {
        return mState == State.NONE || mState == State.MENU;
    }

    public boolean onBackPressed() {
        if (mState == State.EFFECT || mState == State.WIDGET) {
            mMenuController.setLayoutAnimation(mMenuListLayout);
            mMenuController.showView();
            return true;
        } else if (mState == State.WIDGET_LIST) {
            mMenuController.showView();
            return true;
        }
        return false;
    }

    // 从隐藏到显示需要上移的距离
    public float getMenuLayoutShowTranslationY() {
        DeviceProfile profile = LauncherAppState.getInstance(getContext()).getInvariantDeviceProfile().portraitProfile;
        return profile.menBarBottomMarginPx + profile.hotseatBarBottomMarginPx;
    }

    public HorizontalPageScrollView getMenuListLayout() {
        return mMenuListLayout;
    }

    public HorizontalPageScrollView getMenuWidgetAndEffectLayout() {
        return mMenuWidgetAndEffectLayout;
    }

    public HorizontalPageScrollView getMenuWidgetListLayout() {
        return mMenuWidgetListLayout;
    }

    public MenuController getMenuController() {
        return mMenuController;
    }

    protected boolean mIsOpen;
    private final Rect mStartRect = new Rect();
    private final Rect mEndRect = new Rect();
    private final float mOutlineRadius;
    protected Animator mOpenCloseAnimator;

    public void animateOpen() {
        setVisibility(View.VISIBLE);
        final AnimatorSet openAnim = LauncherAnimUtils.createAnimatorSet();
        final Resources res = getResources();
        final long revealDuration = (long) res.getInteger(R.integer.config_popupOpenCloseDuration);
        final TimeInterpolator revealInterpolator = new AccelerateDecelerateInterpolator();

        // Rectangular reveal.
        final ValueAnimator revealAnim = createOpenCloseOutlineProvider()
                .createRevealAnimator(this, false);
        revealAnim.setDuration(revealDuration);
        revealAnim.setInterpolator(revealInterpolator);

        Animator fadeIn = ObjectAnimator.ofFloat(this, ALPHA, 0, 1);
        fadeIn.setDuration(revealDuration);
        fadeIn.setInterpolator(revealInterpolator);
        openAnim.play(fadeIn);
        openAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                announceAccessibilityChanges();
                mOpenCloseAnimator = null;
            }
        });

        mOpenCloseAnimator = openAnim;
        openAnim.playSequentially(revealAnim);
        openAnim.start();
    }

    public void animateClose() {
        if (!mIsOpen) {
            return;
        }
    }

    private RoundedRectRevealOutlineProvider createOpenCloseOutlineProvider() {
        int arrowCenterX = getResources().getDisplayMetrics().widthPixels / 2;
        int arrowCenterY = getMeasuredHeight() / 2;

        mStartRect.set(arrowCenterX, arrowCenterY, arrowCenterX, arrowCenterY);
        if (mEndRect.isEmpty()) {
            mEndRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }

        return new RoundedRectRevealOutlineProvider
                (mOutlineRadius, mOutlineRadius, mStartRect, mEndRect);
    }

    @Override
    public void setInsets(Rect insets) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        DeviceProfile grid = mLauncher.getDeviceProfile();
        if (grid.isVerticalBarLayout()) {
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (grid.isSeascape()) {
                lp.gravity = Gravity.LEFT;
                lp.width = grid.hotseatBarSizePx + insets.left + grid.hotseatBarSidePaddingPx;
            } else {
                lp.gravity = Gravity.RIGHT;
                lp.width = grid.hotseatBarSizePx + insets.right + grid.hotseatBarSidePaddingPx;
            }
        } else {
            lp.gravity = Gravity.BOTTOM;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = grid.hotseatBarSizePx + insets.bottom;
            lp.bottomMargin = grid.hotseatBarBottomMarginPx;

            XLog.d(XLog.getTag(), XLog.TAG_GU_STATE + lp.height);
        }
        Rect padding = grid.getHotseatLayoutPadding();
        setPadding(padding.left, padding.top, padding.right, padding.bottom);

        setLayoutParams(lp);
        InsettableFrameLayout.dispatchInsets(this, insets);
    }

}
