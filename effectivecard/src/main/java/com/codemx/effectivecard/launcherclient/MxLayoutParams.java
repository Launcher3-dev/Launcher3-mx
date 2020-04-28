package com.codemx.effectivecard.launcherclient;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.WindowManager;

public class MxLayoutParams extends WindowManager.LayoutParams implements Parcelable{

    public MxLayoutParams() {
        super();
    }

    private MxLayoutParams(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MxLayoutParams> CREATOR = new Creator<MxLayoutParams>() {
        @Override
        public MxLayoutParams createFromParcel(Parcel in) {
            return new MxLayoutParams(in);
        }

        @Override
        public MxLayoutParams[] newArray(int size) {
            return new MxLayoutParams[size];
        }
    };
}
