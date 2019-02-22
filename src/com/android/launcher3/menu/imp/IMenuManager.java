package com.android.launcher3.menu.imp;

import com.android.launcher3.menu.bean.MenuItem;

import java.util.List;

/**
 * Created by CodeMX
 * DATE 2018/1/15
 * TIME 11:03
 */

public interface IMenuManager {

    /**
     * 更新数据
     *
     * @param list 数据列表
     */
    void updateDataList(List<MenuItem> list);

    void snapToLeftPage();

    void snapToRightPage();

}
