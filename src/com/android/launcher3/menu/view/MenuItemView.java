package com.android.launcher3.menu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.android.launcher3.BubbleTextView;
import com.android.launcher3.R;
import com.android.launcher3.menu.controller.SupperMenuController;
import com.android.launcher3.menu.bean.MenuItem;
import com.android.launcher3.setting.MxSettings;

/**
 * Created by CodeMX
 * DATE 2018/1/16
 * TIME 10:14
 */

public class MenuItemView extends BubbleTextView implements View.OnClickListener {

    public static final int TYPE_MENU = 0;
    public static final int TYPE_WIDGET_GROUP = 1;
    public static final int TYPE_WIDGET_ITEM = 2;

    private int mType;
    private boolean mIsSelected = false;
    private Object menuTag;
    private SupperMenuController mListener;

    public MenuItemView(Context context) {
        this(context, null, 0);
    }

    public MenuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(this);
        setMaxLines(1);
    }

    public void setMenuAction(MenuItem item, SupperMenuController listener) {
        this.mListener = listener;
        menuTag = item;
        mType = TYPE_MENU;
        if (item.getType() == MenuItem.EFFECT) {
            mIsSelected = item.getPosition() == MxSettings.sLauncherEffect;
        }
        applyFromMenuItem(item);
    }

    @Override
    public void onClick(View v) {
        if (menuTag instanceof MenuItem) {
            if (mListener != null) {
                mListener.onClick(v);
            }
        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mIsSelected) {
            drawSelectedIcon(canvas);
        }
    }

    public void invalidate(boolean select) {
        if (mIsSelected == select) {
            return;
        }
        mIsSelected = select;
        invalidate();
    }

    private void drawSelectedIcon(Canvas canvas) {
        Drawable d = getContext().getDrawable(R.drawable.in_use);
        if (d != null) {
            int width = getWidth();
            int iconWidth = width / 5;
            Rect rect = new Rect();
            getDrawingRect(rect);
            Drawable[] compoundDrawables = getCompoundDrawables();
            Drawable compoundDrawable = compoundDrawables[1];
            if (compoundDrawable != null) {
                Rect bounds = compoundDrawable.getBounds();
                int iconLeft = rect.centerX() + bounds.width() / 2;
                int iconTop = rect.centerY() - bounds.height() / 2;
                d.setBounds(iconLeft - iconWidth, iconTop, iconLeft, iconTop + iconWidth);
                d.draw(canvas);
            }
        }
    }

    public Object getMenuTag() {
        return menuTag;
    }

    public int getType() {
        return mType;
    }

}
