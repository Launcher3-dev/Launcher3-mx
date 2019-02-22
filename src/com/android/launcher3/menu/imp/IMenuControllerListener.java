package com.android.launcher3.menu.imp;

/**
 * Created by CodeMX
 * DATE 2018/1/16
 * TIME 17:25
 */

public interface IMenuControllerListener {

    void onShowOrHideBegin(boolean isShow);

    void onShowOrHide(int progress);

    void onShowOrHideEnd(boolean isShow);

}
