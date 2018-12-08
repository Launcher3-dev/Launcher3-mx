/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.launcher3;

import android.graphics.Rect;
import android.view.animation.Interpolator;

import com.android.launcher3.allapps.MenuTransitionController;
import com.android.launcher3.states.EditingState;
import com.android.launcher3.states.MenuState;
import com.android.launcher3.states.OverViewClipState;
import com.android.launcher3.states.OverViewCreateFolderState;
import com.android.launcher3.states.OverViewMultipleUninstallState;
import com.android.launcher3.states.OverViewSpringLoadedState;
import com.android.launcher3.states.SpringLoadedState;
import com.android.launcher3.uioverrides.OverviewState;
import com.android.launcher3.uioverrides.UiFactory;
import com.android.launcher3.userevent.nano.LauncherLogProto.ContainerType;

import java.util.Arrays;

import static android.view.View.IMPORTANT_FOR_ACCESSIBILITY_AUTO;
import static android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS;
import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
import static com.android.launcher3.anim.Interpolators.ACCEL_2;
import static com.android.launcher3.states.RotationHelper.REQUEST_NONE;


/**
 * Base state for various states used for the Launcher
 */
public class LauncherState {

    /**
     * Set of elements indicating various workspace elements which change visibility across states
     * Note that workspace is not included here as in that case, we animate individual pages
     */
    public static final int NONE = 0;
    public static final int HOTSEAT_ICONS = 1 << 0;// Hotseat图标
    public static final int HOTSEAT_SEARCH_BOX = 1 << 1;
    public static final int ALL_APPS_HEADER = 1 << 2;
    public static final int ALL_APPS_HEADER_EXTRA = 1 << 3; // e.g. app predictions
    public static final int ALL_APPS_CONTENT = 1 << 4;
    public static final int VERTICAL_SWIPE_INDICATOR = 1 << 5;// 抽屉指示器

    protected static final int FLAG_MULTI_PAGE = 1 << 0;
    protected static final int FLAG_DISABLE_ACCESSIBILITY = 1 << 1;
    protected static final int FLAG_DISABLE_RESTORE = 1 << 2;
    protected static final int FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED = 1 << 3;
    protected static final int FLAG_DISABLE_PAGE_CLIPPING = 1 << 4;
    protected static final int FLAG_PAGE_BACKGROUNDS = 1 << 5;
    protected static final int FLAG_DISABLE_INTERACTION = 1 << 6;
    protected static final int FLAG_OVERVIEW_UI = 1 << 7;
    protected static final int FLAG_HIDE_BACK_BUTTON = 1 << 8;
    protected static final int FLAG_HAS_SYS_UI_SCRIM = 1 << 9;

    protected static final PageAlphaProvider DEFAULT_ALPHA_PROVIDER =
            new PageAlphaProvider(ACCEL_2) {
                @Override
                public float getPageAlpha(int pageIndex) {
                    return 1;
                }
            };

    protected static final PageScaleProvider DEFAULT_SCALE_PROVIDER =
            new PageScaleProvider(ACCEL_2) {
                @Override
                public float getPageScale(int pageIndex) {
                    return 0.85f;
                }
            };

    private static final LauncherState[] sAllStates = new LauncherState[9];

    /**
     * TODO: Create a separate class for NORMAL state.
     */
    public static final LauncherState NORMAL = new LauncherState(0, ContainerType.WORKSPACE, 0,
            FLAG_DISABLE_RESTORE | FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED | FLAG_HIDE_BACK_BUTTON |
                    FLAG_HAS_SYS_UI_SCRIM);

    /**
     * Various Launcher states arranged in the increasing order of UI layers
     */
    public static final LauncherState SPRING_LOADED = new SpringLoadedState(1);
    public static final LauncherState OVERVIEW = new OverviewState(2);
    public static final LauncherState OVERVIEW_SPRING_LOADED = new OverViewSpringLoadedState(3);
    public static final LauncherState OVERVIEW_CREATE_FOLDER = new OverViewCreateFolderState(4);
    public static final LauncherState OVERVIEW_MULTIPLE_UNINSTALL = new OverViewMultipleUninstallState(5);
    public static final LauncherState OVERVIEW_CLIP = new OverViewClipState(6);
    public static final LauncherState MENU = new MenuState(8);

    // TODO 废除该状态
    public static final LauncherState EDITING = new EditingState(7);

    protected static final Rect sTempRect = new Rect();

    public final int ordinal;

    /**
     * Used for containerType in {@link com.android.launcher3.logging.UserEventDispatcher}
     */
    public final int containerType;

    /**
     * True if the state can be persisted across activity restarts.
     */
    public final boolean disableRestore;

    /**
     * True if workspace has multiple pages visible.
     */
    public final boolean hasMultipleVisiblePages;

    /**
     * Accessibility flag for workspace and its pages.
     *
     * @see android.view.View#setImportantForAccessibility(int)
     */
    public final int workspaceAccessibilityFlag;

    /**
     * Properties related to state transition animation
     *
     * @see WorkspaceStateTransitionAnimation
     */
    public final boolean hasWorkspacePageBackground;

    public final int transitionDuration;

    /**
     * True if the state allows workspace icons to be dragged.
     */
    public final boolean workspaceIconsCanBeDragged;

    /**
     * True if the workspace pages should not be clipped relative to the workspace bounds
     * for this state.
     */
    public final boolean disablePageClipping;

    /**
     * True if launcher can not be directly interacted in this state;
     */
    public final boolean disableInteraction;

    /**
     * True if the state has overview panel visible.
     * 如果是在预览界面则为true
     */
    public final boolean overviewUi;

