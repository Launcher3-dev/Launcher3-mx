package com.codemx.effectivecard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.android.mxlibrary.util.XLog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by yuchuan
 * DATE 2020/4/29
 * TIME 18:16
 */
public class LauncherOverlayView extends FrameLayout {
    public LauncherOverlayView(@NonNull Context context) {
        super(context);
    }

    public LauncherOverlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LauncherOverlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    private float mStartX;
    private float mTranslationX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getRawX();
                mTranslationX = getTranslationX() % getResources().getDisplayMetrics().widthPixels;
                XLog.d(XLog.getTag(), "ACTION_DOWN= " + event.getRawX());
                break;
            case MotionEvent.ACTION_MOVE:
                setTranslationX(mTranslationX + (int) (event.getRawX() - mStartX));
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
