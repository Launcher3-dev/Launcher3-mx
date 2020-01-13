package com.android.launcher3.menu.adapter;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.android.launcher3.menu.controller.SupperMenuController;
import com.android.launcher3.menu.imp.IMenuAdapter;
import com.android.launcher3.menu.imp.IMenuWidgetPreviewLoader;
import com.android.launcher3.menu.view.HorizontalPageScrollView;
import com.android.launcher3.menu.view.MenuWidgetItemView;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.widget.WidgetListRowEntry;

import java.util.List;

/**
 * Created by CodeMX
 * DATE 2018/1/31
 * TIME 15:09
 */

public class MenuWidgetMinorAdapter extends BaseMenuAdapter<WidgetItem>
        implements IMenuAdapter<WidgetItem> {

    private SupperMenuController mMenuController;
    private IMenuWidgetPreviewLoader mLoader;

    public MenuWidgetMinorAdapter(Context context, SupperMenuController controller,
                                  IMenuWidgetPreviewLoader loader) {
        this(context, 0);
        this.mMenuController = controller;
        this.mLoader = loader;
    }

    public MenuWidgetMinorAdapter(@NonNull Context context, @LayoutRes int resource) {
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
    public void addAllData(List<WidgetItem> list) {
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
