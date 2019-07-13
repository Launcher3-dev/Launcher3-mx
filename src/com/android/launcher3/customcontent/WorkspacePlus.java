package com.android.launcher3.customcontent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.android.launcher3.CellLayout;
import com.android.launcher3.FullscreenKeyEventListener;
import com.android.launcher3.Insettable;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Workspace;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragView;

import java.util.ArrayList;

// add by codemx.cn ---- 20190712 ---plus- start
// modify by codemx.cn ---- 20190712 ---plus- start

public class WorkspacePlus extends Workspace {

    private final static long CUSTOM_CONTENT_SCREEN_ID = -301;

    CustomContentCallbacks mCustomContentCallbacks;
    private String mCustomContentDescription = "";

    public WorkspacePlus(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WorkspacePlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public CellLayout getCustomContent() {
        return getScreenWithId(CUSTOM_CONTENT_SCREEN_ID);
    }

    public boolean hasCustomContent() {
        return (mScreenOrder.size() > 0 && mScreenOrder.get(0) == CUSTOM_CONTENT_SCREEN_ID);
    }

    public void createCustomContentContainer() {
        CellLayout customScreen = (CellLayout)
                mLauncher.getLayoutInflater().inflate(R.layout.workspace_screen, this, false);
        customScreen.disableDragTarget();
        customScreen.disableJailContent();

        mWorkspaceScreens.put(CUSTOM_CONTENT_SCREEN_ID, customScreen);
        mScreenOrder.add(0, CUSTOM_CONTENT_SCREEN_ID);

        // We want no padding on the custom content
        customScreen.setPadding(0, 0, 0, 0);

        addFullScreenPage(customScreen);
        // Update the custom content hint
        setCurrentPage(getCurrentPage() + 1);
    }

    public void removeCustomContentPage() {
        CellLayout customScreen = getScreenWithId(CUSTOM_CONTENT_SCREEN_ID);
        if (customScreen == null) {
            throw new RuntimeException("Expected custom content screen to exist");
        }

        mWorkspaceScreens.remove(CUSTOM_CONTENT_SCREEN_ID);
        mScreenOrder.remove(CUSTOM_CONTENT_SCREEN_ID);
        removeView(customScreen);

        if (mCustomContentCallbacks != null) {
            mCustomContentCallbacks.onScrollProgressChanged(0);
            mCustomContentCallbacks.onHide();
        }

        mCustomContentCallbacks = null;

        // Update the custom content hint
        setCurrentPage(getCurrentPage() - 1);
    }

    /**
     * 填充内容视图到负一屏
     *
     * @param customContent 内容视图，用来处理负一屏内容加载等
     * @param callbacks     负一屏显示，隐藏，滑动等回调
     * @param description   负一屏描述
     */
    public void addToCustomContentPage(View customContent, CustomContentCallbacks callbacks,
                                       String description) {
        if (getPageIndexForScreenId(CUSTOM_CONTENT_SCREEN_ID) < 0) {
            throw new RuntimeException("Expected custom content screen to exist");
        }

        // Add the custom content to the full screen custom effect_page
        CellLayout customScreen = getScreenWithId(CUSTOM_CONTENT_SCREEN_ID);
        int spanX = customScreen.getCountX();
        int spanY = customScreen.getCountY();
        CellLayout.LayoutParams lp = new CellLayout.LayoutParams(0, 0, spanX, spanY);
        lp.canReorder = false;
        lp.isFullscreen = true;
        if (customContent instanceof Insettable) {
            ((Insettable) customContent).setInsets(mInsets);
        }

        // Verify that the child is removed from any existing parent.
        if (customContent.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) customContent.getParent();
            parent.removeView(customContent);
        }
        customScreen.removeAllViews();
        customContent.setFocusable(true);
        customContent.setOnKeyListener(new FullscreenKeyEventListener());
        customContent.setOnFocusChangeListener(mLauncher.mFocusHandler
                .getHideIndicatorOnFocusListener());
        customScreen.addViewToCellLayout(customContent, 0, 0, lp, true);
        mCustomContentDescription = description;
        mCustomContentCallbacks = callbacks;
    }

    public CustomContentCallbacks getCustomContentCallbacks() {
        return mCustomContentCallbacks;
    }

    public void setup(DragController dragController) {
        super.setup(dragController);
    }

    public boolean createUserFolderIfNecessary(View newView, long container, CellLayout target,
                                               int[] targetCell, float distance, boolean external, DragView dragView) {
        return super.createUserFolderIfNecessary(newView, container, target, targetCell, distance, external, dragView);
    }

    public boolean addToExistingFolderIfNecessary(View newView, CellLayout target, int[] targetCell,
                                                  float distance, DragObject d, boolean external) {
        return super.addToExistingFolderIfNecessary(newView, target, targetCell, distance, d, external);
    }

    public void moveToDefaultScreen() {
        super.moveToDefaultScreen();
    }

    public CellLayout getParentCellLayoutForView(View v) {
        return super.getParentCellLayoutForView(v);
    }

    public void clearDropTargets() {
        super.clearDropTargets();
    }

    public void updateShortcuts(ArrayList<ShortcutInfo> shortcuts) {
        super.updateShortcuts(shortcuts);
    }

}