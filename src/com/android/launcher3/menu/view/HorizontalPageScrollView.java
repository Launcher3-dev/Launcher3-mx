package com.android.launcher3.menu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.menu.bean.MenuItem;
import com.android.launcher3.menu.imp.IMenuAdapter;
import com.android.launcher3.userevent.nano.LauncherLogProto;


/**
 * Created by yuchuan
 * DATE 16/4/5
 * TIME 09:37
 */
public class HorizontalPageScrollView<T extends MenuItem> extends LinearLayout
        implements DragSource, View.OnClickListener, View.OnLongClickListener {

    private Context mContext;

    private Launcher mLauncher;

    private OverScroller mScroller;

    public static boolean startTouch = true;

    private static final String TAG = "HorizontalScrollView";

    /*
     * 速度追踪器，主要是为了通过当前滑动速度判断当前滑动是否为fling
     */
    private VelocityTracker mVelocityTracker;

    /*
     * 记录当前屏幕下标，取值范围是：0 到 getMenuItemCount()-1
     */
    private int mCurScreen = 0;

    /*
     * Touch状态值 0：静止 1：滑动
     */
    private static final int TOUCH_STATE_REST = 0;

    private static final int TOUCH_STATE_SCROLLING = 1;

    /*
     * 记录当前touch事件状态--滑动（TOUCH_STATE_SCROLLING）、静止（TOUCH_STATE_REST 默认）
     */
    private int mTouchState = TOUCH_STATE_REST;

    private static final int SNAP_VELOCITY = 300;

    /*
     * 记录滑动时上次手指所处的位置
     */
    private float mLastMotionX;

    private float mLastMotionY;

    private int mPageCount;

    private OnScrollChangedListener mScrollChangedListener = null;

    private int mCellViewWidth;
    private int mCellViewHeight;
    private int mCountX;

    /**
     * 是否以页滑动
     */
    private boolean mPageScroll;

    // 是自由滑动还是分页滑动
    private boolean mFreeScroll;
    // 如果自由滑动失效，如果不是自由滑动，不满一屏的是否居中显示
    private boolean mCenterLayout;

    public HorizontalPageScrollView(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        mScroller = new OverScroller(mContext);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MenuDeviceProfile, defStyle, 0);
        mFreeScroll = ta.getBoolean(R.styleable.MenuDeviceProfile_free_scroll, false);
        mCenterLayout = ta.getBoolean(R.styleable.MenuDeviceProfile_center_layout, true);
        mCountX = ta.getInteger(R.styleable.MenuDeviceProfile_menu_numColumns, 4);
        ta.recycle();
        mLauncher = Launcher.getLauncher(context);
    }

    public HorizontalPageScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalPageScrollView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);

        int childWidthSize = widthSize - (getPaddingLeft() + getPaddingRight());
        int childHeightSize = heightSize - (getPaddingTop() + getPaddingBottom());

        mCellViewWidth = DeviceProfile.calculateCellWidth(childWidthSize, mCountX);
        mCellViewHeight = DeviceProfile.calculateCellHeight(childHeightSize, 1);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            measureChild(child);
        }
    }

    private void measureChild(View child) {
        final DeviceProfile profile = Launcher.getLauncher(mContext).getDeviceProfile();
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        lp.width = mCellViewWidth;
        lp.height = mCellViewHeight;
        int cHeight = getCellContentHeight(profile);
        int cellPaddingY = (int) Math.max(0, ((lp.height - cHeight) / 2f));
        int cellPaddingX = (int) (profile.edgeMarginPx / 2f);
        child.setPadding(cellPaddingX, cellPaddingY, cellPaddingX, 0);

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    int getCellContentHeight(DeviceProfile profile) {
        return Math.min(getMeasuredHeight(), profile.getCellHeight(CellLayout.HOTSEAT));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int top = getPaddingTop();
        mPageCount = getPageCount();
        if (!mFreeScroll) {
            if (mPageCount > 1) {
                int left = 0;
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() == View.GONE) {
                        continue;
                    }
                    if (i % mCountX == 0) {
                        left += getPaddingLeft();
                    }
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    child.layout(left, top, left + lp.width, top + lp.height);
                    left += mCellViewWidth;
                    if (i % mCountX == (mCountX - 1)) {
                        left += getPaddingRight();
                    }
                }
            } else {
                int left = getPaddingLeft();
                if (mCenterLayout) {
                    left = getWidth() / 2 - mCellViewWidth * childCount / 2;
                }
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() == View.GONE) {
                        continue;
                    }
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    child.layout(left, top, left + lp.width, top + lp.height);
                    left += lp.width;
                }
            }
        } else {
            int left = getPaddingLeft();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                child.layout(left, top, left + lp.width, top + lp.width);
                left += lp.width;
            }
        }
    }

    public void setAdapter(IMenuAdapter adapter) {
        removeAllViews();
        resetPage();
        adapter.setContainer(this);
        int N = adapter.getMenuItemCount();
        for (int i = 0; i < N; i++) {
            View view = adapter.getChildView(i, null, this);
            addView(view, i);
            startLayoutAnimation();
        }
    }

    private int getPageColumn() {
        int maxWidth = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            maxWidth = Math.max(mCellViewWidth, maxWidth);
        }
        return (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) / (maxWidth);
    }

    /**
     * 是否满屏
     */
    private boolean childIsFull() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight() < getAllChildLength();
    }

    private int getAllChildLength() {
        int column = mCountX;
        int childVisibleCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            childVisibleCount++;
        }
        return (int) Math.ceil(childVisibleCount * (1.0f * getWidth() / column));
    }

    // 计算最左边的child的左边位置,如果没有满一屏则所有居中
    private int computeLayoutLeftPoint() {
        boolean isFull = childIsFull();
        if (isFull) {
            return getPaddingLeft();
        } else {
            int width = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                width += child.getPaddingLeft() + child.getPaddingRight() + child.getMeasuredWidth();
            }

            if (getChildCount() > 0) {
                width += (getChildCount() - 1);
            }

            return (getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - width) / 2;
        }
    }

    // 获取页数
    public int getPageCount() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (params.width == LayoutParams.MATCH_PARENT) {
                return childCount;
            }
        }
        return (int) Math.ceil(1.0 * getAllChildLength() / getWidth());
    }

    public int getAllPageCount() {
        return mPageCount;
    }

    public void setSupportPageScroll(boolean pageScroll) {
        this.mPageScroll = pageScroll;
    }

    public boolean getSupportPageScroll() {
        return mPageScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);

        final int action = event.getAction();

        final float x = event.getX();

        switch (action) {

            case MotionEvent.ACTION_DOWN:

                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastMotionX = x;
                break;

            case MotionEvent.ACTION_MOVE:

                //横向滑动距离
                int deltaX = (int) (mLastMotionX - x);
                mLastMotionX = x;
                scrollBy(deltaX, 0);
                break;

            case MotionEvent.ACTION_UP:

                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) velocityTracker.getXVelocity();
                if (!mFreeScroll) {
                    if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {

                        // Fling enough to move left
                        int page = mCurScreen - 1;
                        page = page > 0 ? page : 0;
                        snapToScreen(page);

                    } else if (velocityX < -SNAP_VELOCITY && mCurScreen < mPageCount) {

                        // Fling enough to move right
                        int page = mCurScreen + 1;
                        page = page < mPageCount ? page : mPageCount - 1;
                        snapToScreen(page);

                    } else {
                        snapToDestination();
                    }
                } else {

                    View view = getChildAt(getChildCount() - 1);
                    if (view == null)
                        return true;
                    int end = view.getRight() - getWidth();
                    end = Math.max(0, end);
                    float minVelocity = ViewConfiguration.getMinimumFlingVelocity();
                    float maxVelocity = ViewConfiguration.getMaximumFlingVelocity();
                    final float step = 0.2f;

                    if (velocityX > SNAP_VELOCITY) {
                        // F;ling enough to move left
                        int delta;
                        if (getScrollX() > -getWidth() && getScrollX() <= 0) {
                            delta = -getScrollX();
                        } else {
                            float velocity = (velocityX - minVelocity);
                            float moveLength = velocity * step;
                            if (moveLength > getScrollX()) {
                                delta = -getScrollX();
                            } else {
                                delta = -(int) moveLength;
                            }
                        }
                        mScroller.startScroll(getScrollX(), 0, delta, 0, 300);
                        invalidate(); // Redraw the layout
                    } else if (velocityX < -SNAP_VELOCITY) {
                        int delta;
                        if (end - getScrollX() < getWidth()) {
                            delta = end - getScrollX();
                        } else {
                            float velocity = (minVelocity - velocityX);
                            float moveLength = velocity * step;
                            if (moveLength > (end - getScrollX())) {
                                delta = end - getScrollX();
                            } else {
                                delta = (int) moveLength;
                            }
                        }
                        mScroller.startScroll(getScrollX(), 0, delta, 0, 300);
                        invalidate(); // Redraw the layout
                    } else {
                        int length = 0;
                        if (getScrollX() > end) {
                            length = end - getScrollX();
                        } else if (getScrollX() < 0) {
                            length = -getScrollX();
                        }
                        mScroller.startScroll(getScrollX(), 0, length, 0, 300);
                        invalidate(); // Redraw the layout
                    }
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return true;
    }

    /**
     * 方法名称：snapToDestination 方法描述：根据当前位置滑动到相应界面
     */

    public void snapToDestination() {

        final int screenWidth = getWidth();

        final int destScreen = (int) Math.ceil(1.0f * getScrollX() / screenWidth);

        snapToScreen(destScreen < mPageCount ? destScreen : mPageCount - 1);

    }

    /**
     * 方法名称：snapToScreen 方法描述：滑动到到第whichScreen（从0开始）个界面，有过渡效果
     *
     * @param whichScreen
     */
    public void snapToScreen(int whichScreen) {
        // get the valid layout effect_page
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        if (getScrollX() != (whichScreen * getWidth())) {
            final int delta = whichScreen * getWidth() - getScrollX();
            mScroller.startScroll(getScrollX(), 0, delta, 0, 300);

            notifyScrollChanged(whichScreen, mCurScreen);
            mCurScreen = whichScreen;
            invalidate(); // Redraw the layout
        }
    }

    /**
     * 当滑动切换界面时执行相应操作
     *
     * @param newPage 目标页面
     * @param oldPage 要离开的页面
     */
    private void notifyScrollChanged(int newPage, int oldPage) {
        if (mScrollChangedListener != null) {
            mScrollChangedListener.onScrollChanged(newPage, oldPage);
            int allPageCount = getPageCount();
            mScrollChangedListener.onPagePositionChanged(newPage, allPageCount);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }

        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_MOVE:
                final int xDiff = (int) Math.abs(mLastMotionX - x);
                if (xDiff > ViewConfiguration.getTouchSlop()) {
                    if (Math.abs(mLastMotionY - y) / Math.abs(mLastMotionX - x) < 1) {
                        mTouchState = TOUCH_STATE_SCROLLING;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                break;
        }

        return mTouchState != TOUCH_STATE_REST;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            scrollTo(x, 0);
            postInvalidate();
        }
    }

    /**
     * 是否滑动到了结尾
     *
     * @param distanceX 滑动距离
     *
     * @return
     */
    private boolean isEnd(float distanceX) {
        int fastLeft = getPaddingLeft();
        View view = getChildAt(getChildCount() - 1);
        int lastRight = view.getRight() - getWidth();

        int mScrollX = 0;
        return (distanceX > 0 && lastRight < mScrollX)
                || (distanceX < 0 && fastLeft > mScrollX);
    }

    public int getCurrentPage() {
        return mCurScreen;
    }

    public void setOnScrollChangeListener(OnScrollChangedListener onScrollChangeListener) {
        this.mScrollChangedListener = onScrollChangeListener;
    }

    @Override
    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onDropCompleted(View target, DropTarget.DragObject d, boolean success) {
//        if (!success || (target != mLauncher.getWorkspace() &&
//                !(target instanceof DeleteDropTarget) && !(target instanceof Folder))) {
//            // Exit spring loaded mode if we have not successfully dropped or have not handled the
//            // drop in Workspace
//            mLauncher.exitSpringLoadedDragModeDelayed(true,
//                    Launcher.EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT, null);
//        }
//        mLauncher.unlockScreenOrientation(false);
        if (!success) {
            d.deferDragViewCleanupPostAnimation = false;
        }
    }

    public interface OnScrollChangedListener {
        void onScrollChanged(int newPage, int oldPage);

        void onPagePositionChanged(int currentPage, int allPage);
    }

    public void resetPage() {
        scrollTo(0, 0);
        mCurScreen = 0;
    }

    /**
     * 设置等距布局
     *
     * @param equidistant 是否等距
     */
    public void setEquidistantLayout(boolean equidistant) {
        mFreeScroll = equidistant;
        requestLayout();
    }

    /**
     * 是否等距布局
     *
     * @return
     */
    public boolean hasEquidistantLayout() {
        return mFreeScroll;
    }

}
