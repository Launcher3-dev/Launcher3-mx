/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.launcher3.model;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Pair;

import com.android.launcher3.AllAppsList;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherModel.CallbackTask;
import com.android.launcher3.LauncherModel.Callbacks;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Task to add auto-created workspace items.
 */
public class AddWorkspaceItemsTask extends BaseModelUpdateTask {

    private final List<Pair<ItemInfo, Object>> mItemList;

    /**
     * @param itemList items to add on the workspace
     */
    public AddWorkspaceItemsTask(List<Pair<ItemInfo, Object>> itemList) {
        mItemList = itemList;
    }

    @Override
    public void execute(LauncherAppState app, BgDataModel dataModel, AllAppsList apps) {
        if (mItemList.isEmpty()) {
            return;
        }
        Context context = app.getContext();

        final ArrayList<ItemInfo> addedItemsFinal = new ArrayList<>();
        final ArrayList<Long> addedWorkspaceScreensFinal = new ArrayList<>();

        // Get the list of workspace screens.  We need to append to this list and
        // can not use sBgWorkspaceScreens because loadWorkspace() may not have been
        // called.
        ArrayList<Long> workspaceScreens = LauncherModel.loadWorkspaceScreensDb(context);
        synchronized (dataModel) {

            List<ItemInfo> filteredItems = new ArrayList<>();
            for (Pair<ItemInfo, Object> entry : mItemList) {
                ItemInfo item = entry.first;
                if (item == null) {
                    continue;
                }
                if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION ||
                        item.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT) {
                    // Short-circuit this logic if the icon exists somewhere on the workspace
                    if (shortcutExists(dataModel, item.getIntent(), item.user)) {
                        continue;
                    }
                }

                filteredItems.add(item);
            }

            for (ItemInfo item : filteredItems) {
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

                // Add the shortcut to the db
                getModelWriter().addItemToDatabase(itemInfo,
                        LauncherSettings.Favorites.CONTAINER_DESKTOP, screenId,
                        cordinates[0], cordinates[1]);

                // Save the ShortcutInfo for binding in the workspace
                addedItemsFinal.add(itemInfo);
            }
        }

        // Update the workspace screens
        updateScreens(context, workspaceScreens);

        if (!addedItemsFinal.isEmpty()) {
            scheduleCallbackTask(new CallbackTask() {
                @Override
                public void execute(Callbacks callbacks) {
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



    /**
     * Returns true if the shortcuts already exists on the workspace. This must be called after
     * the workspace has been loaded. We identify a shortcut by its intent.
     */
    protected boolean shortcutExists(BgDataModel dataModel, Intent intent, UserHandle user) {
        final String compPkgName, intentWithPkg, intentWithoutPkg;
        if (intent == null) {
            // Skip items with null intents
            return true;
        }
        if (intent.getComponent() != null) {
            // If component is not null, an intent with null package will produce
            // the same result and should also be a match.
            compPkgName = intent.getComponent().getPackageName();
            if (intent.getPackage() != null) {
                intentWithPkg = intent.toUri(0);
                intentWithoutPkg = new Intent(intent).setPackage(null).toUri(0);
            } else {
                intentWithPkg = new Intent(intent).setPackage(compPkgName).toUri(0);
                intentWithoutPkg = intent.toUri(0);
            }
        } else {
            compPkgName = null;
            intentWithPkg = intent.toUri(0);
            intentWithoutPkg = intent.toUri(0);
        }

        boolean isLauncherAppTarget = Utilities.isLauncherAppTarget(intent);
        synchronized (dataModel) {
            for (ItemInfo item : dataModel.itemsIdMap) {
                if (item instanceof ShortcutInfo) {
                    ShortcutInfo info = (ShortcutInfo) item;
                    if (item.getIntent() != null && info.user.equals(user)) {
                        Intent copyIntent = new Intent(item.getIntent());
                        copyIntent.setSourceBounds(intent.getSourceBounds());
                        String s = copyIntent.toUri(0);
                        if (intentWithPkg.equals(s) || intentWithoutPkg.equals(s)) {
                            return true;
                        }

                        // checking for existing promise icon with same package name
                        if (isLauncherAppTarget
                                && info.isPromise()
                                && info.hasStatusFlag(ShortcutInfo.FLAG_AUTOINSTALL_ICON)
                                && info.getTargetComponent() != null
                                && compPkgName != null
                                && compPkgName.equals(info.getTargetComponent().getPackageName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
