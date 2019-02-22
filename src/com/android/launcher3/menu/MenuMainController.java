package com.android.launcher3.menu;

import android.content.Context;
import android.view.View;

import com.android.launcher3.R;
import com.android.launcher3.menu.adapter.MenuAdapter;
import com.android.launcher3.menu.bean.MenuItem;
import com.android.launcher3.menu.view.MenuLayout;

import java.util.List;

/**
 * Created by CodeMX
 * DATE 2018/4/4
 * TIME 15:35
 */
public class MenuMainController extends SupperMenuController {

    public MenuMainController(Context context, MenuLayout menuLayout) {
        super(context, menuLayout);
        mMenuAdapter = new MenuAdapter(context, this);
    }

    @Override
    public void loadContainer() {
        mScrollView = mMenuLayout.findViewById(R.id.mx_menu);
    }

    @Override
    public void loadAdapter() {
        List<MenuItem> list = MenuDataModel.getMenuItemList();
        mMenuAdapter.addAll(list);
    }

    @Override
    public void loadMenuList() {
        mMenuLayout.setState(MenuLayout.State.MENU);
        mScrollView = mMenuLayout.getMenuListLayout();
        mScrollView.setVisibility(View.VISIBLE);
        mScrollView.setAlpha(1.0f);
        mScrollView.setAdapter(mMenuAdapter);
    }

    @Override
    public void showView() {
        super.show(mMenuLayout.getState(), MenuLayout.State.MENU, true);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onLongClick(View v) {

    }


}
