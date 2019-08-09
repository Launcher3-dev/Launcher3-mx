/*
 * Copyright (C) 2012 The Android Open Source Project
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

import android.animation.LayoutTransition;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.ScrollView;

import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.pageindicators.PageIndicator;
import com.android.launcher3.touch.OverScroll;
import com.android.launcher3.util.Thunk;
import com.android.mxlibrary.util.XLog;

import java.util.ArrayList;

import static com.android.launcher3.compat.AccessibilityManagerCompat.isAccessibilityEnabled;
import static com.android.launcher3.compat.AccessibilityManagerCompat.isObservedEventType;

/**
 * An abstraction of the original Workspace which supports browsing through a
 * sequential list of "pages"
 */
public abstract class PagedView<T extends View & PageIndicator> extends ViewGroup {
    private static final String TAG = "PagedView";
    private static final boolean DEBUG = false;


    // ---- modify by codemx.cn(新的循环滑动(原始为-1)) --- 2019/05/14  --- start
    protected static final int INVALID_PAGE = -2;
    // ---- modify by codemx.cn(新的循环滑动) --- 2019/05/14  --- end
    protected static final ComputePageScrollsLogic SIMPLE_SCROLL_LOGIC = (v) -> v.getVisibility() != GONE;

    public static final int PAGE_SNAP_ANIMATION_DURATION = 750;
    public static final int SLOW_PAGE_SNAP_ANIMATION_DURATION = 950;

    // OverScroll constants
    private final static int OVERSCROLL_PAGE_SNAP_ANIMATION_DURATION = 270;

    private static final float RETURN_TO_ORIGINAL_PAGE_THRESHOLD = 0.33f;
    // The page is moved more than halfway, automatically move to the next page on touch up.
    private static final float SIGNIFICANT_MOVE_THRESHOLD = 0.4f;

    private static final float MAX_SCROLL_PROGRESS = 1.0f;

    // The following constants need to be scaled based on density. The scaled versions will be
    // assigned to the corresponding member variables below.
    private static final int FLING_THRESHOLD_VELOCITY = 500;
    private static final int MIN_SNAP_VELOCITY = 1500;
    private static final int MIN_FLING_VELOCITY = 250;

    public static final int INVALID_RESTORE_PAGE = -1001;

    private boolean mFreeScroll = false;
    private boolean mSettleOnPageInFreeScroll = false;

    protected int mFlingThresholdVelocity;
    protected int mMinFlingVelocity;// 最小惯性速度
    protected int mMinSnapVelocity;

    protected boolean mFirstLayout = true;

    @ViewDebug.ExportedProperty(category = "launcher")
    protected int mCurrentPage;

    @ViewDebug.ExportedProperty(category = "launcher")
    protected int mNextPage = INVALID_PAGE;
    protected int mMaxScrollX;
    protected LauncherScroller mScroller;
    private Interpolator mDefaultInterpolator;
    private VelocityTracker mVelocityTracker;

    private float mDownMotionX;
    private float mDownMotionY;
    private float mLastMotionX;
    private float mLastMotionXRemainder;
    private float mTotalMotionX;

    /**
     * 记录每个未隐藏页面的左边界到PageView最左侧起始位置的距离，正常情况，
     * 所有页面是从左到右排列，这里就是记录每个页面需要从第一个页面移动到当前位置
     * 需要的移动的距离
     */
    protected int[] mPageScrolls;

    protected final static int TOUCH_STATE_REST = 0;
    protected final static int TOUCH_STATE_SCROLLING = 1;
    protected final static int TOUCH_STATE_PREV_PAGE = 2;
    protected final static int TOUCH_STATE_NEXT_PAGE = 3;

    protected int mTouchState = TOUCH_STATE_REST;

    protected int mTouchSlop;
    private int mMaximumVelocity;
    protected boolean mAllowOverScroll = true;

    protected static final int INVALID_POINTER = -1;

    protected int mActivePointerId = INVALID_POINTER;

    protected boolean mIsPageInTransition = false;

    protected boolean mWasInOverscroll = false;

    /**
     * mOverScrollX is equal to getScrollX() when we're within the normal scroll range.
     * Otherwise(否则) it is equal to the scaled overscroll position. We use a separate
     * value so as to prevent the screens from continuing to translate beyond the normal bounds.
     * <p>
     * getScrollX():表示PageView左侧边缘位置从屏幕左侧边缘位置（Y轴）滑动到当前位置的滑动变量，
     * 如果View左侧边缘从屏幕左侧边缘移动到了屏幕左侧，getScrollX为View左侧边缘到屏幕边缘距离；
     * 如果View左侧边缘从屏幕左侧边缘移动到了屏幕右侧，getScrollX为View左侧边缘到屏幕边缘距离的负值
     * 例如：View在左侧边缘与屏幕左侧边缘重合，那么getScrollX=0（屏幕宽度假设720）
     * View左侧边缘到屏幕边缘的距离|getScrollX值
     * -2260                 2260
     * -1440                 1440
     * -720                  720
     * 0                       0
     * 720                   -720
     * 1440                  -1440
     * 2260                  -2260
     * 距离为负值，说明View的左侧边缘在屏幕左侧边缘的左侧，反之在右侧
     * (PageView向左滑动为正方向，getScrollX为正值；反之负方向，getScrollX为负值)
     */
    protected int mOverScrollX;

    protected int mUnboundedScrollX;

    // Page Indicator
    @Thunk
    int mPageIndicatorViewId;
    protected T mPageIndicator;

    // add by codemx.cn ---- 2018/09/04 -- start
    public static float mDensity;
    // add by codemx.cn ---- 2018/09/04 -- end
    // Convenience/caching
    private static final Rect sTmpRect = new Rect();

    protected final Rect mInsets = new Rect();
    protected boolean mIsRtl;// 从右到左排布（正常是从左到右）

    // Similar to the platform implementation of isLayoutValid();
    protected boolean mIsLayoutValid;

    private int[] mTmpIntPair = new int[2];

    public PagedView(Context context) {
        this(context, null);
    }

