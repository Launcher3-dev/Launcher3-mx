package com.android.launcher3.menu.controller;

import android.content.Context;
import android.graphics.Point;
import android.view.View;

import com.android.launcher3.Launcher;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.menu.anim.MenuStateTransitionAnimation;
import com.android.launcher3.menu.imp.IMenuAdapter;
import com.android.launcher3.menu.view.HorizontalPageScrollView;
import com.android.launcher3.menu.view.MenuLayout;
import com.android.launcher3.util.LabelComparator;
import com.android.launcher3.widget.PendingItemDragHelper;
import com.android.launcher3.widget.WidgetCell;
import com.android.launcher3.widget.WidgetImageView;
import com.android.launcher3.widget.WidgetListRowEntry;
import com.android.mxlibrary.util.XLog;

import java.util.Comparator;

/**
 * Created by CodeMX
 * DATE 2018/4/4
 * TIME 15:45
 */

public abstract class SupperMenuController<T> {

    protected Launcher mLauncher;
    IMenuAdapter<T> mMenuAdapter;
    MenuLayout mMenuLayout;
    HorizontalPageScrollView mScrollView;
    private MenuStateTransitionAnimation mMenuTransition;
    MenuController mController;

    public SupperMenuController(Context context, MenuLayout menuLayout) {
        this.mLauncher = Launcher.getLauncher(context);
        this.mMenuLayout = menuLayout;
    }

    boolean beginDragging(View v, View widgetIcon) {
        if (v instanceof WidgetCell) {
            if (!beginDraggingWidget((WidgetCell) v, widgetIcon)) {
                return false;
            }
        } else {
            XLog.e(XLog.getTag(), XLog.TAG_GU + "Unexpected dragging view: " + v);
        }

        // We don't enter spring-loaded mode if the drag has been cancelled
        if (mLauncher.getDragController().isDragging()) {
            // Go into spring loaded mode (must happen before we startDrag())
            mLauncher.enterSpringLoadedDragMode();
        }
        return true;
    }

    protected boolean beginDraggingWidget(WidgetCell v, View widgetIcon) {
        // Get the widget preview as the drag representation
        WidgetImageView image = v.getWidgetImage();

        // If the ImageView doesn't have a drawable yet, the widget preview hasn't been loaded and
        // we abort the drag.
        if (image.getBitmap() == null) {
            XLog.e(XLog.getTag(), XLog.TAG_GU + "widget preview image is null ");
            return false;
        }

        int[] loc = new int[2];
        mLauncher.getDragLayer().getLocationInDragLayer(widgetIcon, loc);

        new PendingItemDragHelper(v).startDrag(
                image.getBitmapBounds(), image.getBitmap().getWidth(), image.getWidth(),
                new Point(loc[0], loc[1]), mScrollView, new DragOptions());
        return true;
    }

    /**
     * Comparator for sorting WidgetListRowEntry based on package title
     */
    public static class WidgetListRowEntryComparator implements Comparator<WidgetListRowEntry> {

        private final LabelComparator mComparator = new LabelComparator();

        @Override
        public int compare(WidgetListRowEntry a, WidgetListRowEntry b) {
            return mComparator.compare(a.pkgItem.title.toString(), b.pkgItem.title.toString());
        }
    }

    protected void show(MenuLayout.State fromState, MenuLayout.State toState, boolean animated) {
        if (mMenuTransition == null) {
            mMenuTransition = mLauncher.getMenuLayout().getMenuController().getMenuTransition();
        }
        if (mMenuTransition.isMenuAnimationRunning()) {
            return;
        }
        mMenuTransition.startAnimationToNewMenuLayoutState(fromState, toState, animated);
        loadMenuList();
    }

    public abstract void loadMenuList();

    public abstract void loadContainer();

    public abstract void loadAdapter();

    public abstract void showView();

    public abstract void onClick(View v);

    public abstract void onLongClick(View v);


}
