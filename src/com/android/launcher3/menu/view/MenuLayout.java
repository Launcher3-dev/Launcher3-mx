package com.android.launcher3.menu.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Insettable;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.menu.MenuController;
import com.android.launcher3.widget.WidgetListRowEntry;
import com.android.mxlibrary.util.XLog;

import java.util.ArrayList;

/**
 * Created by CodeMX
 * DATE 2018/1/16
 * TIME 16:43
 */

public class MenuLayout extends FrameLayout implements Insettable {


    private MenuController mMenuController;
    private HorizontalPageScrollView mMenuListLayout;
    private HorizontalPageScrollView mMenuWidgetAndEffectLayout;
    private HorizontalPageScrollView mMenuWidgetListLayout;
    private State mState = State.NONE;
    private Launcher mLauncher;

    public void setWidgets(ArrayList<WidgetListRowEntry> allWidgets) {
        if (mMenuController != null) {
            mMenuController.setWidgets(allWidgets);
        }
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
        mLauncher = Launcher.getLauncher(context);
        mMenuController = new MenuController(context, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        XLog.e(XLog.getTag(), XLog.TAG_GU);
        mMenuListLayout = findViewById(R.id.mx_menu);
        mMenuController.setLayoutAnimation(mMenuListLayout);
        mMenuWidgetAndEffectLayout = findViewById(R.id.menu_widget_effect);
        mMenuController.setLayoutAnimation(mMenuWidgetAndEffectLayout);
        mMenuWidgetListLayout = findViewById(R.id.menu_widget_list);
        mMenuController.setLayoutAnimation(mMenuWidgetListLayout);
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

    public void onBackPressed() {
        if (mState == State.EFFECT || mState == State.WIDGET) {
            mMenuController.setLayoutAnimation(mMenuListLayout);
            mMenuController.showView();
        } else if (mState == State.WIDGET_LIST) {
            mMenuController.showView();
        }
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
            lp.bottomMargin = grid.hotseatBarBottomMarginPx + lp.height;
        }
        setLayoutParams(lp);
        InsettableFrameLayout.dispatchInsets(this, insets);

    }
}
