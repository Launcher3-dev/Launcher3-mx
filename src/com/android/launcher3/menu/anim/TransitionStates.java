package com.android.launcher3.menu.anim;

import com.android.launcher3.menu.view.MenuLayout;
import com.android.mxlibrary.util.XLog;

public class TransitionStates {

    final boolean oldStateIsNone;
    final boolean oldStateIsMenu;
    final boolean oldStateIsWidget;
    final boolean oldStateIsWidgetList;
    final boolean oldStateIsEffect;

    final boolean stateIsNone;
    final boolean stateIsMenu;
    final boolean stateIsWidget;
    final boolean stateIsWidgetList;
    final boolean stateIsEffect;

    final boolean noneToMenu;
    final boolean menuToNone;
    final boolean menuToWidget;
    final boolean widgetToMenu;
    final boolean menuToEffect;
    final boolean effectToMenu;
    final boolean widgetToWidgetList;
    final boolean widgetListToWidget;


    public TransitionStates(final MenuLayout.State fromState, final MenuLayout.State toState) {

        XLog.e(XLog.getTag(), XLog.TAG_GU + "  fromState==  " + fromState + "  toState==  " + toState);
        oldStateIsNone = (fromState == MenuLayout.State.NONE);
        oldStateIsMenu = (fromState == MenuLayout.State.MENU);
        oldStateIsWidget = (fromState == MenuLayout.State.WIDGET);
        oldStateIsWidgetList = (fromState == MenuLayout.State.WIDGET_LIST);
        oldStateIsEffect = (fromState == MenuLayout.State.EFFECT);

        stateIsNone = (toState == MenuLayout.State.NONE);
        stateIsMenu = (toState == MenuLayout.State.MENU);
        stateIsWidget = (toState == MenuLayout.State.WIDGET);
        stateIsWidgetList = (toState == MenuLayout.State.WIDGET_LIST);
        stateIsEffect = (toState == MenuLayout.State.EFFECT);

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
