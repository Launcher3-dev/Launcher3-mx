package com.android.launcher3.menu.imp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.android.launcher3.menu.view.HorizontalPageScrollView;

import java.util.List;

/**
 * Created by CodeMX
 * DATE 2018/2/5
 * TIME 18:11
 */

public interface IMenuAdapter<T> {

    void addAllData(List<T> list);

    void setContainer(HorizontalPageScrollView container);

    int getMenuItemCount();

    View getChildView(int position, @Nullable View convertView, @NonNull ViewGroup parent);

}
