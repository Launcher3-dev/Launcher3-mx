package com.codemx.floatwindow;

import android.view.View;

/**
 * Created by yhao on 2017/12/22.
 * https://github.com/yhaolpz
 */

public interface IFloatWindow {

    void show();

    void hide();

    boolean isShowing();

    int getX();

    int getY();

    void updateX(int x);

    void updateX(@Screen.screenType int screenType, float ratio);

    void updateY(int y);

    void updateY(@Screen.screenType int screenType, float ratio);

    View getView();

    void dismiss();
}
