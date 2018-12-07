package com.android.launcher3.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAppState;

public class MenuView extends ViewGroup {

    public MenuView(Context context) {
        super(context);
    }

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


    // TODO 后期放到底部菜单中
    // 从隐藏到显示需要上移的距离
    public float getMenuLayoutShowTranslationY() {
        DeviceProfile profile = LauncherAppState.getInstance(getContext()).getInvariantDeviceProfile().portraitProfile;
        return profile.menBarBottomMarginPx + profile.hotseatBarBottomMarginPx;
    }
}
