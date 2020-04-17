package com.codemx.effectivecard;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.codemx.effectivecard.launcherclient.Constant;

/**
 * Created by yuchuan
 * DATE 2020/4/17
 * TIME 16:03
 */
public class CardWindowManager {

    private Context mContext;
    private Context mCardContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private View mContentView;

    public CardWindowManager(Context context) {
        mContext = context;
        initApplicationWindow();
    }

    private void initApplicationWindow() {
        try {
            mCardContext = mContext.createPackageContext(Constant.GSA_PACKAGE, Context.CONTEXT_IGNORE_SECURITY);
            mWindowManager = (WindowManager) mCardContext.getSystemService(Context.WINDOW_SERVICE);
            mParams = new WindowManager.LayoutParams();
            mParams.packageName = Constant.GSA_PACKAGE;
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA;
            mParams.format = PixelFormat.RGBA_8888;
            mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            mParams.gravity = Gravity.TOP | Gravity.LEFT;
            mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            mParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        } catch (PackageManager.NameNotFoundException n) {
            n.printStackTrace();
        }
    }

    private void initView() {
        mContentView = LayoutInflater.from(mCardContext).inflate(R.layout.layout_card, null);
    }

    public void showWindow() {
        initView();
        mWindowManager.addView(mContentView, mParams);
    }

    public void hideWindow() {
        mWindowManager.removeView(mContentView);
    }


}
