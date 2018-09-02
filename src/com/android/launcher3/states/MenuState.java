package com.android.launcher3.states;

import com.android.launcher3.LauncherState;
import com.android.launcher3.userevent.nano.LauncherLogProto;

import static com.android.launcher3.LauncherAnimUtils.MENU_TRANSITION_MS;

public class MenuState extends LauncherState {
    public MenuState(int id) {
        super(id, LauncherLogProto.ContainerType.MENU, MENU_TRANSITION_MS, FLAG_DISABLE_RESTORE);
    }
}
