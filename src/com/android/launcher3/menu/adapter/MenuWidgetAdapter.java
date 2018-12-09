package com.android.launcher3.menu.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.android.launcher3.menu.SupperMenuController;
import com.android.launcher3.menu.imp.IMenuAdapter;
import com.android.launcher3.menu.imp.IMenuWidgetPreviewLoader;
import com.android.launcher3.menu.view.HorizontalPageScrollView;
import com.android.launcher3.menu.view.MenuWidgetItemView;
import com.android.launcher3.widget.WidgetListRowEntry;

import java.util.List;

/**
 * Created by CodeMX
 * DATE 2018/1/31
 * TIME 15:09
 */

public class MenuWidgetAdapter extends BaseMenuAdapter<WidgetListRowEntry>
        implements IMenuAdapter<WidgetListRowEntry> {

    private SupperMenuController mMenuController;
    private IMenuWidgetPreviewLoader mLoader;

    public MenuWidgetAdapter(Context context, SupperMenuController controller, IMenuWidgetPreviewLoader loader) {
        this(context, 0);
        this.mMenuController = controller;
        this.mLoader = loader;
    }

    public MenuWidgetAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = createShortcut(parent);
        }
        MenuWidgetItemView view = (MenuWidgetItemView) convertView;
        WidgetListRowEntry entry = (WidgetListRowEntry) getItem(position);
        if (entry != null) {
            if (entry.widgets.size() > 1) {
                view.setMenuWidgetAction(entry, position, mMenuController, null);
            } else {
                view.setMenuWidgetAction(entry, 0, mMenuController, mLoader);
            }
        }
        return view;
    }

    @Override
    public void setContainer(View container) {
    }

    @Override
    public void addAll(List<WidgetListRowEntry> list) {
        clear();
        addAll(list);
    }

    @Override
    public void setContainer(HorizontalPageScrollView container) {

    }

    @Override
    public int getMenuItemCount() {
        return getCount();
    }

    @Override
    public View getChildView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

}
