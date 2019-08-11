package com.android.launcher3.menu;

import com.android.launcher3.R;
import com.android.launcher3.effect.TransitionEffect;
import com.android.launcher3.menu.bean.MenuItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by CodeMX
 * DATE 2018/1/16
 * TIME 11:51
 */

public final class MenuDataModel {

    private static final int[] MENU_ID =
            {R.drawable.setting,
                    R.drawable.theme,
                    R.drawable.wallpapaer,
                    R.drawable.widget,
                    R.drawable.effect};

    private static final int[] MENU_TITLE =
            {R.string.menu_setting_title,
                    R.string.menu_theme_title,
                    R.string.menu_wallpaper_title,
                    R.string.menu_widget_title,
                    R.string.menu_effect_title};

    public static List<MenuItem> getMenuItemList() {
        List<MenuItem> list = new ArrayList<>();
        for (int i = 0; i < MENU_ID.length; i++) {
            MenuItem item = new MenuItem();
            item.setId(i);
            item.setIcon(MENU_ID[i]);
            item.setTitleId(MENU_TITLE[i]);
            item.setPosition(i);
            list.add(item);
        }
        return list;
    }

    private static final int[] MENU_EFFECT_ID = {
            R.drawable.selector_effect_noraml,
            R.drawable.selector_effect_cross,
            R.drawable.selector_effect_scale,
            R.drawable.selector_effect_cube_in,
            R.drawable.selector_effect_cube_out,
            R.drawable.selector_effect_windmill};

    private static final int[] MENU_EFFECT_TITLE = {R.string.menu_effect_0, R.string.menu_effect_1,
            R.string.menu_effect_2, R.string.menu_effect_3, R.string.menu_effect_4, R.string.menu_effect_5};

    private static final int[] MENU_EFFECT_POSITION = {
            TransitionEffect.TRANSITION_EFFECT_NONE,
            TransitionEffect.TRANSITION_EFFECT_STACK,
            TransitionEffect.TRANSITION_EFFECT_OVERVIEW_SCALE,
            TransitionEffect.TRANSITION_EFFECT_ZOOM_IN,
            TransitionEffect.TRANSITION_EFFECT_CUBE_OUT,
            TransitionEffect.TRANSITION_EFFECT_WINDMILL_DOWN};

    public static List<MenuItem> getEffectList() {
        List<MenuItem> list = new ArrayList<>();
        for (int i = 0; i < MENU_EFFECT_ID.length; i++) {
            MenuItem item = new MenuItem();
            item.setId(i);
            item.setIcon(MENU_EFFECT_ID[i]);
            item.setTitleId(MENU_EFFECT_TITLE[i]);
            item.setType(MenuItem.EFFECT);
            item.setPosition(MENU_EFFECT_POSITION[i]);
            list.add(item);
        }
        return list;
    }

}
