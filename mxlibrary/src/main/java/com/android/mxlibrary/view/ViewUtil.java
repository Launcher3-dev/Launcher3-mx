package com.android.mxlibrary.view;

import android.graphics.Rect;
import android.view.View;

public final class ViewUtil {

    /**
     * add by codemx
     * 判断是否滑动(上下滑动)到屏幕中
     * <p>
     * 在onScrollChanged方法中调用
     *
     * @param view 视图
     *
     * @return 是否显示在屏幕中
     */
    public static boolean isViewVerticalScrollToScreen(View view) {
        Rect rect = new Rect();
        view.getLocalVisibleRect(rect);
        int top = rect.top;
        int bottom = rect.bottom;
        return top >= 0 && bottom > 0 && top < bottom;
    }

    /**
     * add by codemx
     * 判断是否滑动(上下滑动)到屏幕中
     * <p>
     * 在onScrollChanged方法中调用
     *
     * @param view 视图
     *
     * @return 是否显示在屏幕中
     */
    public static boolean isViewHorizontalScrollToScreen(View view) {
        Rect rect = new Rect();
        view.getLocalVisibleRect(rect);
        int left = rect.left;
        int right = rect.right;
        return left >= 0 && right > 0 && left < right;
    }

}
