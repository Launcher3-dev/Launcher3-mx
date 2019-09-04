package com.android.launcher3.menu.controller;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.menu.CircleMenuView;
import com.android.launcher3.menu.anim.MenuStateTransitionAnimation;
import com.android.launcher3.menu.bean.MenuItem;
import com.android.launcher3.menu.imp.OnMenuClickListener;
import com.android.launcher3.menu.imp.OnMenuLongClickListener;
import com.android.launcher3.menu.view.MenuLayout;
import com.android.launcher3.widget.WidgetListRowEntry;
import com.android.mxlibrary.util.XLog;
import com.android.mxtheme.ThemeActivity;

import java.util.ArrayList;


/**
 * Created by CodeMX
 * DATE 2018/1/16
 * TIME 11:51
 */
public final class MenuController extends SupperMenuController implements OnMenuClickListener,
        OnMenuLongClickListener {

    private SupperMenuController mMenuController;
    private SupperMenuController mMenuEffectController;
    private SupperMenuController mMenuWidgetController;
    private SupperMenuController mMenuWidgetMinorController;

    MenuStateTransitionAnimation getMenuTransition() {
        return mMenuTransition;
    }

    private MenuStateTransitionAnimation mMenuTransition;

    public MenuController(Launcher launcher) {
        super(launcher);
    }

    public void setup(CircleMenuView circleMenuView, MenuLayout menuLayout) {
        this.mMenuLayout = menuLayout;
        this.mCircleMenuView = circleMenuView;
        mMenuLayout.setup(this);
        mCircleMenuView.setMenuController(this, this);
        mMenuTransition = new MenuStateTransitionAnimation(mLauncher, menuLayout);
        mMenuController = mMenuEffectController = new MenuEffectController(mLauncher, menuLayout);
        mMenuWidgetController = new MenuWidgetController(mLauncher, menuLayout);
        mMenuWidgetMinorController = new MenuWidgetMinorController(mLauncher, menuLayout);
        loadAdapter();
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
        mMenuController.onClick(view);
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

    @Override
    public void onMenuClick(View view) {
        Object o = view.getTag();
        XLog.d(XLog.getTag(), XLog.TAG_GU_STATE + o);
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
                    ThemeActivity.startThemeActivity(mLauncher);
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
    public void onMenuLongClick(View view) {

    }
}
