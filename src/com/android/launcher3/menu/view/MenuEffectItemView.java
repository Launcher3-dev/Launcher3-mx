package com.android.launcher3.menu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.menu.SupperMenuController;
import com.android.launcher3.menu.bean.MenuItem;
import com.android.launcher3.setting.MxSettings;

import static com.android.launcher3.menu.view.MenuItemView.TYPE_MENU;

/**
 * Created by yuchuan on 2018/4/4.
 */

public class MenuEffectItemView extends BubbleTextView {

    private int mType;
    private boolean mIsSelected = false;
    private Object menuTag;
    private Launcher mLauncher;
    private SupperMenuController mListener;

    public MenuEffectItemView(Context context) {
        super(context);
    }

    public MenuEffectItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuEffectItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setMenuAction(MenuItem item, SupperMenuController listener) {
        this.mListener = listener;
        menuTag = item;
        mType = TYPE_MENU;
        mIsSelected = item.getPosition() == MxSettings.sLauncherEffect;
        applyFromMenuItem(item);
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

}
