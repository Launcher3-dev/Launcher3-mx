package com.android.launcher3.menu.controller;

import android.content.Context;
import android.view.View;

import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.compat.AlphabeticIndexCompat;
import com.android.launcher3.menu.adapter.MenuWidgetAdapter;
import com.android.launcher3.menu.imp.IMenuWidgetPreviewLoader;
import com.android.launcher3.menu.view.MenuLayout;
import com.android.launcher3.menu.view.MenuWidgetItemView;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.WidgetListRowEntry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by CodeMX
 * DATE 2018/4/4
 * TIME 15:42
 */
public class MenuWidgetController extends SupperMenuController implements IMenuWidgetPreviewLoader {

    private final WidgetPreviewLoader mWidgetPreviewLoader;
    private final AlphabeticIndexCompat mIndexer;
    private ArrayList<WidgetListRowEntry> mAllWidgets;

    public MenuWidgetController(Context context, MenuLayout menuLayout) {
        super(context, menuLayout);
        mMenuAdapter = new MenuWidgetAdapter(context, this, this);
        mWidgetPreviewLoader = LauncherAppState.getInstance(context).getWidgetCache();
        mIndexer = new AlphabeticIndexCompat(context);
    }

    public void setWidgets(ArrayList<WidgetListRowEntry> allWidgets) {
        if (allWidgets == null || allWidgets.size() == 0) {
            return;
        }
        if (mAllWidgets == null) {
            mAllWidgets = new ArrayList<>();
        } else {
            mAllWidgets.clear();
        }
        mAllWidgets = allWidgets;
    }

    @Override
    public void loadContainer() {
        mScrollView = mMenuLayout.findViewById(R.id.menu_widget_effect);
    }

    @Override
    public void loadAdapter() {
        mMenuAdapter.addAll(mAllWidgets);
    }

    @Override
    public void loadMenuList() {
        mMenuLayout.setState(MenuLayout.State.WIDGET);
        mScrollView = mMenuLayout.getMenuWidgetAndEffectLayout();
        mScrollView.setVisibility(View.VISIBLE);
        mScrollView.setAlpha(1.0f);
        mScrollView.setAdapter(mMenuAdapter);
    }

    @Override
    public void showView() {
        super.show(mMenuLayout.getState(), MenuLayout.State.WIDGET, true);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof MenuWidgetItemView) {
            Object o = ((MenuWidgetItemView) v).getMenuTag();
            if (o instanceof PackageItemInfo) {
                if (((PackageItemInfo) o).childCount > 1) {// 同一个App有多个Widget，需要显示二级列表
                    WidgetListRowEntry entry = getWidgetItemsForSameApp((PackageItemInfo) o);
                    onMinorWidgetListExpend(entry);
                }
            } else if (o instanceof WidgetItem) {
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

    @Override
    public WidgetPreviewLoader getWidgetPreviewLoader() {
        return mWidgetPreviewLoader;
    }

    @Override
    public void onMinorWidgetListExpend(WidgetListRowEntry rowEntry) {
        if (rowEntry != null) {
            showMinorWidgetList(rowEntry);
        }
    }

    public void showMinorWidgetList(WidgetListRowEntry entry) {
        mController.showMinorWidgetList(entry);
    }

    public WidgetListRowEntry getWidgetItemsForSameApp(PackageItemInfo info) {
        if (mAllWidgets == null || mAllWidgets.isEmpty()) {
            return null;
        }
        for (WidgetListRowEntry entry : mAllWidgets) {
            if (entry.pkgItem == info) {
                return entry;
            }
        }
        return null;
    }

    public List<WidgetItem> getWidgetsForPackageUser(PackageUserKey packageUserKey) {
        for (WidgetListRowEntry entry : mAllWidgets) {
            if (entry.pkgItem.packageName.equals(packageUserKey.mPackageName)) {
                ArrayList<WidgetItem> widgets = new ArrayList<>(entry.widgets);
                // Remove widgets not associated with the correct user.
                Iterator<WidgetItem> iterator = widgets.iterator();
                while (iterator.hasNext()) {
                    if (!iterator.next().user.equals(packageUserKey.mUser)) {
                        iterator.remove();
                    }
                }
                return widgets.isEmpty() ? null : widgets;
            }
        }
        return null;
    }

}
