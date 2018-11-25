package com.android.launcher3.states;

import com.android.launcher3.LauncherState;
import com.android.launcher3.userevent.nano.LauncherLogProto;

import static com.android.launcher3.LauncherAnimUtils.OVERVIEW_TRANSITION_MS;

/**
 * 剪贴板状态，显示剪贴板列表进入该状态
 * <p>
 * 不可拖拽
 * <p>
 * 该状态下用来管理街切板数据列表，或者复制剪贴板数据，
 */
public class OverViewClipState extends LauncherState {

    /**
     * Launcher 状态构造函数
     *
     * @param id 状态id
     */
    public OverViewClipState(int id) {
        super(id, LauncherLogProto.ContainerType.OVERVIEW_CLIP, OVERVIEW_TRANSITION_MS, FLAG_OVERVIEW_UI);
    }
}
