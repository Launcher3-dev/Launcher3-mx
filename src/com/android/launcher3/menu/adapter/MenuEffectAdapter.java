package com.android.launcher3.menu.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.android.launcher3.menu.SupperMenuController;
import com.android.launcher3.menu.bean.MenuItem;
import com.android.launcher3.menu.imp.IMenuAdapter;
import com.android.launcher3.menu.view.HorizontalPageScrollView;
import com.android.launcher3.menu.view.MenuItemView;
import com.android.launcher3.setting.MxSettings;

import java.util.List;

/**
 * Created by yuchuan on 2018/4/4.
 */

public class MenuEffectAdapter extends BaseMenuAdapter implements IMenuAdapter<MenuItem> {

    private int mSelectPosition;
    private View mContainer;
    private SupperMenuController mMenuController;

    public MenuEffectAdapter(Context context, SupperMenuController controller) {
        this(context, 0);
        this.mMenuController = controller;
    }

    public MenuEffectAdapter(@NonNull Context context, @LayoutRes int resource) {
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
        if (item != null && item.getPosition() == MxSettings.sLauncherEffect) {
            mSelectPosition = item.getId();
        }
        return view;
    }

    public void setSelected(MenuItem item) {
        int old = mSelectPosition;
        mSelectPosition = item.getId();
        if (item.getType() == MenuItem.EFFECT) {
            if (old != mSelectPosition) {
                invalidateView(old, false);
                invalidateView(mSelectPosition, true);
            }
        }
    }

    private void invalidateView(int position, boolean select) {
        if (mContainer instanceof HorizontalPageScrollView) {
            HorizontalPageScrollView psv = (HorizontalPageScrollView) mContainer;
            MenuItemView view = (MenuItemView) psv.getChildAt(position);
            view.invalidate(select);
        }
    }

    @Override
    public void setContainer(View view) {

    }


    @Override
    public void addAll(List<MenuItem> list) {
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