    public PagedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PagedView, defStyle, 0);
        mPageIndicatorViewId = a.getResourceId(R.styleable.PagedView_pageIndicator, -1);
        a.recycle();

        setHapticFeedbackEnabled(false);
        mIsRtl = Utilities.isRtl(getResources());
        init();
    }

    /**
     * Initializes various states for this workspace.
     */
    protected void init() {
        mScroller = new LauncherScroller(getContext());
        setDefaultInterpolator(Interpolators.SCROLL);
        mCurrentPage = 0;

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        // --- add by codemx.cn --- 2018/09/06 --- start
        mDensity = getResources().getDisplayMetrics().density;
        // --- add by codemx.cn --- 2018/09/06 --- end

        // --- modify by codemx.cn --- 2019/04/05 --- start
        mFlingThresholdVelocity = (int) (FLING_THRESHOLD_VELOCITY * mDensity);
        mMinFlingVelocity = (int) (MIN_FLING_VELOCITY * mDensity);
        mMinSnapVelocity = (int) (MIN_SNAP_VELOCITY * mDensity);
        // --- modify by codemx.cn --- 2019/04/05 --- end

        if (Utilities.ATLEAST_OREO) {
            setDefaultFocusHighlightEnabled(false);
        }
    }

    protected void setDefaultInterpolator(Interpolator interpolator) {
        mDefaultInterpolator = interpolator;
        mScroller.setInterpolator(mDefaultInterpolator);
    }

    public void initParentViews(View parent) {
        if (mPageIndicatorViewId > -1) {
            mPageIndicator = parent.findViewById(mPageIndicatorViewId);
            mPageIndicator.setMarkersCount(getChildCount());
        }
    }

    public T getPageIndicator() {
        return mPageIndicator;
    }

    /**
     * Returns the index of the currently displayed page. When in free scroll mode, this is the page
     * that the user was on before entering free scroll mode (e.g. the home screen page they
     * long-pressed on to enter the overview). Try using {@link #getPageNearestToCenterOfScreen()}
     * to get the page the user is currently scrolling over.
     */
    public int getCurrentPage() {
        return mCurrentPage;
    }

    /**
     * Returns the index of page to be shown immediately afterwards.
     */
    public int getNextPage() {
        return (mNextPage != INVALID_PAGE) ? mNextPage : mCurrentPage;
    }

    public int getPageCount() {
        return getChildCount();
    }

    public View getPageAt(int index) {
        return getChildAt(index);
    }

    protected int indexToPage(int index) {
        return index;
    }

    protected void scrollAndForceFinish(int scrollX) {
        scrollTo(scrollX, 0);
        mScroller.setFinalX(scrollX);
        forceFinishScroller(true);
    }

    /**
     * Updates the scroll of the current page immediately to its final scroll position.  We use this
     * in CustomizePagedView to allow tabs to share the same PagedView while resetting the scroll of
     * the previous tab page.
     */
    protected void updateCurrentPageScroll() {
        // If the current page is invalid, just reset the scroll position to zero
        int newX = 0;
        if (0 <= mCurrentPage && mCurrentPage < getPageCount()) {
            newX = getScrollForPage(mCurrentPage);
        }
        scrollAndForceFinish(newX);
    }

    private void abortScrollerAnimation(boolean resetNextPage) {
        mScroller.abortAnimation();
        // We need to clean up the next page here to avoid computeScrollHelper from
        // updating current page on the pass.
        if (resetNextPage) {
            mNextPage = INVALID_PAGE;
            pageEndTransition();
        }
    }

    private void forceFinishScroller(boolean resetNextPage) {
        mScroller.forceFinished(true);
        // We need to clean up the next page here to avoid computeScrollHelper from
        // updating current page on the pass.
        if (resetNextPage) {
            mNextPage = INVALID_PAGE;
            pageEndTransition();
        }
    }

    protected int validateNewPage(int newPage, boolean isSnapTo) {
        // Ensure that it is clamped by the actual set of children in all cases
        return Utilities.boundToRange(newPage, 0, getPageCount() - 1);
    }

    /**
     * Sets the current page.
     */
    public void setCurrentPage(int currentPage) {
        if (!mScroller.isFinished()) {
            abortScrollerAnimation(true);
        }
        // don't introduce any checks like mCurrentPage == currentPage here-- if we change the
        // the default
        if (getChildCount() == 0) {
            return;
        }

        int prevPage = mCurrentPage;
        mCurrentPage = validateNewPage(currentPage, false);
        XLog.e(XLog.getTag(), XLog.TAG_GU + "mCurrentPage= " + mCurrentPage);
        updateCurrentPageScroll();
        notifyPageSwitchListener(prevPage);
        invalidate();
    }

    /**
     * Should be called whenever the page changes. In the case of a scroll, we wait until the page
     * has settled.
     */
    protected void notifyPageSwitchListener(int prevPage) {
        updatePageIndicator();
    }

    private void updatePageIndicator() {
        if (mPageIndicator != null) {
            mPageIndicator.setActiveMarker(getNextPage());
        }
    }

    protected void pageBeginTransition() {
        if (!mIsPageInTransition) {
            mIsPageInTransition = true;
            onPageBeginTransition();
        }
    }

    protected void pageEndTransition() {
        if (mIsPageInTransition) {
            mIsPageInTransition = false;
            onPageEndTransition();
        }
    }

    protected boolean isPageInTransition() {
        return mIsPageInTransition;
    }

    /**
     * Called when the page starts moving as part of the scroll. Subclasses can override this
     * to provide custom behavior during animation.
     */
    protected void onPageBeginTransition() {
    }

    /**
     * Called when the page ends moving as part of the scroll. Subclasses can override this
     * to provide custom behavior during animation.
     */
    protected void onPageEndTransition() {
        mWasInOverscroll = false;
    }

    protected int getUnboundedScrollX() {
        return mUnboundedScrollX;
    }

    @Override
    public void scrollBy(int x, int y) {
        scrollTo(getUnboundedScrollX() + x, getScrollY() + y);
    }

    @Override
    public void scrollTo(int x, int y) {
        // In free scroll mode, we clamp the scrollX
        if (mFreeScroll) {
            // If the scroller is trying to move to a location beyond the maximum allowed
            // in the free scroll mode, we make sure to end the scroll operation.
            if (!mScroller.isFinished() && (x > mMaxScrollX || x < 0)) {
                forceFinishScroller(false);
            }

            x = Utilities.boundToRange(x, 0, mMaxScrollX);
        }

        mUnboundedScrollX = x;

        boolean isXBeforeFirstPage = isXBeforeFirstPage(x);
        boolean isXAfterLastPage = isXAfterLastPage(x);
        if (isXBeforeFirstPage) {
            super.scrollTo(mIsRtl ? mMaxScrollX : 0, y);
            if (mAllowOverScroll) {
                mWasInOverscroll = true;
                if (mIsRtl) {
                    overScroll(x - mMaxScrollX);
                } else {
                    overScroll(x);
                }
            }
        } else if (isXAfterLastPage) {
            super.scrollTo(mIsRtl ? 0 : mMaxScrollX, y);
            if (mAllowOverScroll) {
                mWasInOverscroll = true;
                if (mIsRtl) {
                    overScroll(x);
                } else {
                    overScroll(x - mMaxScrollX);
                }
            }
        } else {
            if (mWasInOverscroll) {
                overScroll(0);
                mWasInOverscroll = false;
            }
            mOverScrollX = x;
            super.scrollTo(x, y);
        }
    }

    private void sendScrollAccessibilityEvent() {
        if (isObservedEventType(getContext(), AccessibilityEvent.TYPE_VIEW_SCROLLED)) {
            if (mCurrentPage != getNextPage()) {
                AccessibilityEvent ev =
                        AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_SCROLLED);
                ev.setScrollable(true);
                ev.setScrollX(getScrollX());
                ev.setScrollY(getScrollY());
                ev.setMaxScrollX(mMaxScrollX);
                ev.setMaxScrollY(0);

                sendAccessibilityEventUnchecked(ev);
            }
        }
    }

    // we moved this functionality to a helper function so SmoothPagedView can reuse it
    protected boolean computeScrollHelper() {
        return computeScrollHelper(true);
    }

    protected void announcePageForAccessibility() {
        if (isAccessibilityEnabled(getContext())) {
            // Notify the user when the page changes
            announceForAccessibility(getCurrentPageDescription());
        }
    }

    protected boolean computeScrollHelper(boolean shouldInvalidate) {
        if (mScroller.computeScrollOffset()) {
            // Don't bother scrolling if the page does not need to be moved
            if (getUnboundedScrollX() != mScroller.getCurrX()
                    || getScrollY() != mScroller.getCurrY()
                    || mOverScrollX != mScroller.getCurrX()) {
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            }
            if (shouldInvalidate) {
                invalidate();
            }
            return true;
        } else if (mNextPage != INVALID_PAGE && shouldInvalidate) {
            sendScrollAccessibilityEvent();

            int prevPage = mCurrentPage;
            mCurrentPage = validateCircularNewPage();
            XLog.e(XLog.getTag(), XLog.TAG_GU + "mCurrentPage= " + mCurrentPage);
            mNextPage = INVALID_PAGE;

            notifyPageSwitchListener(prevPage);

            // We don't want to trigger a page end moving unless the page has settled
            // and the user has stopped scrolling
            if (mTouchState == TOUCH_STATE_REST) {
                pageEndTransition();
            }

            if (canAnnouncePageDescription()) {
                announcePageForAccessibility();
            }
        }
        return false;
    }

    @Override
    public void computeScroll() {
        computeScrollHelper();
    }

    public int getExpectedHeight() {
        return getMeasuredHeight();
    }

    public int getNormalChildHeight() {
        return getExpectedHeight() - getPaddingTop() - getPaddingBottom()
                - mInsets.top - mInsets.bottom;
    }

    public int getExpectedWidth() {
        return getMeasuredWidth();
    }

    public int getNormalChildWidth() {
        return getExpectedWidth() - getPaddingLeft() - getPaddingRight()
                - mInsets.left - mInsets.right;
    }

    @Override
    public void requestLayout() {
        mIsLayoutValid = false;
        super.requestLayout();
    }

    @Override
    public void forceLayout() {
        mIsLayoutValid = false;
        super.forceLayout();
    }

    // add by codemx.cn ---- 20190712 ---plus- start
    public static class LayoutParams extends ViewGroup.LayoutParams {
        public boolean isFullScreenPage = false;

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public void addFullScreenPage(View page) {
        LayoutParams lp = generateDefaultLayoutParams();
        lp.isFullScreenPage = true;
        super.addView(page, 0, lp);
    }

    // Convenience methods to get the actual width/height of the PagedView (since it is measured
    // to be larger to account for the minimum possible scale)
    int getViewportWidth() {
        return mViewport.width();
    }

    public int getViewportHeight() {
        return mViewport.height();
    }

    // Convenience methods to get the offset ASSUMING that we are centering the pages in the
    // PagedView both horizontally and vertically
    int getViewportOffsetX() {
        return (getMeasuredWidth() - getViewportWidth()) / 2;
    }

    int getViewportOffsetY() {
        return (getMeasuredHeight() - getViewportHeight()) / 2;
    }

    private Rect mViewport = new Rect();
    private float mMinScale = 1f;
    private boolean mUseMinScale = false;
    // add by codemx.cn ---- 20190712 ---plus- end

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        // We measure the dimensions of the PagedView to be larger than the pages so that when we
        // zoom out (and scale down), the view is still contained in the parent
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        // NOTE: We multiply by 2f to account for the fact that depending on the offset of the
        // viewport, we can be at most one and a half screens offset once we scale down
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int maxSize = Math.max(dm.widthPixels + mInsets.left + mInsets.right,
                dm.heightPixels + mInsets.top + mInsets.bottom);

        // -- gyc modify ------------20180314----
        // 原始桌面缩放的是workspace，所以倍数是2f，现在缩放的是CellLayout，所以不需要再放大
        int parentWidthSize = (int) (1f * maxSize);
        int parentHeightSize = (int) (1f * maxSize);
        // -- gyc modify ------------20180314----
        int scaledWidthSize, scaledHeightSize;
        if (mUseMinScale) {
            scaledWidthSize = (int) (parentWidthSize / mMinScale);
            scaledHeightSize = (int) (parentHeightSize / mMinScale);
        } else {
            scaledWidthSize = widthSize;
            scaledHeightSize = heightSize;
        }
        mViewport.set(0, 0, widthSize, heightSize);


        if (widthMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.UNSPECIFIED) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        // Return early if we aren't given a proper dimension
        if (widthSize <= 0 || heightSize <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        // modify by codemx.cn ---- 20190712 ---plus- start
        /* Allow the height to be set as WRAP_CONTENT. This allows the particular case
         * of the All apps view on XLarge displays to not take up more space then it needs. Width
         * is still not allowed to be set as WRAP_CONTENT since many parts of the code expect
         * each effect_page to have the same width.
         */
        final int verticalPadding = getPaddingTop() + getPaddingBottom();
        final int horizontalPadding = getPaddingLeft() + getPaddingRight();

        int referenceChildWidth = 0;
        // The children are given the same width and height as the workspace
        // unless they were set to WRAP_CONTENT
        if (DEBUG) Log.d(TAG, "PagedView.onMeasure(): " + widthSize + ", " + heightSize);
        if (DEBUG) Log.d(TAG, "PagedView.scaledSize: " + scaledWidthSize + ", " + scaledHeightSize);
        if (DEBUG) Log.d(TAG, "PagedView.parentSize: " + parentWidthSize + ", " + parentHeightSize);
        if (DEBUG) Log.d(TAG, "PagedView.horizontalPadding: " + horizontalPadding);
        if (DEBUG) Log.d(TAG, "PagedView.verticalPadding: " + verticalPadding);
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            // disallowing padding in paged view (just pass 0)
            final View child = getPageAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                int childWidthMode;
                int childHeightMode;
                int childWidth;
                int childHeight;

                if (!lp.isFullScreenPage) {
                    if (lp.width == LayoutParams.WRAP_CONTENT) {
                        childWidthMode = MeasureSpec.AT_MOST;
                    } else {
                        childWidthMode = MeasureSpec.EXACTLY;
                    }

                    if (lp.height == LayoutParams.WRAP_CONTENT) {
                        childHeightMode = MeasureSpec.AT_MOST;
                    } else {
                        childHeightMode = MeasureSpec.EXACTLY;
                    }

                    childWidth = getViewportWidth() - horizontalPadding
                            - mInsets.left - mInsets.right;
                    childHeight = getViewportHeight() - verticalPadding
                            - mInsets.top - mInsets.bottom;
                } else {
                    childWidthMode = MeasureSpec.EXACTLY;
                    childHeightMode = MeasureSpec.EXACTLY;

                    childWidth = getViewportWidth();
                    childHeight = getViewportHeight();
                }
                if (referenceChildWidth == 0) {
                    referenceChildWidth = childWidth;
                }

                final int childWidthMeasureSpec =
                        MeasureSpec.makeMeasureSpec(childWidth, childWidthMode);
                final int childHeightMeasureSpec =
                        MeasureSpec.makeMeasureSpec(childHeight, childHeightMode);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
        // modify by codemx.cn ---- 20190712 ---plus- end

        setMeasuredDimension(widthSize, heightSize);
    }

    protected void restoreScrollOnLayout() {
        setCurrentPage(getNextPage());
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mIsLayoutValid = true;
        final int childCount = getChildCount();
        boolean pageScrollChanged = false;
        if (mPageScrolls == null || childCount != mPageScrolls.length) {
            mPageScrolls = new int[childCount];
            pageScrollChanged = true;
        }

        if (childCount == 0) {
            return;
        }

        if (DEBUG) Log.d(TAG, "PagedView.onLayout()");

        // 如果页面变化了
        if (getPageScrolls(mPageScrolls, true, SIMPLE_SCROLL_LOGIC)) {
            pageScrollChanged = true;
        }

        final LayoutTransition transition = getLayoutTransition();
        // If the transition is running defer（推迟） updating max scroll, as some empty pages could
        // still be present, and a max scroll change could cause sudden jumps in scroll.
        if (transition != null && transition.isRunning()) {
            transition.addTransitionListener(new LayoutTransition.TransitionListener() {

                @Override
                public void startTransition(LayoutTransition transition, ViewGroup container,
                                            View view, int transitionType) {
                }

                @Override
                public void endTransition(LayoutTransition transition, ViewGroup container,
                                          View view, int transitionType) {
                    // Wait until all transitions are complete.
                    if (!transition.isRunning()) {
                        transition.removeTransitionListener(this);
                        updateMaxScrollX();
                    }
                }
            });
        } else {
            updateMaxScrollX();
        }

        if (mFirstLayout && mCurrentPage >= 0 && mCurrentPage < childCount) {
            updateCurrentPageScroll();
            mFirstLayout = false;
        }

        if (mScroller.isFinished() && pageScrollChanged) {
            restoreScrollOnLayout();
        }
    }

    /**
     * Initializes {@code outPageScrolls} with scroll positions for view at that index. The length
     * of {@code outPageScrolls} should be same as the the childCount
     *
     * @param outPageScrolls 记录每个未隐藏页面的左边界到PageView最左侧起始位置的距离，正常情况，
     *                       所有页面是从左到右排列，这里就是记录每个页面需要从第一个页面移动到当前位置
     *                       需要的移动的距离
     * @param layoutChildren 是否排列Children
     * @param scrollLogic    接口，用来判断该页面是否隐藏，隐藏则不计算
     *
     * @return 记录每个未隐藏页面滑动起始位置的数据是否改变
     */
    protected boolean getPageScrolls(int[] outPageScrolls, boolean layoutChildren,
                                     ComputePageScrollsLogic scrollLogic) {
        final int childCount = getChildCount();

        // add by codemx.cn ---- 20190712 ---plus- start
        int offsetX = getViewportOffsetX();
        int offsetY = getViewportOffsetY();
        // Update the viewport offsets
        mViewport.offset(offsetX, offsetY);
        // add by codemx.cn ---- 20190712 ---plus- end

        // 起始下标（正常从左到右排列，起始是下标是0）
        final int startIndex = mIsRtl ? childCount - 1 : 0;
        // 结束下标（正常从左到右，结束下标是最后一个）
        final int endIndex = mIsRtl ? -1 : childCount;
        // 差量（从右到左是负值，是减；从左到右是正值，是加）
        final int delta = mIsRtl ? -1 : 1;
        // 竖直(Y)方向的中心位置
        final int verticalCenter = (getPaddingTop() + getMeasuredHeight() + mInsets.top
                - mInsets.bottom - getPaddingBottom()) / 2;

        // modify by codemx.cn ---- 20190712 ---plus- start
        LayoutParams lp = (LayoutParams) getChildAt(startIndex).getLayoutParams();
        LayoutParams nextLp;
        // 最左边的页面的起始位置（有可能是有边距的）
        final int scrollOffsetLeft = mInsets.left + (lp.isFullScreenPage ? 0 : getPaddingLeft());
        // modify by codemx.cn ---- 20190712 ---plus- end
        boolean pageScrollChanged = false;
        // 从第1屏到最后一屏计算每屏的距离
        for (int i = startIndex, childLeft = scrollOffsetLeft + offsetForPageScrolls();
             i != endIndex;
             i += delta) {
            final View child = getPageAt(i);
            lp = (LayoutParams) child.getLayoutParams();
            // 如果当前第i个屏幕隐藏则不计算
            if (scrollLogic.shouldIncludeView(child)) {
                // 页面顶部
                int childTop;
                if (lp.isFullScreenPage) {
                    childTop = offsetY;
                } else {
                    childTop = verticalCenter - child.getMeasuredHeight() / 2;
                }
                // 页面宽度
                final int childWidth = child.getMeasuredWidth();
                // 页面高度
                final int childHeight = child.getMeasuredHeight();
                // 排列该页面
                if (layoutChildren) {
                    // 布局该页面
                    child.layout(childLeft, childTop,
                            childLeft + child.getMeasuredWidth(), childTop + childHeight);
                }

                // modify by codemx.cn ---- 20190712 ---plus- start
                int next = i + delta;
                if (next != endIndex) {
                    nextLp = (LayoutParams) getPageAt(next).getLayoutParams();
                } else {
                    nextLp = null;
                }

                int pageSpacing = 0;
                // Prevent full screen pages from showing in the viewport
                // when they are not the current effect_page.
                if (lp.isFullScreenPage) {
                    pageSpacing = getPaddingLeft();
                } else if (nextLp != null && nextLp.isFullScreenPage) {
                    pageSpacing = getPaddingRight();
                } else {
                    pageSpacing = getPaddingRight() + getPaddingLeft();
                }
                // modify by codemx.cn ---- 20190712 ---plus- end

                // TODO 要考虑反向排列问题
                // 每个页面左侧的滑动有效位置
                final int pageScroll = childLeft - (lp.isFullScreenPage ? 0 : getPaddingLeft());
                if (outPageScrolls[i] != pageScroll) {
                    pageScrollChanged = true;
                    outPageScrolls[i] = pageScroll;
                }
                childLeft += childWidth + pageSpacing;
            }
        }
        return pageScrollChanged;
    }

    private void updateMaxScrollX() {
        mMaxScrollX = computeMaxScrollX();
    }

    // 计算X方向最大滚动距离
    protected int computeMaxScrollX() {
        int childCount = getChildCount();
        if (childCount > 0) {
            final int index = mIsRtl ? 0 : childCount - 1;
            return getScrollForPage(index);
        } else {
            return 0;
        }
    }

    // 页面偏移距离
    protected int offsetForPageScrolls() {
        return 0;
    }

    private void dispatchPageCountChanged() {
        if (mPageIndicator != null) {
            mPageIndicator.setMarkersCount(getChildCount());
        }
        // This ensures that when children are added, they get the correct transforms / alphas
        // in accordance with any scroll effects.
        invalidate();
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        dispatchPageCountChanged();
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        mCurrentPage = validateNewPage(mCurrentPage, false);
        XLog.e(XLog.getTag(), XLog.TAG_GU + "mCurrentPage= " + mCurrentPage);
        dispatchPageCountChanged();
    }

    protected int getChildOffset(int index) {
        if (index < 0 || index > getChildCount() - 1) return 0;
        return getPageAt(index).getLeft();
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        int page = indexToPage(indexOfChild(child));
        if (page != mCurrentPage || !mScroller.isFinished()) {
            if (immediate) {
                setCurrentPage(page);
            } else {
                snapToPage(page);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        int focusablePage;
        if (mNextPage != INVALID_PAGE) {
            focusablePage = mNextPage;
        } else {
            focusablePage = mCurrentPage;
        }
        View v = getPageAt(focusablePage);
        if (v != null) {
            return v.requestFocus(direction, previouslyFocusedRect);
        }
        return false;
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (super.dispatchUnhandledMove(focused, direction)) {
            return true;
        }

        if (mIsRtl) {
            if (direction == View.FOCUS_LEFT) {
                direction = View.FOCUS_RIGHT;
            } else if (direction == View.FOCUS_RIGHT) {
                direction = View.FOCUS_LEFT;
            }
        }
        if (direction == View.FOCUS_LEFT) {
            if (getCurrentPage() > 0) {
                snapToPage(getCurrentPage() - 1);
                return true;
            }
        } else if (direction == View.FOCUS_RIGHT) {
            if (getCurrentPage() < getPageCount() - 1) {
                snapToPage(getCurrentPage() + 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (getDescendantFocusability() == FOCUS_BLOCK_DESCENDANTS) {
            return;
        }

        // XXX-RTL: This will be fixed in a future CL
        if (mCurrentPage >= 0 && mCurrentPage < getPageCount()) {
            getPageAt(mCurrentPage).addFocusables(views, direction, focusableMode);
        }
        if (direction == View.FOCUS_LEFT) {
            if (mCurrentPage > 0) {
                getPageAt(mCurrentPage - 1).addFocusables(views, direction, focusableMode);
            }
        } else if (direction == View.FOCUS_RIGHT) {
            if (mCurrentPage < getPageCount() - 1) {
                getPageAt(mCurrentPage + 1).addFocusables(views, direction, focusableMode);
            }
        }
    }

    /**
     * If one of our descendant views decides that it could be focused now, only
     * pass that along if it's on the current page.
     * <p>
     * This happens when live folders requery, and if they're off page, they
     * end up calling requestFocus, which pulls it on page.
     */
    @Override
    public void focusableViewAvailable(View focused) {
        View current = getPageAt(mCurrentPage);
        View v = focused;
        while (true) {
            if (v == current) {
                super.focusableViewAvailable(focused);
                return;
            }
            if (v == this) {
                return;
            }
            ViewParent parent = v.getParent();
            if (parent instanceof View) {
                v = (View) v.getParent();
            } else {
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            // We need to make sure to cancel our long press if
            // a scrollable widget takes over touch events
            final View currentPage = getPageAt(mCurrentPage);
            currentPage.cancelLongPress();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    /**
     * Returns whether x and y originated within the buffered viewport
     */
    private boolean isTouchPointInViewportWithBuffer(int x, int y) {
        sTmpRect.set(-getMeasuredWidth() / 2, 0, 3 * getMeasuredWidth() / 2, getMeasuredHeight());
        return sTmpRect.contains(x, y);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */
        acquireVelocityTrackerAndAddMovement(ev);

        // Skip touch handling if there are no pages to swipe
        if (getChildCount() <= 0) return super.onInterceptTouchEvent(ev);

        /*
         * Shortcut the most recurring case: the user is in the dragging
         * state and he is moving his finger.  We want to intercept this
         * motion.
         */
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) &&
                (mTouchState == TOUCH_STATE_SCROLLING)) {
            return true;
        }

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */
                if (mActivePointerId != INVALID_POINTER) {
                    determineScrollingStart(ev);
                }
                // if mActivePointerId is INVALID_POINTER, then we must have missed an ACTION_DOWN
                // event. in that case, treat the first occurence of a move event as a ACTION_DOWN
                // i.e. fall through to the next case (don't break)
                // (We sometimes miss ACTION_DOWN events in Workspace because it ignores all events
                // while it's small- this was causing a crash before we checked for INVALID_POINTER)
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                // Remember location of down touch
                mDownMotionX = x;
                mDownMotionY = y;
                mLastMotionX = x;
                mLastMotionXRemainder = 0;
                mTotalMotionX = 0;
                mActivePointerId = ev.getPointerId(0);

                /*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't.  mScroller.isFinished should be false when
                 * being flinged.
                 */
                final int xDist = Math.abs(mScroller.getFinalX() - mScroller.getCurrX());
                final boolean finishedScrolling = (mScroller.isFinished() || xDist < mTouchSlop / 3);

                if (finishedScrolling) {
                    mTouchState = TOUCH_STATE_REST;
                    if (!mScroller.isFinished() && !mFreeScroll) {
                        setCurrentPage(getNextPage());
                        pageEndTransition();
                    }
                } else {
                    if (isTouchPointInViewportWithBuffer((int) mDownMotionX, (int) mDownMotionY)) {
                        mTouchState = TOUCH_STATE_SCROLLING;
                    } else {
                        mTouchState = TOUCH_STATE_REST;
                    }
                }

                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                resetTouchState();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                releaseVelocityTracker();
                break;
        }

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        return mTouchState != TOUCH_STATE_REST;
    }

    public boolean isHandlingTouch() {
        return mTouchState != TOUCH_STATE_REST;
    }

    protected void determineScrollingStart(MotionEvent ev) {
        determineScrollingStart(ev, 1.0f);
    }

    /*
     * Determines if we should change the touch state to start scrolling after the
     * user moves their touch point too far.
     */
    protected void determineScrollingStart(MotionEvent ev, float touchSlopScale) {
        // Disallow scrolling if we don't have a valid pointer index
        final int pointerIndex = ev.findPointerIndex(mActivePointerId);
        if (pointerIndex == -1) return;

        // Disallow scrolling if we started the gesture from outside the viewport
        final float x = ev.getX(pointerIndex);
        final float y = ev.getY(pointerIndex);
        if (!isTouchPointInViewportWithBuffer((int) x, (int) y)) return;

        final int xDiff = (int) Math.abs(x - mLastMotionX);

        final int touchSlop = Math.round(touchSlopScale * mTouchSlop);
        boolean xMoved = xDiff > touchSlop;

        if (xMoved) {
            // Scroll if the user moved far enough along the X axis
            mTouchState = TOUCH_STATE_SCROLLING;
            mTotalMotionX += Math.abs(mLastMotionX - x);
            mLastMotionX = x;
            mLastMotionXRemainder = 0;
            onScrollInteractionBegin();
            pageBeginTransition();
            // Stop listening for things like pinches.
            requestDisallowInterceptTouchEvent(true);
        }
    }

    protected void cancelCurrentPageLongPress() {
        // Try canceling the long press. It could also have been scheduled
        // by a distant descendant, so use the mAllowLongPress flag to block
        // everything
        final View currentPage = getPageAt(mCurrentPage);
        if (currentPage != null) {
            currentPage.cancelLongPress();
        }
    }

    // --- modify by codemx.cn --- 2018/09/08 -- start

    /**
     * 影响特效存在情况下循环滑动时最后一页与第一页切换时特效不对的问题
     *
     * @param scrollX getScrollX
     * @param v       当前页面
     * @param page    当前页面对应下标
     *
     * @return 滑动进度
     */
    public float getScrollProgress(int scrollX, View v, int page) {
        final int halfScreenSize = getMeasuredWidth() / 2;
        int delta = scrollX - (getScrollForPage(page) + halfScreenSize);

        final int totalDistance;
        // 滑动过程中正在进入的页面下标
        int adjacentPage = page + 1;
        if ((delta < 0 && !mIsRtl) || (delta > 0 && mIsRtl)) {
            adjacentPage = page - 1;
        }

        // 计算一个页面从开始到结束（一个页面切换过程）走过的完整距离
        totalDistance = computeTotalDistance(v, adjacentPage, page);
        delta = reComputeDelta(delta, scrollX, page, totalDistance);

        // 范围：[-1,1]
        float scrollProgress = delta / (totalDistance * 1.0f);
        scrollProgress = Math.min(scrollProgress, MAX_SCROLL_PROGRESS);
        scrollProgress = Math.max(scrollProgress, -MAX_SCROLL_PROGRESS);
        return scrollProgress;
    }
    // --- modify by codemx.cn --- 2018/09/08 -- end

    /**
     * 获取下标为index的页面左侧边缘到PageView左侧边缘的距离
     *
     * @param index 页面下标
     *
     * @return 当前页面左侧边缘到PageView左侧边缘的距离
     */
    public int getScrollForPage(int index) {
        if (mPageScrolls == null || index >= mPageScrolls.length || index < 0) {
            return 0;
        } else {
            return mPageScrolls[index];
        }
    }

    // While layout transitions are occurring, a child's position may stray from its baseline
    // position. This method returns the magnitude of this stray at any given time.
    public int getLayoutTransitionOffsetForPage(int index) {
        if (mPageScrolls == null || index >= mPageScrolls.length || index < 0) {
            return 0;
        } else {
            View child = getChildAt(index);

            // modify by codemx.cn ---- 20190712 ---plus- start
            int scrollOffset = 0;
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (!lp.isFullScreenPage) {
                scrollOffset = mIsRtl ? getPaddingRight() : getPaddingLeft();
            }
            // modify by codemx.cn ---- 20190712 ---plus- end

            int baselineX = mPageScrolls[index] + scrollOffset;
            return (int) (child.getX() - baselineX);
        }
    }

    protected void dampedOverScroll(float amount) {
        if (Float.compare(amount, 0f) == 0) return;

        int overScrollAmount = OverScroll.dampedScroll(amount, getMeasuredWidth());
        if (amount < 0) {
            mOverScrollX = overScrollAmount;
            super.scrollTo(mOverScrollX, getScrollY());
        } else {
            mOverScrollX = mMaxScrollX + overScrollAmount;
            super.scrollTo(mOverScrollX, getScrollY());
        }
        invalidate();
    }

    protected void overScroll(float amount) {
        dampedOverScroll(amount);
    }

    protected void setEnableOverScroll(boolean enable) {
        mAllowOverScroll = enable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);

        // Skip touch handling if there are no pages to swipe
        if (getChildCount() <= 0) return super.onTouchEvent(ev);

        acquireVelocityTrackerAndAddMovement(ev);

        final int action = ev.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                /*
                 * If being flinged and user touches, stop the fling. isFinished
                 * will be false if being flinged.
                 */
                if (!mScroller.isFinished()) {
                    abortScrollerAnimation(false);
                }

                // Remember where the motion event started
                mDownMotionX = mLastMotionX = ev.getX();
                mDownMotionY = ev.getY();
                mLastMotionXRemainder = 0;
                mTotalMotionX = 0;
                mActivePointerId = ev.getPointerId(0);

                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    onScrollInteractionBegin();
                    pageBeginTransition();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    // Scroll to follow the motion event
                    final int pointerIndex = ev.findPointerIndex(mActivePointerId);

                    if (pointerIndex == -1) return true;

                    final float x = ev.getX(pointerIndex);
                    final float deltaX = mLastMotionX + mLastMotionXRemainder - x;

                    mTotalMotionX += Math.abs(deltaX);

                    // Only scroll and update mLastMotionX if we have moved some discrete amount.  We
                    // keep the remainder because we are actually testing if we've moved from the last
                    // scrolled position (which is discrete).
                    if (Math.abs(deltaX) >= 1.0f) {
                        scrollBy((int) deltaX, 0);
                        mLastMotionX = x;
                        mLastMotionXRemainder = deltaX - (int) deltaX;
                    } else {
                        awakenScrollBars();
                    }
                } else {
                    determineScrollingStart(ev);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    final int activePointerId = mActivePointerId;
                    final int pointerIndex = ev.findPointerIndex(activePointerId);
                    final float x = ev.getX(pointerIndex);
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int velocityX = (int) velocityTracker.getXVelocity(activePointerId);
                    final int deltaX = (int) (x - mDownMotionX);
                    final int pageWidth = getPageAt(mCurrentPage).getMeasuredWidth();
                    boolean isSignificantMove = Math.abs(deltaX) > pageWidth *
                            SIGNIFICANT_MOVE_THRESHOLD;

                    mTotalMotionX += Math.abs(mLastMotionX + mLastMotionXRemainder - x);
                    boolean isFling = mTotalMotionX > mTouchSlop && shouldFlingForVelocity(velocityX);

                    if (!mFreeScroll) {
                        // In the case that the page is moved far to one direction and then is flung
                        // in the opposite direction, we use a threshold to determine whether we should
                        // just return to the starting page, or if we should skip one further.
                        boolean returnToOriginalPage = false;
                        if (Math.abs(deltaX) > pageWidth * RETURN_TO_ORIGINAL_PAGE_THRESHOLD &&
                                Math.signum(velocityX) != Math.signum(deltaX) && isFling) {
                            returnToOriginalPage = true;
                        }

                        int finalPage;
                        // We give flings precedence over large moves, which is why we short-circuit our
                        // test for a large move if a fling has been registered. That is, a large
                        // move to the left and fling to the right will registerRemoteCallback as a fling to the right.
                        boolean isDeltaXLeft = mIsRtl ? deltaX > 0 : deltaX < 0;
                        boolean isVelocityXLeft = mIsRtl ? velocityX > 0 : velocityX < 0;
                        if (((isSignificantMove && !isDeltaXLeft && !isFling) ||
                                (isFling && !isVelocityXLeft))
                                && mCurrentPage > getMinPageIndex()) {
                            finalPage = returnToOriginalPage ? mCurrentPage : mCurrentPage - 1;
                            snapToPageWithVelocity(finalPage, velocityX);
                        } else if (((isSignificantMove && isDeltaXLeft && !isFling) ||
                                (isFling && isVelocityXLeft)) &&
                                mCurrentPage < getMaxPageIndex()) {
                            finalPage = returnToOriginalPage ? mCurrentPage : mCurrentPage + 1;
                            snapToPageWithVelocity(finalPage, velocityX);
                        } else {
                            snapToDestination();
                        }
                    } else {
                        if (!mScroller.isFinished()) {
                            abortScrollerAnimation(true);
                        }

                        float scaleX = getScaleX();
                        int vX = (int) (-velocityX * scaleX);
                        int initialScrollX = (int) (getScrollX() * scaleX);

                        mScroller.setInterpolator(mDefaultInterpolator);
                        mScroller.fling(initialScrollX,
                                getScrollY(), vX, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
                        int unscaledScrollX = (int) (mScroller.getFinalX() / scaleX);
                        mNextPage = getPageNearestToCenterOfScreen(unscaledScrollX);
                        int firstPageScroll = getScrollForPage(!mIsRtl ? 0 : getPageCount() - 1);
                        int lastPageScroll = getScrollForPage(!mIsRtl ? getPageCount() - 1 : 0);
                        if (mSettleOnPageInFreeScroll && unscaledScrollX > 0
                                && unscaledScrollX < mMaxScrollX) {
                            // If scrolling ends in the half of the added space that is closer to the
                            // end, settle to the end. Otherwise snap to the nearest page.
                            // If flinging past one of the ends, don't change the velocity as it will
                            // get stopped at the end anyway.
                            final int finalX = unscaledScrollX < firstPageScroll / 2 ?
                                    0 :
                                    unscaledScrollX > (lastPageScroll + mMaxScrollX) / 2 ?
                                            mMaxScrollX :
                                            getScrollForPage(mNextPage);

                            mScroller.setFinalX((int) (finalX * getScaleX()));
                            // Ensure the scroll/snap doesn't happen too fast;
                            int extraScrollDuration = OVERSCROLL_PAGE_SNAP_ANIMATION_DURATION
                                    - mScroller.getDuration();
                            if (extraScrollDuration > 0) {
                                mScroller.extendDuration(extraScrollDuration);
                            }
                        }
                        invalidate();
                    }
                    onScrollInteractionEnd();
                } else if (mTouchState == TOUCH_STATE_PREV_PAGE) {
                    // at this point we have not moved beyond the touch slop
                    // (otherwise mTouchState would be TOUCH_STATE_SCROLLING), so
                    // we can just page
                    int nextPage = Math.max(0, mCurrentPage - 1);
                    if (nextPage != mCurrentPage) {
                        snapToPage(nextPage);
                    } else {
                        snapToDestination();
                    }
                } else if (mTouchState == TOUCH_STATE_NEXT_PAGE) {
                    // at this point we have not moved beyond the touch slop
                    // (otherwise mTouchState would be TOUCH_STATE_SCROLLING), so
                    // we can just page
                    int nextPage = Math.min(getChildCount() - 1, mCurrentPage + 1);
                    if (nextPage != mCurrentPage) {
                        snapToPage(nextPage);
                    } else {
                        snapToDestination();
                    }
                }

                // End any intermediate reordering states
                resetTouchState();
                cancelCurrentPageLongPress();
                break;

            case MotionEvent.ACTION_CANCEL:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    snapToDestination();
                    onScrollInteractionEnd();
                }
                resetTouchState();
                cancelCurrentPageLongPress();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                releaseVelocityTracker();
                break;
        }

        return true;
    }

    protected boolean shouldFlingForVelocity(int velocityX) {
        return Math.abs(velocityX) > mFlingThresholdVelocity;
    }

    private void resetTouchState() {
        releaseVelocityTracker();
        mTouchState = TOUCH_STATE_REST;
        mActivePointerId = INVALID_POINTER;
    }

    /**
     * Triggered by scrolling via touch
     */
    protected void onScrollInteractionBegin() {
    }

    protected void onScrollInteractionEnd() {
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_CLASS_POINTER) != 0) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_SCROLL: {
                    // Handle mouse (or ext. device) by shifting the page depending on the scroll
                    final float vscroll;
                    final float hscroll;
                    if ((event.getMetaState() & KeyEvent.META_SHIFT_ON) != 0) {
                        vscroll = 0;
                        hscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    } else {
                        vscroll = -event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                        hscroll = event.getAxisValue(MotionEvent.AXIS_HSCROLL);
                    }
                    if (hscroll != 0 || vscroll != 0) {
                        boolean isForwardScroll = mIsRtl ? (hscroll < 0 || vscroll < 0)
                                : (hscroll > 0 || vscroll > 0);
                        if (isForwardScroll) {
                            scrollRight();
                        } else {
                            scrollLeft();
                        }
                        return true;
                    }
                }
            }
        }
        return super.onGenericMotionEvent(event);
    }

    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = mDownMotionX = ev.getX(newPointerIndex);
            mLastMotionXRemainder = 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        int page = indexToPage(indexOfChild(child));
        if (page >= 0 && page != getCurrentPage() && !isInTouchMode()) {
            snapToPage(page);
        }
    }

    public int getPageNearestToCenterOfScreen() {
        return getPageNearestToCenterOfScreen(getScrollX());
    }

    private int getPageNearestToCenterOfScreen(int scaledScrollX) {
        int screenCenter = scaledScrollX + (getMeasuredWidth() / 2);
        int minDistanceFromScreenCenter = Integer.MAX_VALUE;
        int minDistanceFromScreenCenterIndex = -1;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View layout = getPageAt(i);
            int childWidth = layout.getMeasuredWidth();
            int halfChildWidth = (childWidth / 2);
            int childCenter = getChildOffset(i) + halfChildWidth;
            int distanceFromScreenCenter = Math.abs(childCenter - screenCenter);
            if (distanceFromScreenCenter < minDistanceFromScreenCenter) {
                minDistanceFromScreenCenter = distanceFromScreenCenter;
                minDistanceFromScreenCenterIndex = i;
            }
        }
        return minDistanceFromScreenCenterIndex;
    }

    protected void snapToDestination() {
        snapToPage(getPageNearestToCenterOfScreen(), getPageSnapDuration());
    }

    protected boolean isInOverScroll() {
        return (mOverScrollX > mMaxScrollX || mOverScrollX < 0);
    }

    protected int getPageSnapDuration() {
        if (isInOverScroll()) {
            return OVERSCROLL_PAGE_SNAP_ANIMATION_DURATION;
        }
        return PAGE_SNAP_ANIMATION_DURATION;
    }

    // We want the duration of the page snap animation to be influenced by the distance that
    // the screen has to travel, however, we don't want this duration to be effected in a
    // purely linear fashion. Instead, we use this method to moderate the effect that the distance
    // of travel has on the overall snap duration.
    private float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    /**
     * 按照给定速度滑向对应页面
     *
     * @param whichPage 要滑向的页面
     * @param velocity  设定的速度
     *
     * @return 是否滑向成功
     */
    protected boolean snapToPageWithVelocity(int whichPage, int velocity) {

        whichPage = validateNewPage(whichPage, true);
        int halfScreenSize = getMeasuredWidth() / 2;

        final int newX = getScrollForPage(whichPage);
        int delta = newX - getUnboundedScrollX();
        int duration = 0;

        // 如果当前速度小于最小速度，那么按照默认时间完成滑动
        if (Math.abs(velocity) < mMinFlingVelocity) {
            // If the velocity is low enough, then treat this more as an automatic page advance
            // as opposed to an apparent physical response to flinging
            return snapToPage(whichPage, PAGE_SNAP_ANIMATION_DURATION);
        }

        // Here we compute a "distance" that will be used in the computation of the overall
        // snap duration. This is a function of the actual distance that needs to be traveled;
        // we keep this value close to half screen size in order to reduce the variance in snap
        // duration as a function of the distance the page needs to travel.
        float distanceRatio = Math.min(1f, 1.0f * Math.abs(delta) / (2 * halfScreenSize));
        float distance = halfScreenSize + halfScreenSize *
                distanceInfluenceForSnapDuration(distanceRatio);

        velocity = Math.abs(velocity);
        velocity = Math.max(mMinSnapVelocity, velocity);

        // we want the page's snap velocity to approximately match the velocity at which the
        // user flings, so we scale the duration by a value near to the derivative of the scroll
        // interpolator at zero, ie. 5. We use 4 to make it a little slower.
        duration = 4 * Math.round(1000 * Math.abs(distance / velocity));

        return snapToPage(whichPage, delta, duration);
    }

    public boolean snapToPage(int whichPage) {
        return snapToPage(whichPage, PAGE_SNAP_ANIMATION_DURATION);
    }

    public boolean snapToPageImmediately(int whichPage) {
        return snapToPage(whichPage, PAGE_SNAP_ANIMATION_DURATION, true, null);
    }

    public boolean snapToPage(int whichPage, int duration) {
        return snapToPage(whichPage, duration, false, null);
    }

    public boolean snapToPage(int whichPage, int duration, TimeInterpolator interpolator) {
        return snapToPage(whichPage, duration, false, interpolator);
    }

    protected boolean snapToPage(int whichPage, int duration, boolean immediate,
                                 TimeInterpolator interpolator) {
        whichPage = validateNewPage(whichPage, true);

        int newX = getScrollForPage(whichPage);
        final int delta = newX - getUnboundedScrollX();
        return snapToPage(whichPage, delta, duration, immediate, interpolator);
    }

    protected boolean snapToPage(int whichPage, int delta, int duration) {
        return snapToPage(whichPage, delta, duration, false, null);
    }

    protected boolean snapToPage(int whichPage, int delta, int duration, boolean immediate,
                                 TimeInterpolator interpolator) {
        if (mFirstLayout) {
            setCurrentPage(whichPage);
            return false;
        }

        if (FeatureFlags.IS_DOGFOOD_BUILD) {
            duration *= Settings.System.getFloat(getContext().getContentResolver(),
                    Settings.System.WINDOW_ANIMATION_SCALE, 1);
        }
        XLog.e(XLog.getTag(), XLog.TAG_GU + "whichPage:  " + whichPage);
        // 验证whichPage是否有效
        whichPage = validateNewPage(whichPage, true);

        mNextPage = whichPage;
        XLog.e(XLog.getTag(), XLog.TAG_GU + "mNextPage:  " + mNextPage);

        awakenScrollBars(duration);
        if (immediate) {
            duration = 0;
        } else if (duration == 0) {
            duration = Math.abs(delta);
        }

        if (duration != 0) {
            pageBeginTransition();
        }

        if (!mScroller.isFinished()) {
            abortScrollerAnimation(false);
        }

        if (interpolator != null) {
            mScroller.setInterpolator(interpolator);
        } else {
            mScroller.setInterpolator(mDefaultInterpolator);
        }

        mScroller.startScroll(getUnboundedScrollX(), 0, delta, 0, duration);

        updatePageIndicator();

        // Trigger a compute() to finish switching pages if necessary
        if (immediate) {
            computeScroll();
            pageEndTransition();
        }

        invalidate();
        return Math.abs(delta) > 0;
    }

    public boolean scrollLeft() {
        if (getNextPage() > 0) {
            snapToPage(getNextPage() - 1);
            return true;
        }
        return false;
    }

    public boolean scrollRight() {
        if (getNextPage() < getChildCount() - 1) {
            snapToPage(getNextPage() + 1);
            return true;
        }
        return false;
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        // Some accessibility services have special logic for ScrollView. Since we provide same
        // accessibility info as ScrollView, inform the service to handle use the same way.
        return ScrollView.class.getName();
    }

    protected boolean isPageOrderFlipped() {
        return false;
    }

    /* Accessibility */
    @SuppressWarnings("deprecation")
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        final boolean pagesFlipped = isPageOrderFlipped();
        info.setScrollable(getPageCount() > 1);
        if (getCurrentPage() < getPageCount() - 1) {
            info.addAction(pagesFlipped ? AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
                    : AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        }
        if (getCurrentPage() > 0) {
            info.addAction(pagesFlipped ? AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
                    : AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        }

        // Accessibility-wise, PagedView doesn't support long click, so disabling it.
        // Besides disabling the accessibility long-click, this also prevents this view from getting
        // accessibility focus.
        info.setLongClickable(false);
        info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK);
    }

    @Override
    public void sendAccessibilityEvent(int eventType) {
        // Don't let the view send real scroll events.
        if (eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            super.sendAccessibilityEvent(eventType);
        }
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setScrollable(getPageCount() > 1);
    }

    @Override
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (super.performAccessibilityAction(action, arguments)) {
            return true;
        }
        final boolean pagesFlipped = isPageOrderFlipped();
        switch (action) {
            case AccessibilityNodeInfo.ACTION_SCROLL_FORWARD: {
                if (pagesFlipped ? scrollLeft() : scrollRight()) {
                    return true;
                }
            }
            break;
            case AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD: {
                if (pagesFlipped ? scrollRight() : scrollLeft()) {
                    return true;
                }
            }
            break;
        }
        return false;
    }

    protected boolean canAnnouncePageDescription() {
        return true;
    }

    protected String getCurrentPageDescription() {
        return getContext().getString(R.string.default_scroll_format,
                getNextPage() + 1, getChildCount());
    }

    protected interface ComputePageScrollsLogic {
        boolean shouldIncludeView(View view);
    }

    public int[] getVisibleChildrenRange() {
        float visibleLeft = 0;
        float visibleRight = visibleLeft + getMeasuredWidth();
        float scaleX = getScaleX();
        if (scaleX < 1 && scaleX > 0) {
            float mid = getMeasuredWidth() / 2;
            visibleLeft = mid - ((mid - visibleLeft) / scaleX);
            visibleRight = mid + ((visibleRight - mid) / scaleX);
        }

        int leftChild = -1;
        int rightChild = -1;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getPageAt(i);

            float left = child.getLeft() + child.getTranslationX() - getScrollX();
            if (left <= visibleRight && (left + child.getMeasuredWidth()) >= visibleLeft) {
                if (leftChild == -1) {
                    leftChild = i;
                }
                rightChild = i;
            }
        }
        mTmpIntPair[0] = leftChild;
        mTmpIntPair[1] = rightChild;
        return mTmpIntPair;
    }

    // ---- add by codemx.cn(新的循环滑动) --- 2019/04/01  --- start
    // SPRD: add for circular sliding. adjust if need circular slide

    // X在第一页之前，表示从第一页循环到最后一页
    protected boolean isXBeforeFirstPage(int x) {
        return mIsRtl ? (x > mMaxScrollX) : (x < 0);
    }

    // X在最后一页之后，表示从从最后一页循环到第一页
    protected boolean isXAfterLastPage(int x) {
        return mIsRtl ? (x < 0) : (x > mMaxScrollX);
    }

    protected int getMinPageIndex() {
        return 0;
    }

    protected int getMaxPageIndex() {
        return getChildCount() - 1;
    }

    protected int validateCircularNewPage() {
        return validateNewPage(mNextPage, false);
    }

    protected int computeTotalDistance(View v, int adjacentPage, int page) {
        int totalDistance;
        if (adjacentPage < 0 || adjacentPage > getChildCount() - 1) {
            totalDistance = v.getMeasuredWidth();
        } else {
            // 正在进入页面左边缘到正在退出页面左边缘的距离
            totalDistance = Math.abs(getScrollForPage(adjacentPage) - getScrollForPage(page));
        }
        return totalDistance;
    }

    protected int reComputeDelta(int delta, int screenCenter, int page, int totalDistance) {
        return delta;
    }

    protected abstract boolean isPagedViewCircledScroll();

    // ---- add by codemx.cn(新的循环滑动) --- 2019/04/01  --- end

}
