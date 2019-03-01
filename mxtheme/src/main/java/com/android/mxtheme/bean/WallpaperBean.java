package com.android.mxtheme.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 壁纸对象封装
 *
 * Created by CodeMX
 * DATE 2019/2/22
 * TIME 9:40
 */
public class WallpaperBean implements Parcelable {

    private String wallpaperId;

    private String wallpaperName;

    private String wallpaperPath;

    private String wallpaperUrl;

    private String wallpaperAuthor;

    private String wallpaperPublishDate;

    public String getWallpaperId() {
        return wallpaperId;
    }

    public void setWallpaperId(String wallpaperId) {
        this.wallpaperId = wallpaperId;
    }

    public String getWallpaperName() {
        return wallpaperName;
    }

    public void setWallpaperName(String wallpaperName) {
        this.wallpaperName = wallpaperName;
    }

    public String getWallpaperPath() {
        return wallpaperPath;
    }

    public void setWallpaperPath(String wallpaperPath) {
        this.wallpaperPath = wallpaperPath;
    }

    public String getWallpaperUrl() {
        return wallpaperUrl;
    }

    public void setWallpaperUrl(String wallpaperUrl) {
        this.wallpaperUrl = wallpaperUrl;
    }

    public String getWallpaperAuthor() {
        return wallpaperAuthor;
    }

    public void setWallpaperAuthor(String wallpaperAuthor) {
        this.wallpaperAuthor = wallpaperAuthor;
    }

    public String getWallpaperPublishDate() {
        return wallpaperPublishDate;
    }

    public void setWallpaperPublishDate(String wallpaperPublishDate) {
        this.wallpaperPublishDate = wallpaperPublishDate;
    }

    public WallpaperBean() {
    }

    protected WallpaperBean(Parcel in) {
        wallpaperId = in.readString();
        wallpaperName = in.readString();
        wallpaperPath = in.readString();
        wallpaperUrl = in.readString();
        wallpaperAuthor = in.readString();
        wallpaperPublishDate = in.readString();
    }

    public static final Creator<WallpaperBean> CREATOR = new Creator<WallpaperBean>() {
        @Override
        public WallpaperBean createFromParcel(Parcel in) {
            return new WallpaperBean(in);
        }

        @Override
        public WallpaperBean[] newArray(int size) {
            return new WallpaperBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(wallpaperId);
        dest.writeString(wallpaperName);
        dest.writeString(wallpaperPath);
        dest.writeString(wallpaperUrl);
        dest.writeString(wallpaperAuthor);
        dest.writeString(wallpaperPublishDate);
    }
}
