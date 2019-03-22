package com.android.mxtheme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.mxtheme.bean.ThemeBean;
import com.android.mxtheme.bean.WallpaperBean;

/**
 * Created by CodeMX
 * DATE 2019/2/22
 * TIME 9:22
 */
public class ThemeActivity extends AppCompatActivity implements View.OnClickListener {

    ThemeChangeUtil mThemeUtil;
    private Button mBtnTheme;
    private Button mBtnWallpaper;
    private TextView mTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_activity);

        mBtnTheme = findViewById(R.id.theme_activity_set_theme);
        mBtnWallpaper = findViewById(R.id.theme_activity_set_wallpaper);
        mTv = findViewById(R.id.theme_activity_tv);
        mBtnTheme.setOnClickListener(this);
        mBtnWallpaper.setOnClickListener(this);

        mThemeUtil = new ThemeChangeUtil();
        mThemeUtil.startService(this);
    }

    @Override
    protected void onDestroy() {
        mThemeUtil.endService(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.theme_activity_set_theme) {
            ThemeBean bean = new ThemeBean();
            bean.setThemeName("theme");
            mThemeUtil.changeTheme(bean);
        } else if (i == R.id.theme_activity_set_wallpaper) {
            WallpaperBean wallpaperBean = new WallpaperBean();
            wallpaperBean.setWallpaperName("theme");
            mThemeUtil.changeWallpaper(wallpaperBean);
        } else {
        }
    }
}
