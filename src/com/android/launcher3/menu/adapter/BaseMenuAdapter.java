package com.android.launcher3.menu.adapter;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.launcher3.R;
import com.android.mxlibrary.view.CircleImageView;

/**
 * Created by CodeMX
 * DATE 2018/1/31
 * TIME 16:08
 */

public abstract class BaseMenuAdapter<T> extends ArrayAdapter {

    BaseMenuAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    public abstract void setContainer(View view);

    View createShortcut(ViewGroup parent) {
        return (CircleImageView) LayoutInflater.from(getContext())
                .inflate(R.layout.menu_item_layout, parent, false);
    }

}
