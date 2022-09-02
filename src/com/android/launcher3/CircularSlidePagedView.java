package com.android.launcher3;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.android.launcher3.pageindicators.PageIndicator;
import com.android.launcher3.setting.MxSettings;
import com.android.mxlibrary.util.XLog;

public abstract class CircularSlidePagedView<T extends View & PageIndicator> extends PagedView<T> {

    private static final String TAG = "CircularSlidePagedView";

    private static final boolean DEBUG = true;

    protected static final int OVER_FIRST_PAGE_INDEX = -1;
    private static final int OVER_FIRST_INDEX = 0;
    private static final int OVER_LAST_INDEX = 1;
    private int[] mOverPageLeft = new int[]{0, 0};
    protected int mPageWidth = 0;

    public CircularSlidePagedView(Context context) {
        super(context);
    }

    public CircularSlidePagedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularSlidePagedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Returns the index of page to be shown immediately afterwards.
     */
    @Override
    public int getNextPage() {
        int nextPage = mNextPage;
        if (enableLoop()) {
            if (mNextPage == OVER_FIRST_PAGE_INDEX) {
                nextPage = getChildCount() - 1;
            } else if (mNextPage == getChildCount()) {
                nextPage = 0;
            }
        }
        return (mNextPage != INVALID_PAGE) ? nextPage : mCurrentPage;
    }

