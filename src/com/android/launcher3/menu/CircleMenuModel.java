package com.android.launcher3.menu;

import android.util.SparseArray;

import com.android.launcher3.R;
import com.android.launcher3.menu.bean.MenuItem;

/**
 * 主菜单上数据模型
 */
public final class CircleMenuModel {

    private static final String[] titles = {"设置", "主题", "壁纸", "插件", "特效"};
    private static final int[] icons =
            {R.drawable.setting,
                    R.drawable.theme,
                    R.drawable.wallpapaer,
                    R.drawable.widget,
                    R.drawable.effect};

    private SparseArray<MenuItem> mCircleMenuItemList;

    public CircleMenuModel() {
        mCircleMenuItemList = new SparseArray<>();
    }

    private void initCircleMenuData() {
        int N = titles.length;
        for (int i = 0; i < N; i++) {
            MenuItem item = new MenuItem();
            item.setTitle(titles[i]);
            item.setIcon(icons[i]);
            item.setPosition(i);
            mCircleMenuItemList.put(i, item);
        }
    }

    public SparseArray<MenuItem> getCircleMenuItemList() {
        return mCircleMenuItemList;
    }
}
