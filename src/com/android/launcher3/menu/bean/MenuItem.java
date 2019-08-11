package com.android.launcher3.menu.bean;

/**
 * Created by CodeMX
 * DATE 2018/1/15
 * TIME 11:04
 */

public class MenuItem {

    public static final int SETTING = 100;
    public static final int THEME = 101;
    public static final int WALLPAPER = 102;
    public static final int WIDGET = 103;
    public static final int EFFECT = 104;
    public static final int WIDGET_LIST = 105;

    private int id;
    private int icon;
    private String title;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
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
