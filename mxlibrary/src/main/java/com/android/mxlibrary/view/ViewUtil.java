package com.android.mxlibrary.view;

import android.graphics.Rect;
import android.view.View;

public final class ViewUtil {

    /**
     * add by codemx
     * 判断是否滑动到屏幕中
     *
     * 在onScrollChanged方法中调用
     *
     * @param view 视图
     * @return 是否显示在屏幕中
     */
    public static boolean isViewScrollToScreen(View view) {
        Rect rect = new Rect();
        view.getLocalVisibleRect(rect);
        int top = rect.top;
        int bottom = rect.bottom;
        return top >= 0 && bottom > 0 && top < bottom;
    }

}
