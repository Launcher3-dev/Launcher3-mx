package com.android.launcher3.menu;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.launcher3.Insettable;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.menu.bean.MenuItem;
import com.android.mxlibrary.view.CircleImageView;

import java.util.List;


/**
 * CircleMenuView
 */
public class CircleMenuView extends ViewGroup implements View.OnClickListener, Insettable {

    private static final int DEFAULT_BUTTON_SIZE = 56;
    private static final float DEFAULT_DISTANCE = DEFAULT_BUTTON_SIZE * 1.5f;
    private static final float DEFAULT_RING_SCALE_RATIO = 1.3f;
    private static final float DEFAULT_CLOSE_ICON_ALPHA = 0.3f;
    private static final float DEFAULT_CHILD_SCALE_RATIO = 0.25f;

    private boolean mIsAnimating = false;

    private int mDurationRing;
    private int mDurationOpen;
    private int mDurationClose;
    private float mDistance;

    private Launcher mLauncher;
    private LayoutInflater mInflater;

    //布局时的开始角度
    private int mStartAngle = 0;
    // 圆形菜单直径
    private int mDiameter = 0;
    private int mChildDiameter = 0;

    public CircleMenuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs);
    }

    public void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        mLauncher = Launcher.getLauncher(context);
        if (attrs == null) {
            throw new IllegalArgumentException("No buttons icons or colors set");
        }

        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CircleMenuView, 0, 0);
        try {
            mDurationRing = a.getInteger(R.styleable.CircleMenuView_duration_ring, getResources().getInteger(android.R.integer.config_mediumAnimTime));
            mDurationOpen = a.getInteger(R.styleable.CircleMenuView_duration_open, getResources().getInteger(android.R.integer.config_mediumAnimTime));
            mDurationClose = a.getInteger(R.styleable.CircleMenuView_duration_close, getResources().getInteger(android.R.integer.config_mediumAnimTime));

            final float density = context.getResources().getDisplayMetrics().density;
            final float defaultDistance = DEFAULT_DISTANCE * density;
            mDistance = a.getDimension(R.styleable.CircleMenuView_distance, defaultDistance);
        } finally {
            a.recycle();
        }
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int w = MeasureSpec.getSize(widthMeasureSpec);
        final int h = MeasureSpec.getSize(heightMeasureSpec);
        mDiameter = Math.min(w, h);
        mChildDiameter = (int) (mDiameter * DEFAULT_CHILD_SCALE_RATIO);
        int childCount = getChildCount();
        if (childCount > 0) {
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mChildDiameter, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mChildDiameter, MeasureSpec.EXACTLY);
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed && mIsAnimating) {
            return;
        }
        int childCount = getChildCount();

        // 根据menu item的个数，计算角度
        float angleDelay = 360 / (float) (childCount);
        float tmp = (mDiameter - getPaddingLeft() - getPaddingRight()) / 2f - mChildDiameter / 2f;
        int childLeft;
        int childTop;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            // 中间按钮
            if (child.getId() == R.id.circle_menu_main_button) {
                child.layout(getMeasuredWidth() / 2 - child.getMeasuredWidth() / 2,
                        getMeasuredHeight() / 2 - child.getMeasuredHeight() / 2,
                        getMeasuredWidth() / 2 + child.getMeasuredWidth() / 2,
                        getMeasuredHeight() / 2 + child.getMeasuredHeight() / 2);
                child.setOnClickListener(this);
                continue;
            }
            mStartAngle %= 360;
            // 计算，中心点到menu item中心的距离
            childLeft = mDiameter
                    / 2
                    + (int) Math.round(tmp
                    * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f
                    * mChildDiameter);
            childTop = mDiameter
                    / 2
                    + (int) Math.round(tmp
                    * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f
                    * mChildDiameter);
            child.layout(childLeft, childTop, childLeft + mChildDiameter, childTop + mChildDiameter);
            // 叠加尺寸
            mStartAngle += angleDelay;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initData();
    }

    private void initData() {
        List<MenuItem> menuItems = MenuDataModel.getMenuItemList();
        for (MenuItem item : menuItems) {
            CircleImageView itemView = (CircleImageView) mInflater.inflate(R.layout.menu_item_layout, this, false);
            itemView.setImageResource(item.getIcon());
            addView(itemView);
        }
    }

    @Override
    public void onClick(final View view) {
        if (mIsAnimating) {
            return;
        }
    }

    @Override
    public void setInsets(Rect insets) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        lp.gravity = Gravity.CENTER;
        setLayoutParams(lp);
        InsettableFrameLayout.dispatchInsets(this, insets);
    }

    /**
     * 记录上一次的x，y坐标
     */
    private float mLastX;
    private float mLastY;

    /**
     * 自动滚动的Runnable
     */
    private AutoFlingRunnable mFlingRunnable;
    /**
     * 检测按下到抬起时旋转的角度
     */
    private float mTmpAngle;
    /**
     * 检测按下到抬起时使用的时间
     */
    private long mDownTime;

    /**
     * 如果移动角度达到该值，则屏蔽点击
     */
    private static final int NOCLICK_VALUE = 3;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mDownTime = System.currentTimeMillis();
                mTmpAngle = 0;
                // 如果当前已经在快速滚动
                if (isFling) {
                    // 移除快速滚动的回调
                    removeCallbacks(mFlingRunnable);
                    isFling = false;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // 获得开始的角度
                float start = getAngle(mLastX, mLastY);
                // 获得当前的角度
                float end = getAngle(x, y);
                // Log.e("TAG", "start = " + start + " , end =" + end);
                // 如果是一、四象限，则直接end-start，角度值都是正值
                int quadrant = getQuadrant(x, y);
                if (quadrant == 1 || quadrant == 4) {
                    mStartAngle += end - start;
                    mTmpAngle += end - start;
                } else {// 二、三象限，色角度值是付值
                    mStartAngle += start - end;
                    mTmpAngle += start - end;
                }
                // 重新布局
                requestLayout();
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                // 计算，每秒移动的角度
                float anglePerSecond = mTmpAngle * 1000
                        / (System.currentTimeMillis() - mDownTime);
                // 如果达到该值认为是快速移动
                if (Math.abs(anglePerSecond) > mFlingAbleValue && !isFling) {
                    // post一个任务，去自动滚动
                    post(mFlingRunnable = new AutoFlingRunnable(anglePerSecond));
                    return true;
                }
                // 如果当前旋转角度超过NOCLICK_VALUE屏蔽点击
                if (Math.abs(mTmpAngle) > NOCLICK_VALUE) {
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 主要为了action_down时，返回true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch 触摸点的x坐标值
     * @param yTouch 触摸点的y坐标值
     *
     * @return 触摸点相对中点的角度
     */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - (mDiameter / 2d);
        double y = yTouch - (mDiameter / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据当前位置计算象限
     *
     * @param x x坐标值
     * @param y y坐标值
     *
     * @return 坐标所在象限
     */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - mDiameter / 2);
        int tmpY = (int) (y - mDiameter / 2);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }
    }

    private int mFlingAbleValue;

    /**
     * 如果每秒旋转角度到达该值，则认为是自动滚动
     *
     * @param flingAbleValue
     */
    public void setFlingAbleValue(int flingAbleValue) {
        this.mFlingAbleValue = flingAbleValue;
    }

    private boolean isFling = false;

    /**
     * 自动滚动的任务
     *
     * @author zhy
     */
    private class AutoFlingRunnable implements Runnable {

        private float angelPerSecond;

        AutoFlingRunnable(float velocity) {
            this.angelPerSecond = velocity;
        }

        public void run() {
            // 如果小于20,则停止
            if ((int) Math.abs(angelPerSecond) < 20) {
                isFling = false;
                return;
            }
            isFling = true;
            // 不断改变mStartAngle，让其滚动，/30为了避免滚动太快
            mStartAngle += (angelPerSecond / 30);
            // 逐渐减小这个值
            angelPerSecond /= 1.0666F;
            postDelayed(this, 30);
            // 重新布局
            requestLayout();
        }
    }

}
