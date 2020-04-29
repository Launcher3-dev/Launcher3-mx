package com.codemx.floatwindow;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FloatWindow {

    private static final String mDefaultTag = "default_float_window_tag";
    private static Map<String, IFloatWindow> mFloatWindowMap;

    public static IFloatWindow get() {
        return get(mDefaultTag);
    }

    public static IFloatWindow get(@NonNull String tag) {
        return mFloatWindowMap == null ? null : mFloatWindowMap.get(tag);
    }

    @MainThread
    public static Builder with(@NonNull Context applicationContext) {
        return new Builder(applicationContext);
    }

    public static void destroy() {
        destroy(mDefaultTag);
    }

    public static void destroy(String tag) {
        if (mFloatWindowMap == null || !mFloatWindowMap.containsKey(tag)) {
            return;
        }
        mFloatWindowMap.get(tag).dismiss();
        mFloatWindowMap.remove(tag);
    }

    public static class Builder {
        Context mApplicationContext;
        View mView;
        int mLayoutId;
        int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        int gravity = Gravity.TOP | Gravity.START;
        int xOffset;
        int yOffset;
        boolean mShow = true;
        Class[] mActivities;
        int mMoveType = MoveType.slide;
        int mSlideLeftMargin;
        int mSlideRightMargin;
        long mDuration = 300;
        TimeInterpolator mInterpolator;
        String mTag = mDefaultTag;
        boolean mDesktopShow;
        PermissionListener mPermissionListener;
        ViewStateListener mViewStateListener;

        Builder(Context applicationContext) {
            mApplicationContext = applicationContext;
        }

        public Builder setView(@NonNull View view) {
            mView = view;
            return this;
        }

        public Builder setView(@LayoutRes int layoutId) {
            mLayoutId = layoutId;
            return this;
        }

        public Builder setWidth(int width) {
            mWidth = width;
            return this;
        }

        public Builder setHeight(int height) {
            mHeight = height;
            return this;
        }

        public Builder setWidth(@Screen.screenType int screenType, float ratio) {
            mWidth = (int) ((screenType == Screen.width ?
                    Util.getScreenWidth(mApplicationContext) :
                    Util.getScreenHeight(mApplicationContext)) * ratio);
            return this;
        }

        public Builder setHeight(@Screen.screenType int screenType, float ratio) {
            mHeight = (int) ((screenType == Screen.width ?
                    Util.getScreenWidth(mApplicationContext) :
                    Util.getScreenHeight(mApplicationContext)) * ratio);
            return this;
        }

        public Builder setX(int x) {
            xOffset = x;
            return this;
        }

        public Builder setY(int y) {
            yOffset = y;
            return this;
        }

        public Builder setX(@Screen.screenType int screenType, float ratio) {
            xOffset = (int) ((screenType == Screen.width ?
                    Util.getScreenWidth(mApplicationContext) :
                    Util.getScreenHeight(mApplicationContext)) * ratio);
            return this;
        }

        public Builder setY(@Screen.screenType int screenType, float ratio) {
            yOffset = (int) ((screenType == Screen.width ?
                    Util.getScreenWidth(mApplicationContext) :
                    Util.getScreenHeight(mApplicationContext)) * ratio);
            return this;
        }

        /**
         * 设置 Activity 过滤器，用于指定在哪些界面显示悬浮窗，默认全部界面都显示
         *
         * @param show       　过滤类型,子类类型也会生效
         * @param activities 　过滤界面
         */
        public Builder setFilter(boolean show, @NonNull Class... activities) {
            mShow = show;
            mActivities = activities;
            return this;
        }

        public Builder setMoveType(@MoveType.MOVE_TYPE int moveType) {
            return setMoveType(moveType, 0, 0);
        }

        /**
         * 设置带边距的贴边动画，只有 moveType 为 MoveType.slide，设置边距才有意义，这个方法不标准，后面调整
         *
         * @param moveType         贴边动画 MoveType.slide
         * @param slideLeftMargin  贴边动画左边距，默认为 0
         * @param slideRightMargin 贴边动画右边距，默认为 0
         */
        public Builder setMoveType(@MoveType.MOVE_TYPE int moveType, int slideLeftMargin, int slideRightMargin) {
            mMoveType = moveType;
            mSlideLeftMargin = slideLeftMargin;
            mSlideRightMargin = slideRightMargin;
            return this;
        }

        public Builder setMoveStyle(long duration, @Nullable TimeInterpolator interpolator) {
            mDuration = duration;
            mInterpolator = interpolator;
            return this;
        }

        public Builder setTag(@NonNull String tag) {
            mTag = tag;
            return this;
        }

        public Builder setDesktopShow(boolean show) {
            mDesktopShow = show;
            return this;
        }

        public Builder setPermissionListener(PermissionListener listener) {
            mPermissionListener = listener;
            return this;
        }

        public Builder setViewStateListener(ViewStateListener listener) {
            mViewStateListener = listener;
            return this;
        }

        public void build() {
            if (mFloatWindowMap == null) {
                mFloatWindowMap = new HashMap<>();
            }
            if (mFloatWindowMap.containsKey(mTag)) {
                return;
//                throw new IllegalArgumentException("FloatWindow of this tag has been added, Please set a new tag for the new FloatWindow");
            }
            if (mView == null && mLayoutId == 0) {
                throw new IllegalArgumentException("View has not been set!");
            }
            if (mView == null) {
                mView = Util.inflate(mApplicationContext, mLayoutId);
            }
            IFloatWindow floatWindowImpl = new IFloatWindowImpl(this);
            mFloatWindowMap.put(mTag, floatWindowImpl);
        }

    }
}