    /**
     * True if the back button should be hidden when in this state (assuming no floating views are
     * open, launcher has window focus, etc).
     */
    public final boolean hideBackButton;

    public final boolean hasSysUiScrim;

    /**
     * Launcher 状态构造函数
     *
     * @param id                 状态id
     * @param containerType      状态类型
     * @param transitionDuration 进入当前状态动画时间
     * @param flags              状态flag
     */
    public LauncherState(int id, int containerType, int transitionDuration, int flags) {
        this.containerType = containerType;
        this.transitionDuration = transitionDuration;

        this.hasWorkspacePageBackground = (flags & FLAG_PAGE_BACKGROUNDS) != 0;
        this.hasMultipleVisiblePages = (flags & FLAG_MULTI_PAGE) != 0;
        this.workspaceAccessibilityFlag = (flags & FLAG_DISABLE_ACCESSIBILITY) != 0
                ? IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                : IMPORTANT_FOR_ACCESSIBILITY_AUTO;
        this.disableRestore = (flags & FLAG_DISABLE_RESTORE) != 0;
        this.workspaceIconsCanBeDragged = (flags & FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED) != 0;
        this.disablePageClipping = (flags & FLAG_DISABLE_PAGE_CLIPPING) != 0;
        this.disableInteraction = (flags & FLAG_DISABLE_INTERACTION) != 0;
        this.overviewUi = (flags & FLAG_OVERVIEW_UI) != 0;
        this.hideBackButton = (flags & FLAG_HIDE_BACK_BUTTON) != 0;
        this.hasSysUiScrim = (flags & FLAG_HAS_SYS_UI_SCRIM) != 0;

        this.ordinal = id;
        sAllStates[id] = this;
    }

    public static LauncherState[] values() {
        return Arrays.copyOf(sAllStates, sAllStates.length);
    }

    public float[] getWorkspaceScaleAndTranslation(Launcher launcher) {
        return new float[]{1, 0, 0};
    }

    /**
     * Returns 2 floats designating how to transition overview:
     * scale for the current and adjacent pages
     * translationY factor where 0 is top aligned and 0.5 is centered vertically
     */
    public float[] getOverviewScaleAndTranslationYFactor(Launcher launcher) {
        return new float[]{1.1f, 0f};
    }

    public void onStateEnabled(Launcher launcher) {
        dispatchWindowStateChanged(launcher);
    }

    public void onStateDisabled(Launcher launcher) {
    }

    public int getVisibleElements(Launcher launcher) {
        if (launcher.getDeviceProfile().isVerticalBarLayout()) {// 横屏模式，Hotseat竖直放置
            return HOTSEAT_ICONS | VERTICAL_SWIPE_INDICATOR;
        }
        return HOTSEAT_ICONS | HOTSEAT_SEARCH_BOX | VERTICAL_SWIPE_INDICATOR;
    }

    /**
     * Fraction shift in the vertical translation UI and related properties
     *
     * @see MenuTransitionController
     */
    public float getVerticalProgress(Launcher launcher) {
        return 1f;
    }

    public float getWorkspaceScrimAlpha(Launcher launcher) {
        return 0;
    }

    public String getDescription(Launcher launcher) {
        return launcher.getWorkspace().getCurrentPageDescription();
    }

    public PageAlphaProvider getWorkspacePageAlphaProvider(Launcher launcher) {
        if (this != NORMAL || !launcher.getDeviceProfile().shouldFadeAdjacentWorkspaceScreens()) {
            return DEFAULT_ALPHA_PROVIDER;
        }
        final int centerPage = launcher.getWorkspace().getNextPage();
        return new PageAlphaProvider(ACCEL_2) {
            @Override
            public float getPageAlpha(int pageIndex) {
                return pageIndex != centerPage ? 0 : 1f;
            }
        };
    }

    public PageScaleProvider getWorkspacePageScaleProvider(Launcher launcher) {
        // 正常模式或者竖屏显示
        if (this != NORMAL || !launcher.getDeviceProfile().shouldFadeAdjacentWorkspaceScreens()) {
            return DEFAULT_SCALE_PROVIDER;
        }
        final int centerPage = launcher.getWorkspace().getNextPage();
        return new PageScaleProvider(ACCEL_2) {
            @Override
            public float getPageScale(int pageIndex) {
                return pageIndex != centerPage ? 0 : 1f;
            }
        };
    }

    public LauncherState getHistoryForState(LauncherState previousState) {
        // No history is supported
        return NORMAL;
    }

    /**
     * Called when the start transition ends and the user settles on this particular state.
     */
    public void onStateTransitionEnd(Launcher launcher) {
        if (this == NORMAL) {
            UiFactory.resetOverview(launcher);
            // Clear any rotation locks when going to normal state
            launcher.getRotationHelper().setCurrentStateRequest(REQUEST_NONE);
        }
    }

    protected static void dispatchWindowStateChanged(Launcher launcher) {
        launcher.getWindow().getDecorView().sendAccessibilityEvent(TYPE_WINDOW_STATE_CHANGED);
    }

    public static abstract class PageAlphaProvider {

        public final Interpolator interpolator;

        public PageAlphaProvider(Interpolator interpolator) {
            this.interpolator = interpolator;
        }

        public abstract float getPageAlpha(int pageIndex);
    }

    public static abstract class PageScaleProvider {

        public final Interpolator interpolator;

        public PageScaleProvider(Interpolator interpolator) {
            this.interpolator = interpolator;
        }

        public abstract float getPageScale(int pageIndex);
    }
}
