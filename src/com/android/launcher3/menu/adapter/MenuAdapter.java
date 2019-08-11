package com.android.launcher3.menu.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.android.launcher3.menu.controller.SupperMenuController;
import com.android.launcher3.menu.bean.MenuItem;
import com.android.launcher3.menu.imp.IMenuAdapter;
import com.android.launcher3.menu.view.HorizontalPageScrollView;
import com.android.launcher3.menu.view.MenuItemView;

import java.util.List;

/**
 * Created by CodeMX
 * DATE 2018/1/16
 * TIME 10:32
 */

public class MenuAdapter extends BaseMenuAdapter<MenuItem> implements IMenuAdapter<MenuItem> {

    private View mContainer;
    private SupperMenuController mMenuController;

    public MenuAdapter(Context context, SupperMenuController controller) {
        this(context, 0);
        this.mMenuController = controller;
    }

    public MenuAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = createShortcut(parent);
        }
        MenuItemView view = (MenuItemView) convertView;

        MenuItem item = (MenuItem) getItem(position);
        view.setMenuAction(item, mMenuController);
        return view;
    }

    @Override
    public void setContainer(View container) {
        mContainer = container;
    }


    @Override
    public void addAll(List<MenuItem> list) {
        addAll(list);
    }

    @Override
    public void setContainer(HorizontalPageScrollView container) {
        this.mContainer = container;
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
