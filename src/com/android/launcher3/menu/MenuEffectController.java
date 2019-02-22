package com.android.launcher3.menu;

import android.content.Context;
import android.view.View;

import com.android.launcher3.R;
import com.android.launcher3.menu.adapter.MenuAdapter;
import com.android.launcher3.menu.adapter.MenuEffectAdapter;
import com.android.launcher3.menu.bean.MenuItem;
import com.android.launcher3.menu.imp.IMenuSelectListener;
import com.android.launcher3.menu.view.MenuItemView;
import com.android.launcher3.menu.view.MenuLayout;

import java.util.List;

/**
 * Created by CodeMX
 * DATE 2018/4/4
 * TIME 15:41
 */

public class MenuEffectController extends SupperMenuController implements IMenuSelectListener {

    public MenuEffectController(Context context, MenuLayout menuLayout) {
        super(context, menuLayout);
        mMenuAdapter = new MenuAdapter(context, this);
    }

    @Override
    public void loadContainer() {
        mMenuLayout.findViewById(R.id.menu_widget_effect);
    }

    @Override
    public void loadAdapter() {
        List<MenuItem> list = MenuDataModel.getEffectList();
        mMenuAdapter.addAll(list);
    }

    @Override
    public void loadMenuList() {
        mMenuLayout.setState(MenuLayout.State.EFFECT);
        mScrollView = mMenuLayout.getMenuWidgetAndEffectLayout();
        mScrollView.setVisibility(View.VISIBLE);
        mScrollView.setAlpha(1.0f);
        mScrollView.setAdapter(mMenuAdapter);
    }

    @Override
    public void showView() {
        super.show(mMenuLayout.getState(), MenuLayout.State.EFFECT, true);
    }

    @Override
    public void onClick(View v) {
        Object o = ((MenuItemView) v).getMenuTag();
        mLauncher.getWorkspace().previewTransitionEffect((MenuItem) o, this);
    }

    @Override
    public void onLongClick(View v) {

    }

    @Override
    public void onSelected(MenuItem item) {
        ((MenuEffectAdapter) mMenuAdapter).setSelected(item);
    }

    public MenuEffectAdapter getAdapter() {
        return (MenuEffectAdapter) mMenuAdapter;
    }
}
