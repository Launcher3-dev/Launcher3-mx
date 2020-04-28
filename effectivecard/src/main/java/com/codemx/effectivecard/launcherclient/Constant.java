package com.codemx.effectivecard.launcherclient;

/**
 * Created by yuchuan
 * DATE 2020/4/17
 * TIME 14:58
 */
public final class Constant {

    public static final String GSA_PACKAGE = "com.codemx.effectcard";// 负一屏包名
    public static final String ACTION = "com.codemx.launcher3.WINDOW_OVERLAY";

    private static final String DESCRIPTOR = "com.google.android.libraries.launcherclient.ILauncherOverlayCallback";
    static final int TRANSACTION_overlayScrollChanged = 1;
    static final int TRANSACTION_overlayStatusChanged = 2;
    static final int TRANSACTION_requestStatusbarState = 3;
    static final int TRANSACTION_requestSearchActivity = 4;


}
