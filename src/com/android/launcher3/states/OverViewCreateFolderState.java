package com.android.launcher3.states;

import com.android.launcher3.LauncherState;
import com.android.launcher3.userevent.nano.LauncherLogProto;

import static com.android.launcher3.LauncherAnimUtils.OVERVIEW_TRANSITION_MS;

/**
 * 批量选择创建文件夹
 * <p>
 * 不可拖拽
 * <p>
 * 从预览模式进入，进入创建文件夹状态后，可以多选然后点击确定创建文件夹后可以选择屏幕放置要创建的文件夹，
 * 此时其他的功能不能用
 */
public class OverViewCreateFolderState extends LauncherState {

    /**
     * Launcher 状态构造函数
     *
     * @param id 状态id
     */
    public OverViewCreateFolderState(int id) {
        super(id, LauncherLogProto.ContainerType.OVERVIEW_CREATE_FOLDER, OVERVIEW_TRANSITION_MS, FLAG_OVERVIEW_UI);
    }
}
