package com.android.launcher3.customcontent;

import android.content.Context;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class CustomContent extends FrameLayout implements CustomContentCallbacks {

    public CustomContent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomContent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomContent(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void onShow(boolean fromResume) {

    }

    @Override
    public void onHide() {

    }

    @Override
    public void onScrollProgressChanged(float progress) {

    }

    // 滑到负一屏是否再允许滑动，true:允许滑动到主屏，false:不允许再滑动
    @Override
    public boolean isScrollingAllowed() {
        return true;
    }
}
