package com.android.launcher3.states;

import com.android.launcher3.LauncherState;
import com.android.launcher3.userevent.nano.LauncherLogProto;

import static com.android.launcher3.LauncherAnimUtils.SPRING_LOADED_TRANSITION_MS;

public class EditingState extends LauncherState {

    public EditingState(int id) {
        super(id, LauncherLogProto.ContainerType.EDITING, SPRING_LOADED_TRANSITION_MS, FLAG_DISABLE_RESTORE);
    }
}
