package com.android.launcher3.model;

import android.content.Context;

import com.android.launcher3.AllAppsList;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.util.XLog;

import java.util.ArrayList;
import java.util.List;

public class AddWorkspaceItemsNoPositionTask extends BaseModelUpdateTask {

    private List<ItemInfo> mAllAppsNoPotion;

    public AddWorkspaceItemsNoPositionTask(List<ItemInfo> allAppsNoPotion) {
        this.mAllAppsNoPotion = allAppsNoPotion;
        setForceExecute(true);
    }

    @Override
    public void execute(LauncherAppState app, BgDataModel dataModel, AllAppsList apps) {
        setForceExecute(false);
        if (mAllAppsNoPotion.isEmpty()) {
            return;
        }
        Context context = app.getContext();
        final ArrayList<ItemInfo> addedItemsFinal = new ArrayList<>();
        final ArrayList<Long> addedWorkspaceScreensFinal = new ArrayList<>();

        // Get the list of workspace screens.  We need to append to this list and
        // can not use sBgWorkspaceScreens because loadWorkspace() may not have been
        // called.
        ArrayList<Long> workspaceScreens = LauncherModel.loadWorkspaceScreensDb(context);

        XLog.e(XLog.getTag(),XLog.TAG_GU + mAllAppsNoPotion.size());

        filterAddedItemsFinal(app, dataModel, apps, workspaceScreens, mAllAppsNoPotion, addedWorkspaceScreensFinal, addedItemsFinal);

        // Update the workspace screens
        updateScreens(context, workspaceScreens);

        bindAddedItemsFinal(addedWorkspaceScreensFinal, addedItemsFinal);

    }
}
