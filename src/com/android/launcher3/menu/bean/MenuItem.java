package com.android.launcher3.menu.bean;

/**
 * Created by CodeMX
 * DATE 2018/1/15
 * TIME 11:04
 */

public class MenuItem {


    public static final int MENU = 0;
    public static final int WIDGET = 1;
    public static final int EFFECT = 2;
    public static final int WIDGET_LIST = 3;

    private int id;
    private int icon;
    private int titleId;
    private int position;
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getTitle() {
        return titleId;
    }

    public void setTitle(int title) {
        this.titleId = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", icon=" + icon +
                ", titleId=" + titleId +
                ", position=" + position +
                ", type=" + type +
                '}';
    }
}
