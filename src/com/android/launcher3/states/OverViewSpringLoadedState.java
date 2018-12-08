package com.android.launcher3.states;

import com.android.launcher3.LauncherState;
import com.android.launcher3.userevent.nano.LauncherLogProto;

import static com.android.launcher3.LauncherAnimUtils.OVERVIEW_TRANSITION_MS;

/**
 * 预览-拖拽模式
 * <p>
 * 此模式下，可以通过拖拽图标到右边CellLayout预览视图从而实现快速拖拽图标到指定CellLayout中
 */
public class OverViewSpringLoadedState extends LauncherState {

    /**
     * Launcher 状态构造函数
     *
     * @param id                 状态id
     */
    public OverViewSpringLoadedState(int id) {
        super(id, LauncherLogProto.ContainerType.OVERVIEW_SPRING_LOADED, OVERVIEW_TRANSITION_MS, FLAG_OVERVIEW_UI);
    }
}
