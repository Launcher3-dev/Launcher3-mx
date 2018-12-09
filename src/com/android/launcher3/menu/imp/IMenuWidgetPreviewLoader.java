package com.android.launcher3.menu.imp;

import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.widget.WidgetListRowEntry;

/**
 * Created by yuchuan on 2018/4/4.
 */

public interface IMenuWidgetPreviewLoader {

    WidgetPreviewLoader getWidgetPreviewLoader();

    void onMinorWidgetListExpend(WidgetListRowEntry rowEntry);

}
