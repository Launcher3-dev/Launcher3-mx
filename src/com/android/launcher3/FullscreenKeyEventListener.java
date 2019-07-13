package com.android.launcher3;

import android.view.KeyEvent;
import android.view.View;

/**
 * A keyboard listener we set on full screen pages (e.g. custom content).
 */
public class FullscreenKeyEventListener implements View.OnKeyListener {
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                || keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            // Handle the key event just like a workspace icon would in these cases. In this case,
            // it will basically act as if there is a single icon in the top left (so you could
            // think of the fullscreen page as a focusable fullscreen widget).
            return FocusHelper.handleIconKeyEvent(v, keyCode, event);
        }
        return false;
    }
}
