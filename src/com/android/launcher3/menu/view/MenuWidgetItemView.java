package com.android.launcher3.menu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.menu.controller.SupperMenuController;
import com.android.launcher3.menu.imp.IMenuWidgetPreviewLoader;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.widget.WidgetCell;
import com.android.launcher3.widget.WidgetListRowEntry;

import static com.android.launcher3.menu.view.MenuItemView.TYPE_WIDGET_GROUP;
import static com.android.launcher3.menu.view.MenuItemView.TYPE_WIDGET_ITEM;

/**
 * Created by yuchuan on 2018/4/4.
 */

public class MenuWidgetItemView extends BubbleTextView implements View.OnClickListener,
        View.OnLongClickListener {

    private SupperMenuController mListener;
    private IMenuWidgetPreviewLoader mLoader;

    public WidgetCell getWidget() {
        return mWidget;
    }

    private WidgetCell mWidget;
    private int mType;

    public Object getMenuTag() {
        return menuTag;
    }

    private Object menuTag;
    private Launcher mLauncher;

    public MenuWidgetItemView(Context context) {
        this(context, null);
    }

    public MenuWidgetItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuWidgetItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mLauncher = Launcher.getLauncher(context);
        setOnClickListener(this);
        setOnLongClickListener(this);
        setMaxLines(1);
    }

    public void setMenuWidgetAction(WidgetListRowEntry item, int position, SupperMenuController listener, IMenuWidgetPreviewLoader loader) {
        this.mListener = listener;
        this.mLoader = loader;
        if (item.widgets.size() > 1) {
            menuTag = item;
            mType = TYPE_WIDGET_GROUP;
            applyFromPackageItemInfo(item.pkgItem, true);
        } else {
            menuTag = item.widgets.get(position);
            mType = TYPE_WIDGET_ITEM;
            applyFromPackageItemInfo(item, position);
            mWidget = (WidgetCell) LayoutInflater.from(mLauncher).inflate(R.layout.widget_cell, mLauncher.getMenuLayout().getMenuWidgetListLayout(), false);
            mWidget.setX(getX());
            mWidget.setY(getY());
            mWidget.applyFromCellItem(item.widgets.get(position), loader.getWidgetPreviewLoader());
            mWidget.ensurePreview();
        }
    }

    @Override
    public void onClick(View v) {
        if (menuTag instanceof WidgetListRowEntry) {
            if (mLoader != null && mType == TYPE_WIDGET_GROUP) {
                mLoader.onMinorWidgetListExpend((WidgetListRowEntry) menuTag);
            }
        } else if (menuTag instanceof WidgetItem) {
            if (mListener != null && mType == TYPE_WIDGET_ITEM) {
                mListener.onClick(v);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (menuTag instanceof WidgetItem) {
            if (mListener != null && mType == TYPE_WIDGET_ITEM) {
                mListener.onLongClick(v);
            }
        }
        return false;
    }
}
