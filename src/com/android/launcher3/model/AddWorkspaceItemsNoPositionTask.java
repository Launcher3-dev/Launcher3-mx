package com.android.launcher3.model;

import android.content.Context;
import android.util.Pair;

import com.android.launcher3.AllAppsList;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.util.XLog;

import java.util.ArrayList;
import java.util.List;

public class AddWorkspaceItemsNoPositionTask extends BaseModelUpdateTask {

    private List<ShortcutInfo> mAllAppsNoPotion;

    public AddWorkspaceItemsNoPositionTask(List<ShortcutInfo> allAppsNoPotion) {
        this.mAllAppsNoPotion = allAppsNoPotion;
        setForceExecute(true);
    }

    @Override
    public void execute(LauncherAppState app, BgDataModel dataModel, AllAppsList apps) {
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

        for (ItemInfo item : mAllAppsNoPotion) {
            // Find appropriate space for the item.
            Pair<Long, int[]> coords = findSpaceForItem(app, dataModel, workspaceScreens,
                    addedWorkspaceScreensFinal, item.spanX, item.spanY);
            long screenId = coords.first;
            int[] cordinates = coords.second;

            ItemInfo itemInfo;
            if (item instanceof ShortcutInfo || item instanceof FolderInfo ||
                    item instanceof LauncherAppWidgetInfo) {
                itemInfo = item;
            } else {
                throw new RuntimeException("Unexpected info type");
            }

            // Update the workspace screens
            updateScreens(context, workspaceScreens);

            // Add the shortcut to the db
            getModelWriter().addItemToDatabase(itemInfo,
                    LauncherSettings.Favorites.CONTAINER_DESKTOP, screenId,
                    cordinates[0], cordinates[1]);

            // Save the ShortcutInfo for binding in the workspace
            addedItemsFinal.add(itemInfo);
        }

        if (!addedItemsFinal.isEmpty()) {
            scheduleCallbackTask(new LauncherModel.CallbackTask() {
                @Override
                public void execute(LauncherModel.Callbacks callbacks) {
                    final ArrayList<ItemInfo> addAnimated = new ArrayList<>();
                    final ArrayList<ItemInfo> addNotAnimated = new ArrayList<>();
                    if (!addedItemsFinal.isEmpty()) {
                        ItemInfo info = addedItemsFinal.get(addedItemsFinal.size() - 1);
                        long lastScreenId = info.screenId;
                        for (ItemInfo i : addedItemsFinal) {
                            if (i.screenId == lastScreenId) {
                                addAnimated.add(i);
                            } else {
                                addNotAnimated.add(i);
                            }
                        }
                    }
                    callbacks.bindAppsAdded(addedWorkspaceScreensFinal,
                            addNotAnimated, addAnimated);
                }
            });
        }
    }

}
