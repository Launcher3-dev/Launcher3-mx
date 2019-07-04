package com.android.launcher3.util;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.android.launcher3.BubbleTextView;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.folder.FolderPagedView;
import com.android.launcher3.widget.LauncherAppWidgetHostView;

public class DrawEditIcons {

    public static void drawFirstInstall(Canvas canvas, View icon, int id) {
        ItemInfo info = (ItemInfo) icon.getTag();
        if (info != null /* && info.unreadNum > 0 */) {
            Resources res = icon.getContext().getResources();
            Drawable unreadBgNinePatchDrawable = (Drawable) res
                    .getDrawable(id);
            int width = unreadBgNinePatchDrawable.getIntrinsicWidth();
            int height = unreadBgNinePatchDrawable.getIntrinsicHeight();

            int unreadBgWidth = width/*(int) (unreadBgNinePatchDrawable.getIntrinsicWidth())*/;//图标太大
            int unreadBgHeight = height/*(int) (unreadBgNinePatchDrawable.getIntrinsicHeight())*/;
            /*if (width == 0 || height == 0) {

				 unreadBgWidth = (int) (unreadBgNinePatchDrawable.getIntrinsicWidth());//图标太大
				 unreadBgHeight = (int) (unreadBgNinePatchDrawable.getIntrinsicHeight());
			}*/
            Rect unreadBgBounds = new Rect(0, 0, unreadBgWidth, unreadBgHeight);
            unreadBgNinePatchDrawable.setBounds(unreadBgBounds);

            int unreadMarginTop = 0;
            int unreadMarginRight = 0;
            if (info instanceof ShortcutInfo) {//如果是应用类型也添加删除图标的位置操作
                if (info.container == (long) LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.hotseat_unread_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.hotseat_unread_margin_right);
                } else if (info.container == (long) LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.workspace_unread_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.workspace_unread_margin_right);
                } else {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.folder_unread_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.folder_unread_margin_right);
                }
            } else if (info instanceof FolderInfo) {
                if (info.container == (long) LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.hotseat_unread_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.hotseat_unread_margin_right);
                } else if (info.container == (long) LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.workspace_unread_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.workspace_unread_margin_right);
                }
            }
            /*Prize--调整首次安装小点的位置--fuqiang--2016-02-27--begin*/
            int unreadBgPosX = icon.getScrollX();
            //+ unreadBgWidth ;
            int unreadBgPosY = icon.getScrollY();//(int) (icon.getScrollY() +icon.getHeight()-unreadBgHeight/2*Launcher.scale-unreadMarginTop-unreadMarginTop/2 - icon.getPaddingLeft() + icon.getPaddingLeft()/8);
            /*Prize--调整首次安装小点的位置--fuqiang--2016-02-27--end*/

            canvas.save();
            canvas.translate(unreadBgPosX, unreadBgPosY);
            drawCenterLeftDrawable(canvas, height, width, (TextView) icon);
            unreadBgNinePatchDrawable.draw(canvas);
            canvas.restore();
        }
    }

    public static void drawCenterLeftDrawable(Canvas canvas, int h, int w, TextView v) {
        Drawable[] drawables = v.getCompoundDrawables();
        if (drawables != null) {
            Drawable drawableTop = drawables[1];
            if (drawableTop != null) {
                float textWidth = v.getPaint().measureText(v.getText().toString());
                int maxWidth = v.getWidth() - v.getPaddingLeft() - v.getPaddingRight() - w / 2;
                textWidth = Math.min(maxWidth, textWidth);
                float left = (v.getWidth() - textWidth) / 2 - w;
                int y = v.getPaddingTop() + drawableTop.getIntrinsicHeight() + v.getCompoundDrawablePadding();
                int textHeight = v.getHeight() - v.getPaddingTop() - drawableTop.getIntrinsicHeight() - v.getCompoundDrawablePadding();
                int top = y + (textHeight - h) / 2;
                canvas.translate(left, top);
            }
        }
    }

    public static void drawUninstallIcon(Canvas canvas, BubbleTextView icon, Drawable d, float percent) {
        ItemInfo info = (ItemInfo) icon.getTag();
        if (info != null) {
            Resources res = icon.getResources();
            int uninstallIconLeft = 0;
            int uninstallIconTop = 0;
            if (icon.getParent().getParent().getParent() instanceof FolderPagedView) {
                uninstallIconLeft = res.getDimensionPixelSize(R.dimen.uninstall_icon_folder_margin_left);
                uninstallIconTop = res.getDimensionPixelSize(R.dimen.uninstall_icon_folder_margin_top);
            } else {
                uninstallIconLeft = res.getDimensionPixelSize(R.dimen.uninstall_icon_margin_left);
                uninstallIconTop = res.getDimensionPixelSize(R.dimen.uninstall_icon_margin_top);
            }

            Rect rect = new Rect();
            icon.getDrawingRect(rect);
            Drawable[] compoundDrawables = icon.getCompoundDrawables();
            Drawable compoundDrawable = compoundDrawables[1];

            if (compoundDrawable != null) {
                Rect bounds = compoundDrawable.getBounds();
                int iconLeft = rect.centerX() - bounds.width() / 2 + uninstallIconLeft;
                int iconTop = rect.centerY() - rect.height() / 2 + uninstallIconTop;
                d.setBounds(iconLeft, iconTop, iconLeft
                                + (int) (d.getIntrinsicWidth() * percent * 0.8f),
                        iconTop + (int) (d.getIntrinsicHeight() * percent * 0.8f));
                d.draw(canvas);
            }
            d.setAlpha((int) (percent * 255));
            d.draw(canvas);
        }
    }

    public static void drawStateIcon(Canvas canvas, View icon, Drawable d, float percent) {
        ItemInfo info = (ItemInfo) icon.getTag();
        if (info != null /* && info.unreadNum > 0 */) {
            Resources res = icon.getContext().getResources();
            int unreadBgWidth = d.getIntrinsicWidth();/*(int) (unreadBgNinePatchDrawable.getIntrinsicWidth())*///图标太大
            int unreadBgHeight = d.getIntrinsicHeight();/*(int) (unreadBgNinePatchDrawable.getIntrinsicHeight())*/
            Rect unreadBgBounds = new Rect(0, 0, unreadBgWidth, unreadBgHeight);
            d.setBounds(unreadBgBounds);

            int unreadMarginTop = 0;
            int unreadMarginRight = 0;
            if (info instanceof ShortcutInfo) {//如果是应用类型也添加删除图标的位置操作
                if (info.container == (long) LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.hotseat_uninstall_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.hotseat_uninstall_margin_right);
                } else if (info.container == (long) LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.workspace_uninstall_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.workspace_uninstall_margin_right);
                } else {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.folder_uninstall_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.folder_uninstall_margin_right);
                }
            } else if (info instanceof FolderInfo) {
                if (info.container == (long) LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.hotseat_uninstall_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.hotseat_uninstall_margin_right);
                } else if (info.container == (long) LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.workspace_uninstall_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.workspace_uninstall_margin_right);
                }
            }

            int unreadBgPosX = icon.getScrollX()
                    + unreadBgWidth - unreadMarginRight;
            if (icon instanceof LauncherAppWidgetHostView) {
                unreadBgPosX = icon.getScrollX();
            }
            int unreadBgPosY = icon.getScrollY() + unreadMarginTop;

            canvas.save();
            canvas.translate(unreadBgPosX, unreadBgPosY);
            canvas.scale(percent, percent, unreadBgWidth / 2, unreadBgHeight / 2);
            d.setAlpha((int) (percent * 255));
            d.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * 绘画批处理图标勾选状态
     *
     * @param canvas
     * @param icon
     * @param id
     */
    public static void drawStateIconForBatch(Canvas canvas, View icon, int id, int w, int h, double percent) {
        ItemInfo info = (ItemInfo) icon.getTag();
        if (info != null /* && info.unreadNum > 0 */) {
            Resources res = icon.getContext().getResources();
            Drawable unreadBgNinePatchDrawable = (Drawable) res
                    .getDrawable(id);
            int unreadBgWidth = w;//(int) (unreadBgNinePatchDrawable.getIntrinsicWidth()/1.8f);//图标太大
            int unreadBgHeight = h;// (int) (unreadBgNinePatchDrawable.getIntrinsicHeight()/1.8f);
            Rect unreadBgBounds = new Rect(0, 0, unreadBgWidth, unreadBgHeight);
            unreadBgNinePatchDrawable.setBounds(unreadBgBounds);

            int unreadMarginTop = 0;
            int unreadMarginRight = 0;
            if (info instanceof ShortcutInfo) {//如果是应用类型也添加删除图标的位置操作
                if (info.container == (long) LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.hotseat_unread_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.hotseat_unread_margin_right);
                } else if (info.container == (long) LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.workspace_unread_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.workspace_unread_margin_right);
                } else {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.folder_unread_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.folder_unread_margin_right);
                }
            } else if (info instanceof FolderInfo) {
                if (info.container == (long) LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.hotseat_unread_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.hotseat_unread_margin_right);
                } else if (info.container == (long) LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                    unreadMarginTop = (int) res
                            .getDimension(R.dimen.workspace_unread_margin_top);
                    unreadMarginRight = (int) res
                            .getDimension(R.dimen.workspace_unread_margin_right);
                }
            }

            int unreadBgPosX = icon.getScrollX() + icon.getWidth()
                    - unreadBgWidth - unreadMarginRight;
            int unreadBgPosY = icon.getScrollY() + unreadMarginTop;

            canvas.save();
            canvas.translate(unreadBgPosX, unreadBgPosY);

            unreadBgNinePatchDrawable.setAlpha((int) (percent * 255));
            unreadBgNinePatchDrawable.draw(canvas);
            canvas.restore();
        }
    }

}
