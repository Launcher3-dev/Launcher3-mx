package com.android.launcher3.menu.anim;

import com.android.launcher3.menu.view.MenuLayout;
import com.android.mxlibrary.util.XLog;

class TransitionStates {

    final boolean noneToMenu;
    final boolean menuToNone;
    final boolean menuToWidget;
    final boolean widgetToMenu;
    final boolean menuToEffect;
    final boolean effectToMenu;
    final boolean widgetToWidgetList;
    final boolean widgetListToWidget;


    TransitionStates(final MenuLayout.State fromState, final MenuLayout.State toState) {

        XLog.e(XLog.getTag(), XLog.TAG_GU + "  fromState==  " + fromState + "  toState==  " + toState);
        boolean oldStateIsNone = (fromState == MenuLayout.State.NONE);
        boolean oldStateIsMenu = (fromState == MenuLayout.State.MENU);
        boolean oldStateIsWidget = (fromState == MenuLayout.State.WIDGET);
        boolean oldStateIsWidgetList = (fromState == MenuLayout.State.WIDGET_LIST);
        boolean oldStateIsEffect = (fromState == MenuLayout.State.EFFECT);

        boolean stateIsNone = (toState == MenuLayout.State.NONE);
        boolean stateIsMenu = (toState == MenuLayout.State.MENU);
        boolean stateIsWidget = (toState == MenuLayout.State.WIDGET);
        boolean stateIsWidgetList = (toState == MenuLayout.State.WIDGET_LIST);
        boolean stateIsEffect = (toState == MenuLayout.State.EFFECT);

        noneToMenu = (oldStateIsNone && stateIsMenu);
        menuToNone = (oldStateIsMenu && stateIsNone);
        menuToWidget = (oldStateIsMenu && stateIsWidget);
        widgetToMenu = (oldStateIsWidget && stateIsMenu);
        menuToEffect = (oldStateIsMenu && stateIsEffect);
        effectToMenu = (oldStateIsEffect && stateIsMenu);
        widgetToWidgetList = (oldStateIsWidget && stateIsWidgetList);
        widgetListToWidget = (oldStateIsWidgetList && stateIsWidget);

    }

}
