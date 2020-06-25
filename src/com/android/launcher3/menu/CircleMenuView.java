package com.android.launcher3.menu;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
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
import com.android.launcher3.menu.imp.OnMenuClickListener;
import com.android.launcher3.menu.imp.OnMenuLongClickListener;
import com.android.mxlibrary.util.XLog;
import com.android.mxlibrary.view.CircleImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * CircleMenuView
 */
public class CircleMenuView extends ViewGroup implements View.OnClickListener, Insettable,
        View.OnLongClickListener {

    private static final int DEFAULT_BUTTON_SIZE = 56;
    private static final float DEFAULT_DISTANCE = DEFAULT_BUTTON_SIZE * 1.5f;
    private static final float DEFAULT_RING_SCALE_RATIO = 1.3f;
    private static final float DEFAULT_CLOSE_ICON_ALPHA = 0.3f;
    private static final float DEFAULT_CHILD_SCALE_RATIO = 0.25f;

    private boolean mIsAnimating = false;

    private int mMenuChildDiameter;
    private int mDurationOpen;
    private int mDurationClose;
    private float mDistance;

    private MenuSweeper mSweeper;
    private Launcher mLauncher;
    private LayoutInflater mInflater;
    private OnMenuClickListener mOnClickListener;
    private OnMenuLongClickListener mOnLongClickListener;

    // 圆形菜单直径
    private int mDiameter = 0;
    private int mChildDiameter = 0;

    public CircleMenuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs);
    }

    public void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        XLog.e(XLog.getTag(), XLog.TAG_GU_STATE);
        mLauncher = Launcher.getLauncher(context);
        mSweeper = new MenuSweeper(this);
        if (attrs == null) {
            throw new IllegalArgumentException("No buttons icons or colors set");
        }

        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CircleMenuView, 0, 0);
        try {
            mMenuChildDiameter = a.getInteger(R.styleable.CircleMenuView_menu_child_diameter, 0);
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

    public void setMenuController(OnMenuClickListener onClickListener,
                                  OnMenuLongClickListener onLongClickListener) {
        this.mOnClickListener = onClickListener;
        this.mOnLongClickListener = onLongClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int w = MeasureSpec.getSize(widthMeasureSpec);
        final int h = MeasureSpec.getSize(heightMeasureSpec);
        mDiameter = Math.min(w, h);
        mChildDiameter = (int) (mDiameter * DEFAULT_CHILD_SCALE_RATIO);
        if (mMenuChildDiameter > 0 && mChildDiameter != mMenuChildDiameter) {
            mChildDiameter = mMenuChildDiameter;
        }
        int childCount = getChildCount();
        if (childCount > 0) {
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mChildDiameter, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mChildDiameter, MeasureSpec.EXACTLY);
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                if (child.getId() == R.id.circle_menu_main_item) {
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
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
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        final int childCount = getChildCount();
        // 根据menu item的个数，计算角度
        float angleDelay = 360 / (float) (isContainCenterMenuItem(childCount) ? childCount - 1 : childCount);
        float tmp = (mDiameter - getPaddingLeft() - getPaddingRight()) / 2f - mChildDiameter / 2f;
        int childLeft;
        int childTop;
        int startAngle = mSweeper.getStartAngle();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            // 中间按钮
            if (child.getId() == R.id.circle_menu_main_item) {
                child.layout(width / 2 - childWidth / 2, height / 2 - childHeight / 2,
                        width / 2 + childWidth / 2, height / 2 + childHeight / 2);
                child.setOnClickListener(this);
                child.setOnLongClickListener(this);
                continue;
            }

            startAngle %= 360;
            int radius = mChildDiameter / 2;
            // 转换为弧度
            double radians = Math.toRadians(startAngle);
            // 计算，中心点到menu item中心的距离
            childLeft = mDiameter / 2 + (int) Math.round(tmp * Math.cos(radians) - radius);
            childTop = mDiameter / 2 + (int) Math.round(tmp * Math.sin(radians) - radius);
            child.layout(childLeft, childTop, childLeft + mChildDiameter, childTop + mChildDiameter);
            // 叠加尺寸
            startAngle += angleDelay;
        }
    }

    private boolean isContainCenterMenuItem(int childCount) {
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            if (child.getId() == R.id.circle_menu_main_item) {
                return true;
            }
        }
        return false;
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
            itemView.setTag(item);
            itemView.setOnClickListener(this);
            addView(itemView);
        }
    }

    @Override
    public void onClick(final View view) {
        if (mSweeper.isFling()) {
            return;
        }
        mOnClickListener.onMenuClick(view);
    }

    @Override
    public void setInsets(Rect insets) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        lp.gravity = Gravity.CENTER;
        setLayoutParams(lp);
        InsettableFrameLayout.dispatchInsets(this, insets);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
    }

    /**
     * 记录上一次的x，y坐标
     */
    private float mLastX;
    private float mLastY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mSweeper.initActionDown();
                // 如果当前已经在快速滚动
                if (mSweeper.isFling()) {
                    // 移除快速滚动的回调
                    mSweeper.abortSweeperAnimation();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // 获得开始的角度
                float start = mSweeper.getAngle(mDiameter, mLastX, mLastY);
                // 获得当前的角度
                float end = mSweeper.getAngle(mDiameter, x, y);
                // 如果是一、四象限，则直接end-start，角度值都是正值
                boolean isQuadrant1Or4 = mSweeper.isQuadrant1Or4(mDiameter, x, y);
                float delta = isQuadrant1Or4 ? end - start : start - end;
                mSweeper.startFling(delta);
                // 重新布局
                requestLayout();
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                // 如果达到该值认为是快速移动
                if (mSweeper.isFastSweep() && !mSweeper.isFling()) {
                    // post一个任务，去自动滚动
                    mSweeper.fling();
                    return true;
                }
                // 如果当前旋转角度超过SWEEP_SLOT屏蔽点击
                if (mSweeper.isNoClick()) {
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


    @Override
    public boolean onLongClick(View v) {
        mOnLongClickListener.onMenuLongClick(v);
        return false;
    }
}
