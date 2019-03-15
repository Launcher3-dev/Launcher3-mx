package com.android.mxtheme.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 主题对象封装
 *
 * Created by CodeMX
 * DATE 2019/2/22
 * TIME 9:40
 */
public class ThemeBean implements Parcelable {

    private String themeId;

    private String themeName;

    private String themePath;

    private String themeUrl;

    private String themeAuthor;

    private String themePublishDate;

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getThemePath() {
        return themePath;
    }

    public void setThemePath(String themePath) {
        this.themePath = themePath;
    }

    public String getThemeUrl() {
        return themeUrl;
    }

    public void setThemeUrl(String themeUrl) {
        this.themeUrl = themeUrl;
    }

    public String getThemeAuthor() {
        return themeAuthor;
    }

    public void setThemeAuthor(String themeAuthor) {
        this.themeAuthor = themeAuthor;
    }

    public String getThemePublishDate() {
        return themePublishDate;
    }

    public void setThemePublishDate(String themePublishDate) {
        this.themePublishDate = themePublishDate;
    }

    public ThemeBean() {
    }

    protected ThemeBean(Parcel in) {
        themeId = in.readString();
        themeName = in.readString();
        themePath = in.readString();
        themeUrl = in.readString();
        themeAuthor = in.readString();
        themePublishDate = in.readString();
    }

    public static final Creator<ThemeBean> CREATOR = new Creator<ThemeBean>() {
        @Override
        public ThemeBean createFromParcel(Parcel in) {
            return new ThemeBean(in);
        }

        @Override
        public ThemeBean[] newArray(int size) {
            return new ThemeBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(themeId);
        dest.writeString(themeName);
        dest.writeString(themePath);
        dest.writeString(themeUrl);
        dest.writeString(themeAuthor);
        dest.writeString(themePublishDate);
    }
}
