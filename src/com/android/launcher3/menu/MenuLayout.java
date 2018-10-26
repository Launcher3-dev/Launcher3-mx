package com.android.launcher3.menu;

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
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.userevent.nano.LauncherLogProto;

/**
 * 底部菜单
 */
public class MenuLayout extends FrameLayout implements UserEventDispatcher.LogContainerProvider, Insettable {

    private Launcher mLauncher;

    public MenuLayout(@NonNull Context context) {
        this(context, null);
    }

    public MenuLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLauncher = Launcher.getLauncher(context);
    }

    // 从隐藏到显示需要上移的距离
    public float getMenuLayoutShowTranslationY() {
        DeviceProfile profile = LauncherAppState.getInstance(getContext()).getInvariantDeviceProfile().portraitProfile;
        return profile.menBarBottomMarginPx + profile.hotseatBarBottomMarginPx;
    }

    @Override
    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {

    }

    @Override
    public void setInsets(Rect insets) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        DeviceProfile grid = mLauncher.getDeviceProfile();
        lp.gravity = Gravity.BOTTOM;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = grid.hotseatBarSizePx + insets.bottom;
        lp.bottomMargin = -grid.menBarBottomMarginPx;
        setPadding(grid.hotseatBarSizePx + grid.workspacePadding.left,
                grid.hotseatBarTopPaddingPx, grid.hotseatBarSizePx + grid.workspacePadding.right,
                grid.hotseatBarBottomPaddingPx + insets.bottom);
        setLayoutParams(lp);
        InsettableFrameLayout.dispatchInsets(this, insets);
    }
}
