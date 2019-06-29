package com.android.launcher3;

import com.android.mxlibrary.util.XLog;

/**
 * Stores the transition states for convenience.
 */
public class TransitionStates {

    // Raw states
    final boolean oldStateIsNormal;
    final boolean oldStateIsEditing;
    final boolean oldStateIsSpringLoaded;
    final boolean oldStateIsOverview;

    final boolean stateIsNormal;
    final boolean stateIsEditing;
    final boolean stateIsSpringLoaded;
    final boolean stateIsOverview;

    // Convenience members
    final boolean workspaceToOverview;
    final boolean overviewToWorkspace;
    final boolean workspaceToEditing;
    final boolean editingToWorkspace;
    final boolean workspaceToSpringLoaded;
    final boolean springLoadedToEditing;


    public TransitionStates(final LauncherState fromState, final LauncherState toState) {
        XLog.e(XLog.getTag(), XLog.TAG_GU + "fromState:  " + fromState.containerType + "  ---   toState:  " + toState.containerType);
        oldStateIsNormal = (fromState == LauncherState.NORMAL);
        oldStateIsEditing = (fromState == LauncherState.EDITING);
        oldStateIsSpringLoaded = (fromState == LauncherState.SPRING_LOADED);
        oldStateIsOverview = (fromState == LauncherState.OVERVIEW);

        stateIsNormal = (toState == LauncherState.NORMAL);
        stateIsEditing = (toState == LauncherState.EDITING);
        stateIsSpringLoaded = (toState == LauncherState.SPRING_LOADED);
        stateIsOverview = (toState == LauncherState.OVERVIEW);

        workspaceToOverview = (oldStateIsNormal && stateIsOverview);
        overviewToWorkspace = (oldStateIsOverview && stateIsNormal);
        workspaceToEditing = (oldStateIsNormal && stateIsEditing);
        editingToWorkspace = (oldStateIsEditing && stateIsNormal);
        workspaceToSpringLoaded = (oldStateIsNormal && stateIsSpringLoaded);
        springLoadedToEditing = (oldStateIsSpringLoaded && stateIsEditing);
    }

}
