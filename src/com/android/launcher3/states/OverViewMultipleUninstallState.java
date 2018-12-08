package com.android.launcher3.states;

import com.android.launcher3.LauncherState;
import com.android.launcher3.userevent.nano.LauncherLogProto;

import static com.android.launcher3.LauncherAnimUtils.OVERVIEW_TRANSITION_MS;

/**
 * 批量卸载状态
 * <p>
 * 不可拖拽
 * <p>
 * 从预览状态进入，批量卸载时可以多选App视图，然后点击确定后开始批量卸载
 */
public class OverViewMultipleUninstallState extends LauncherState {

    /**
     * Launcher 状态构造函数
     *
     * @param id 状态id
     */
    public OverViewMultipleUninstallState(int id) {
        super(id, LauncherLogProto.ContainerType.OVERVIEW_MULTIPLE_UNINSTALL, OVERVIEW_TRANSITION_MS, FLAG_OVERVIEW_UI);
    }
}
