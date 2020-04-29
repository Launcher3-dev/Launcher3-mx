package com.codemx.floatwindow;

import android.view.View;

/**
 * Created by yhao on 17-11-14.
 * https://github.com/yhaolpz
 */

interface FloatView {

    void setSize(int width, int height);

    void setView(View view);

    void setGravity(int gravity, int xOffset, int yOffset);

    void init();

    void dismiss();

    void updateXY(int x, int y);

    void updateX(int x);

    void updateY(int y);

    int getX();

    int getY();
}
