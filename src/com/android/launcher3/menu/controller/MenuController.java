package com.android.launcher3.menu.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.android.launcher3.R;
import com.android.launcher3.menu.anim.MenuStateTransitionAnimation;
import com.android.launcher3.menu.bean.MenuItem;
import com.android.launcher3.menu.view.MenuItemView;
import com.android.launcher3.menu.view.MenuLayout;
import com.android.launcher3.widget.WidgetListRowEntry;

import java.util.ArrayList;


/**
 * Created by CodeMX
 * DATE 2018/1/16
 * TIME 11:51
 */
public final class MenuController extends SupperMenuController implements View.OnClickListener {

    private Context mContext;
    private MenuLayout mMenuLayout;

    private SupperMenuController mMenuController;
    private SupperMenuController mMenuEffectController;
    private SupperMenuController mMenuWidgetController;
    private SupperMenuController mMenuWidgetMinorController;

    public MenuStateTransitionAnimation getMenuTransition() {
        return mMenuTransition;
    }

    private MenuStateTransitionAnimation mMenuTransition;

    public MenuController(Context context, MenuLayout menuLayout) {
        super(context, menuLayout);
        this.mContext = context;
        this.mMenuLayout = menuLayout;
        mMenuTransition = new MenuStateTransitionAnimation(mContext, menuLayout);
        mMenuController = new MenuMainController(context, menuLayout);
        mMenuEffectController = new MenuEffectController(context, menuLayout);
        mMenuWidgetController = new MenuWidgetController(context, menuLayout);
        mMenuWidgetMinorController = new MenuWidgetMinorController(context, menuLayout);
    }

    @Override
    public void loadContainer() {
        mMenuController.loadContainer();
    }

    @Override
    public void loadAdapter() {
        mMenuController.loadAdapter();
    }

    public void loadMenuList() {
        mMenuController.loadMenuList();
    }

    @Override
    public void showView() {
        mMenuController.showView();
    }

    @Override
    public void onClick(View view) {
        Object o = ((MenuItemView) view).getMenuTag();
        if (o instanceof MenuItem) {
            final int type = ((MenuItem) o).getType();
            switch (type) {
                case MenuItem.WIDGET:
                    mMenuController = mMenuWidgetController;
                    break;
                case MenuItem.EFFECT:
                    mMenuController = mMenuEffectController;
                    break;
                case MenuItem.THEME:
//                  mMenuController = mMenuThemeController;
                    break;
                case MenuItem.WALLPAPER:
                    break;
                default:
                    break;
            }

        }
        mMenuController.showView();
    }


    @Override
    public void onLongClick(View v) {

    }

    /**
     * 设置子View的分步动画
     *
     * @param viewGroup 父View
     */
    public void setLayoutAnimation(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(viewGroup.getContext(), R.anim.anim_menu_in);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        controller.setDelay(0.3f);
        viewGroup.setLayoutAnimation(controller);
        // 在添加完成后调用
//        viewGroup.startLayoutAnimation();
    }

    public void clearLayoutAnimation(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        viewGroup.setLayoutAnimation(null);
    }

    public void setWidgets(ArrayList<WidgetListRowEntry> allWidgets) {
        ((MenuWidgetController) mMenuWidgetController).setWidgets(allWidgets);
    }

    public MenuLayout getMenuLayout() {
        return mMenuLayout;
    }

    public void showMinorWidgetList(WidgetListRowEntry entry) {
        ((MenuWidgetMinorController) mMenuWidgetMinorController).setEntry(entry);
        mMenuWidgetMinorController.showView();
    }
}
