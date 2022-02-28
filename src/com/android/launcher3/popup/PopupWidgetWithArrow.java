/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.launcher3.popup;

import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.launcher3.*;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.accessibility.ShortcutMenuAccessibilityDelegate;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.shortcuts.ShortcutDragPreviewProvider;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import com.android.mxlibrary.util.XLog;

import java.util.ArrayList;
import java.util.List;

import static com.android.launcher3.notification.NotificationMainView.NOTIFICATION_ITEM_INFO;
import static com.android.launcher3.userevent.nano.LauncherLogProto.*;

/**
 * A container for shortcuts to deep links and notifications associated with an app.
 * <p>
 * 长按图标弹出的扩展框
 */
@TargetApi(Build.VERSION_CODES.N)
public class PopupWidgetWithArrow extends ArrowPopup implements DragSource,
        DragController.DragListener, View.OnLongClickListener,
        View.OnTouchListener {

    private final List<DeepShortcutView> mShortcuts = new ArrayList<>();
    private final PointF mInterceptTouchDown = new PointF();
    private final Point mIconLastTouchPos = new Point();

    private final int mStartDragThreshold;
    private final LauncherAccessibilityDelegate mAccessibilityDelegate;

    private LauncherAppWidgetHostView mOriginalWidgetHostView;

    private ViewGroup mSystemShortcutContainer;

    public PopupWidgetWithArrow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mStartDragThreshold = getResources().getDimensionPixelSize(
                R.dimen.deep_shortcuts_start_drag_threshold);
        mAccessibilityDelegate = new ShortcutMenuAccessibilityDelegate(mLauncher);
    }

    public PopupWidgetWithArrow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupWidgetWithArrow(Context context) {
        this(context, null, 0);
    }

    public LauncherAccessibilityDelegate getAccessibilityDelegate() {
        return mAccessibilityDelegate;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mInterceptTouchDown.set(ev.getX(), ev.getY());
        }
        // Stop sending touch events to deep shortcut views if user moved beyond touch slop.
        return Math.hypot(mInterceptTouchDown.x - ev.getX(), mInterceptTouchDown.y - ev.getY())
                > ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    @Override
    protected boolean isOfType(int type) {
        return (type & TYPE_ACTION_POPUP) != 0;
    }

    @Override
    public void logActionCommand(int command) {
        mLauncher.getUserEventDispatcher().logActionCommand(
                command, mOriginalWidgetHostView, ContainerType.DEEPSHORTCUTS);
    }

    @Override
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            DragLayer dl = mLauncher.getDragLayer();
            if (!dl.isEventOverView(this, ev)) {
                mLauncher.getUserEventDispatcher().logActionTapOutside(
                        LoggerUtils.newContainerTarget(ContainerType.DEEPSHORTCUTS));
                close(true);

                // We let touches on the original icon go through so that users can launch
                // the app with one tap if they don't find a shortcut they want.
                return mOriginalWidgetHostView == null || !dl.isEventOverView(mOriginalWidgetHostView, ev);
            }
        }
        return false;
    }

    /**
     * Shows the notifications and deep shortcuts associated with {@param icon}.
     *
     * @return the container if shown or null.
     */
    public static PopupWidgetWithArrow showForIcon(LauncherAppWidgetHostView widgetHostView) {
        XLog.i(XLog.getTag(), XLog.TAG_GU);
        Launcher launcher = Launcher.getLauncher(widgetHostView.getContext());
        if (getOpen(launcher) != null) {
            // There is already an items container open, so don't open this one.
            widgetHostView.clearFocus();
            return null;
        }

        ItemInfo itemInfo = (ItemInfo) widgetHostView.getTag();

        PopupDataProvider popupDataProvider = launcher.getPopupDataProvider();

        XLog.i(XLog.getTag(), XLog.TAG_GU);
        List<String> shortcutIds = new ArrayList<>();
        shortcutIds.add(launcher.getResources().getString(R.string.remove_drop_target_label));
        List<SystemShortcut> systemShortcuts = popupDataProvider
                .getEnabledSystemWidget(itemInfo);
        final PopupWidgetWithArrow container =
                (PopupWidgetWithArrow) launcher.getLayoutInflater().inflate(
                        R.layout.popup_widget, launcher.getDragLayer(), false);
        container.populateAndShow(widgetHostView, shortcutIds, systemShortcuts);
        return container;
    }

    @Override
    protected void onInflationComplete(boolean isReversed) {
        // Update dividers
        int count = getChildCount();
        DeepShortcutView lastView = null;
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() == VISIBLE && view instanceof DeepShortcutView) {
                if (lastView != null) {
                    lastView.setDividerVisibility(VISIBLE);
                }
                lastView = (DeepShortcutView) view;
                lastView.setDividerVisibility(INVISIBLE);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    private void populateAndShow(final LauncherAppWidgetHostView originalIcon,
                                 final List<String> shortcutIds,
                                 List<SystemShortcut> systemShortcuts) {
        XLog.i(XLog.getTag(), XLog.TAG_GU);
        mOriginalWidgetHostView = originalIcon;

        int viewsToFlip = getChildCount();
        mSystemShortcutContainer = this;

        if (!shortcutIds.isEmpty()) {
//            for (int i = shortcutIds.size(); i > 0; i--) {
//                DeepShortcutView view = inflateAndAdd(R.layout.deep_shortcut, this);
//                DeepShortcutTextView textView = view.findViewById(R.id.bubble_text);
//                textView.setText(shortcutIds.get(i - 1));
//                View icon = view.findViewById(R.id.icon);
//                icon.setVisibility(GONE);
//                icon.setBackground(mLauncher.getResources().getDrawable(R.drawable.ic_uninstall_no_shadow, null));
////                mShortcuts.add(view);
//            }

            if (!systemShortcuts.isEmpty()) {
                mSystemShortcutContainer = inflateAndAdd(R.layout.system_shortcut_icons, this);
                int itemHeight = getResources().getDimensionPixelSize(R.dimen.bg_popup_item_height);
                mSystemShortcutContainer.getLayoutParams().height = itemHeight;
                for (SystemShortcut shortcut : systemShortcuts) {
                    initializeSystemShortcut(
                            R.layout.system_shortcut, mSystemShortcutContainer, shortcut);
                }
            }

            updateHiddenShortcuts();
        }

        reorderAndShow(viewsToFlip);

        mLauncher.getDragController().addDragListener(this);

        // All views are added. Animate layout from now on.
        setLayoutTransition(new LayoutTransition());
    }

    @Override
    protected Pair<View, String> getAccessibilityTarget() {
        return Pair.create(this, "");
    }

    @Override
    protected void getTargetObjectLocation(Rect outPos) {
        mLauncher.getDragLayer().getDescendantRectRelativeToSelf(mOriginalWidgetHostView.getChildAt(0), outPos);
        outPos.top += mOriginalWidgetHostView.getPaddingTop() - 20;
        outPos.left += mOriginalWidgetHostView.getPaddingLeft();
        outPos.right -= mOriginalWidgetHostView.getPaddingRight();
        outPos.bottom = outPos.top + mOriginalWidgetHostView.getHeight();
    }

    private void updateHiddenShortcuts() {
        int itemHeight = getResources().getDimensionPixelSize(R.dimen.bg_popup_item_height);
        int total = mShortcuts.size();
        for (DeepShortcutView view : mShortcuts) {
            view.setVisibility(VISIBLE);
            view.getLayoutParams().height = itemHeight;
        }
    }

    private void updateDividers() {
        int count = getChildCount();
        DeepShortcutView lastView = null;
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() == VISIBLE && view instanceof DeepShortcutView) {
                if (lastView != null) {
                    lastView.setDividerVisibility(VISIBLE);
                }
                lastView = (DeepShortcutView) view;
                lastView.setDividerVisibility(INVISIBLE);
            }
        }
    }

    @Override
    protected void onWidgetsBound() {
        ItemInfo itemInfo = (ItemInfo) mOriginalWidgetHostView.getTag();
        SystemShortcut widgetInfo = new SystemShortcut.Widgets();
        OnClickListener onClickListener = widgetInfo.getOnClickListener(mLauncher, itemInfo, mOriginalWidgetHostView);
        View widgetsView = null;
        int count = mSystemShortcutContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            View systemShortcutView = mSystemShortcutContainer.getChildAt(i);
            if (systemShortcutView.getTag() instanceof SystemShortcut.Widgets) {
                widgetsView = systemShortcutView;
                break;
            }
        }

        if (onClickListener != null && widgetsView == null) {
            // We didn't have any widgets cached but now there are some, so enable the shortcut.
            if (mSystemShortcutContainer != this) {
                initializeSystemShortcut(
                        R.layout.system_shortcut_icon_only, mSystemShortcutContainer, widgetInfo);
            } else {
                // If using the expanded system shortcut (as opposed to just the icon), we need to
                // reopen the container to ensure measurements etc. all work out. While this could
                // be quite janky, in practice the user would typically see a small flicker as the
                // animation restarts partway through, and this is a very rare edge case anyway.
                close(false);
                PopupWidgetWithArrow.showForIcon(mOriginalWidgetHostView);
            }
        } else if (onClickListener == null && widgetsView != null) {
            // No widgets exist, but we previously added the shortcut so remove it.
            if (mSystemShortcutContainer != this) {
                mSystemShortcutContainer.removeView(widgetsView);
            } else {
                close(false);
                PopupWidgetWithArrow.showForIcon(mOriginalWidgetHostView);
            }
        }
    }

    private void initializeSystemShortcut(int resId, ViewGroup container, SystemShortcut info) {
        View view = inflateAndAdd(resId, container);
        if (view instanceof DeepShortcutView) {
            // Expanded system shortcut, with both icon and text shown on white background.
            final DeepShortcutView shortcutView = (DeepShortcutView) view;
            shortcutView.getIconView().setBackgroundResource(info.iconResId);
            shortcutView.getBubbleText().setText(info.labelResId);
        } else if (view instanceof ImageView) {
            // Only the system shortcut icon shows on a gray background header.
            final ImageView shortcutIcon = (ImageView) view;
            shortcutIcon.setImageResource(info.iconResId);
            shortcutIcon.setContentDescription(getContext().getText(info.labelResId));
        }
        view.setTag(info);
        view.setOnClickListener(info.getOnClickListener(mLauncher,
                (ItemInfo) mOriginalWidgetHostView.getTag(), mOriginalWidgetHostView));
    }

    /**
     * Determines when the deferred drag should be started.
     * <p>
     * Current behavior:
     * - Start the drag if the touch passes a certain distance from the original touch down.
     */
    public DragOptions.PreDragCondition createPreDragCondition() {
        return new DragOptions.PreDragCondition() {

            @Override
            public boolean shouldStartDrag(double distanceDragged) {
                return distanceDragged > mStartDragThreshold;
            }

            @Override
            public void onPreDragStart(DragObject dragObject) {
                if (mIsAboveIcon) {
                    // Hide only the icon, keep the text visible.
                    mOriginalWidgetHostView.setVisibility(VISIBLE);
                } else {
                    // Hide both the icon and text.
                    mOriginalWidgetHostView.setVisibility(INVISIBLE);
                }
            }

            @Override
            public void onPreDragEnd(DragObject dragObject, boolean dragStarted) {
                if (dragStarted) {
                    // Make sure we keep the original icon hidden while it is being dragged.
                    mOriginalWidgetHostView.setVisibility(INVISIBLE);
                } else {
                    mLauncher.getUserEventDispatcher().logDeepShortcutsOpen(mOriginalWidgetHostView);
                    if (!mIsAboveIcon) {
                        // Show the icon but keep the text hidden.
                        mOriginalWidgetHostView.setVisibility(VISIBLE);
                    }
                }
            }
        };
    }

    @Override
    public void onDropCompleted(View target, DragObject d, boolean success) {
    }

    @Override
    public void onDragStart(DragObject dragObject, DragOptions options) {
        // Either the original icon or one of the shortcuts was dragged.
        // Hide the container, but don't remove it yet because that interferes with touch events.
        mDeferContainerRemoval = true;
        animateClose();
    }

    @Override
    public void onDragEnd() {
        if (!mIsOpen) {
            if (mOpenCloseAnimator != null) {
                // Close animation is running.
                mDeferContainerRemoval = false;
            } else {
                // Close animation is not running.
                if (mDeferContainerRemoval) {
                    closeComplete();
                }
            }
        }
    }

    @Override
    public void fillInLogContainerData(View v, ItemInfo info, Target target, Target targetParent) {
        if (info == NOTIFICATION_ITEM_INFO) {
            target.itemType = ItemType.NOTIFICATION;
        } else {
            target.itemType = ItemType.DEEPSHORTCUT;
            target.rank = info.rank;
        }
        targetParent.containerType = ContainerType.DEEPSHORTCUTS;
    }

    @Override
    protected void onCreateCloseAnimation(AnimatorSet anim) {
        // Animate original icon's text back in.
//        anim.play(mOriginalIcon.createTextAlphaAnimator(true /* fadeIn */));
//        mOriginalIcon.forceHideBadge(false);
    }

    @Override
    protected void closeComplete() {
        super.closeComplete();
//        mOriginalIcon.setTextVisibility(mOriginalIcon.shouldTextBeVisible());
//        mOriginalIcon.forceHideBadge(false);
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        // Touched a shortcut, update where it was touched so we can drag from there on long click.
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mIconLastTouchPos.set((int) ev.getX(), (int) ev.getY());
                break;
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        if (!ItemLongClickListener.canStartDrag(mLauncher)) return false;
        // Return early if not the correct view
        if (!(v.getParent() instanceof DeepShortcutView)) return false;

        // Long clicked on a shortcut.
        DeepShortcutView sv = (DeepShortcutView) v.getParent();
        sv.setWillDrawIcon(false);

        // Move the icon to align with the center-top of the touch point
        Point iconShift = new Point();
        iconShift.x = mIconLastTouchPos.x - sv.getIconCenter().x;
        iconShift.y = mIconLastTouchPos.y - mLauncher.getDeviceProfile().iconSizePx;

        DragView dv = mLauncher.getWorkspace().beginDragShared(sv.getIconView(),
                this, sv.getFinalInfo(),
                new ShortcutDragPreviewProvider(sv.getIconView(), iconShift), new DragOptions());
        dv.animateShift(-iconShift.x, -iconShift.y);

        // TODO: support dragging from within folder without having to close it
        AbstractFloatingView.closeOpenContainer(mLauncher, AbstractFloatingView.TYPE_FOLDER);
        return false;
    }

    /**
     * Returns a PopupContainerWithArrow which is already open or null
     */
    public static PopupWidgetWithArrow getOpen(Launcher launcher) {
        return getOpenView(launcher, TYPE_ACTION_POPUP);
    }

}