    @Override
    protected int validateNewPage(int newPage, boolean isSnapTo) {
        int validatedPage = newPage;
        if (enableLoop() && isSnapTo) {
            validatedPage = Math.max(OVER_FIRST_PAGE_INDEX, Math.min(validatedPage, getPageCount()));
        } else {
            // Ensure that it is clamped by the actual set of children in all cases
            validatedPage = super.validateNewPage(newPage, isSnapTo);
        }
        return validatedPage;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mPageScrolls != null && mPageScrolls.length > 1) {
            mPageWidth = Math.abs(mPageScrolls[1] - mPageScrolls[0]);
            mOverPageLeft[(mIsRtl ? OVER_LAST_INDEX : OVER_FIRST_INDEX)] = -mPageWidth;
            mOverPageLeft[(mIsRtl ? OVER_FIRST_INDEX : OVER_LAST_INDEX)] = mPageScrolls[mIsRtl ?
                    0 : (mPageScrolls.length - 1)] + mPageWidth;
        }
        if (DEBUG) {
            XLog.d(TAG, "onLayout mPageWidth:" + mPageWidth
                    + " mOverPageLeft[0]:" + mOverPageLeft[0]
                    + " mOverPageLeft[1]:" + mOverPageLeft[1]);
        }
    }

    // SPRD: add for circular sliding.
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (enableLoop()) {
            drawCircularPageIfNeed(canvas);
        }
    }

    @Override
    public int getScrollForPage(int index) {
        if (enableLoop()) {
            if (index == OVER_FIRST_PAGE_INDEX) {
                return mOverPageLeft[OVER_FIRST_INDEX];
            } else if (index == getChildCount()) {
                return mOverPageLeft[OVER_LAST_INDEX];
            }
        }
        return super.getScrollForPage(index);
    }

    @Override
    protected boolean isXBeforeFirstPage(int x) {
        return !enableLoop() && super.isXBeforeFirstPage(x);
    }

    @Override
    protected boolean isXAfterLastPage(int x) {
        return !enableLoop() && super.isXAfterLastPage(x);
    }

    @Override
    protected int getMinPageIndex() {
        return enableLoop() ? OVER_FIRST_PAGE_INDEX : super.getMinPageIndex();
    }

    @Override
    protected int getMaxPageIndex() {
        return enableLoop() ? getChildCount() : super.getMaxPageIndex();
    }

    public boolean enableLoop() {
        boolean multPage;
        //Can't circular slide when there is only one page
        if (mPageScrolls == null) {
            multPage = false;
        } else {
            multPage = mPageScrolls.length > 1;
        }
        return isPagedViewCircledScroll() && multPage && isPageInTransition();
    }

    @Override
    protected int validateCircularNewPage() {
        int currentPage;
        if (enableLoop()) {
            if (mNextPage == OVER_FIRST_PAGE_INDEX && mPageScrolls != null) {
                currentPage = getPageCount() - 1;
            } else if (mNextPage == getPageCount() && mPageScrolls != null) {
                currentPage = 0;
            } else {
                currentPage = validateNewPage(mNextPage, false);
            }
            scrollTo(mPageScrolls[currentPage], getScrollY());
        } else {
            currentPage = super.validateCircularNewPage();
        }
        if (DEBUG) {
            XLog.d(TAG, "validateCircularNewPage currentPage:" + currentPage);
        }
        return currentPage;
    }

    @Override
    protected int computeTotalDistance(View v, int adjacentPage, int page) {
        int totalDistance;
        if (enableLoop() && (adjacentPage == -1 || adjacentPage == getChildCount())) {
            totalDistance = Math.abs(getScrollForPage(adjacentPage) - getScrollForPage(page));
        } else {
            totalDistance = super.computeTotalDistance(v, adjacentPage, page);
        }
        return totalDistance;
    }

    @Override
    protected int reComputeDelta(int delta, int screenCenter, int page, int totalDistance) {
        int index = 0;
        final int halfScreenSize = getMeasuredWidth() / 2;
        if (enableLoop()) {// 循环滑动
            if (mIsRtl) {// 反向排列
                if (mOverScrollX < 0 && page == 0) {
                    index = getChildCount();
                } else if (mOverScrollX > mMaxScrollX && page == getChildCount() - 1) {
                    index = OVER_FIRST_PAGE_INDEX;
                }
            } else {// 从左向右排列
                // 从最后一屏继续向左滑动Workspace，此时应该循环到第一屏
                if (mOverScrollX > mMaxScrollX && page == 0) {
                    index = getChildCount();
                } else if (mOverScrollX < 0 && page == getChildCount() - 1) {// 从第一屏继续向右滑动，应该循环到最后一屏
                    index = OVER_FIRST_PAGE_INDEX;
                }
            }
        }
        return (index == 0) ? delta : (screenCenter - (getScrollForPage(index) + halfScreenSize));
    }

    private void drawCircularPageIfNeed(Canvas canvas) {
        // X在第一页之前，表示从第一页循环到最后一页
        boolean isXBeforeFirstPage = mIsRtl ? (mOverScrollX > mMaxScrollX) : (mOverScrollX < 0);
        // X在最后一页之后，表示从从最后一页循环到第一页
        boolean isXAfterLastPage = mIsRtl ? (mOverScrollX < 0) : (mOverScrollX > mMaxScrollX);
        if (isXBeforeFirstPage || isXAfterLastPage) {
            long drawingTime = getDrawingTime();
            int childCount = getChildCount();
            canvas.save();
            canvas.clipRect(getScrollX(), getScrollY(), getScrollX() + getRight() - getLeft(),
                    getScrollY() + getBottom() - getTop());
            // here we assume that a page's horizontal padding plus it's measured width
            // equals to ViewPort's width
            // 偏移量是所有页面的宽度之和
            int offset = (mIsRtl ? -childCount : childCount) * (mPageWidth);
            if (isXBeforeFirstPage) {
                // 偏移画布
                canvas.translate(-offset, 0);
                // 从第一页循环到最后一页的过程中，画布中左侧部分会留出空白，此时，为了让我们看似是循环滑动，
                // 就要把最后一页的一部分绘制到画布的空白部分上，一边滑动一边绘制，这样最后一页就是看似是一个
                // 渐显的过程，在绘制之前要先移动画布到对应最后一页的位置
                drawChild(canvas, getPageAt(childCount - 1), drawingTime);
                // 还原画布位置
                canvas.translate(+offset, 0);
            } else {
                // 偏移画布
                canvas.translate(+offset, 0);
                // 类似上面的从第一页到最后一页的过程
                drawChild(canvas, getPageAt(0), drawingTime);
                // 还原画布
                canvas.translate(-offset, 0);
            }
            canvas.restore();
        }
    }

    @Override
    protected boolean isPagedViewCircledScroll() {
        return MxSettings.getInstance().isPageViewCircleScroll();
    }

}
