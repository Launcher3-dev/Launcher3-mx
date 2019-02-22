package com.android.launcher3.menu;

import android.content.Context;
import android.view.View;

import com.android.launcher3.R;
import com.android.launcher3.menu.adapter.MenuWidgetMinorAdapter;
import com.android.launcher3.menu.view.MenuLayout;
import com.android.launcher3.menu.view.MenuWidgetItemView;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.WidgetListRowEntry;

import java.util.List;

/**
 * Created by CodeMX
 * DATE 2018/4/4
 * TIME 15:42
 */

public class MenuWidgetMinorController extends SupperMenuController<WidgetItem> {

    private WidgetListRowEntry entry;

    public MenuWidgetMinorController(Context context, MenuLayout menuLayout) {
        super(context, menuLayout);
        mMenuAdapter = new MenuWidgetMinorAdapter(context, this, null);
    }

    public void setEntry(WidgetListRowEntry entry) {
        this.entry = entry;
        loadAdapter();
    }

    @Override
    public void loadContainer() {
        mScrollView = mMenuLayout.findViewById(R.id.menu_widget_list);
    }

    @Override
    public void loadAdapter() {
        List<WidgetItem> widgets = mLauncher.getPopupDataProvider().getWidgetsForPackageUser(
                new PackageUserKey(
                        entry.pkgItem.getTargetComponent().getPackageName(),
                        entry.pkgItem.user));
        mMenuAdapter.addAll(widgets);
    }

    @Override
    public void loadMenuList() {
        mMenuLayout.setState(MenuLayout.State.WIDGET_LIST);
        mScrollView = mMenuLayout.getMenuWidgetListLayout();
        mScrollView.setVisibility(View.VISIBLE);
        mScrollView.setAlpha(1.0f);
        mScrollView.removeAllViews();
        mScrollView.setAdapter(mMenuAdapter);
    }

    @Override
    public void showView() {
        super.show(mMenuLayout.getState(), MenuLayout.State.WIDGET_LIST, true);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof MenuWidgetItemView) {
            Object o = ((MenuWidgetItemView) v).getMenuTag();
            if (o instanceof WidgetItem) {
                PendingAddWidgetInfo info = new PendingAddWidgetInfo(((WidgetItem) o).widgetInfo);
                mLauncher.getWorkspace().bindWidget(info);
            }
        }
    }

    @Override
    public void onLongClick(View v) {
        if (v instanceof MenuWidgetItemView) {
            Object o = ((MenuWidgetItemView) v).getMenuTag();
            if (o instanceof WidgetItem) {
                beginDragging(((MenuWidgetItemView) v).getWidget(), v);
            }
        }
    }
}
