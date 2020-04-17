package com.codemx.effectivecard;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by yuchuan
 * DATE 2020/4/17
 * TIME 16:03
 */
public class CardWindowManager {

    Context mContext;
    WindowManager mWindowManager;
    WindowManager.LayoutParams mParams;
    View mContentView;

    private void initApplicationWindow(Context context) {
        try{
            mContext = context.createPackageContext("chenchao.example.com.appclient", Context.CONTEXT_IGNORE_SECURITY);
            Log.i("chenchao","mContext =" +mContext.getApplicationInfo().toString());
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

            mParams = new WindowManager.LayoutParams();
            mParams.packageName ="chenchao.example.com.appclient";
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA;
            mParams.format = PixelFormat.RGBA_8888;
            mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            mParams.gravity = Gravity.TOP | Gravity.LEFT;
            mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            mParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        } catch (PackageManager.NameNotFoundException n){
            n.printStackTrace();
        }
    }

    private void initView(){

    }

    private void showWindow(){
        mWindowManager.addView(mContentView, mParams);
    }
    private void hideWindow(){
        mWindowManager.removeView(mContentView);
    }


}
