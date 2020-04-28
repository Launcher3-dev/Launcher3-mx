package com.codemx.effectivecard.launcherclient;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 用来Launcher和LauncherOverlay通信
 * <p>
 * Created by yuchuan
 * DATE 2020/4/28
 * TIME 17:16
 */
public class MxMessage implements Parcelable, Serializable {

    public final int what;
    public String msg1;
    public String msg2;
    public Object obj;

    public MxMessage(int what) {
        this.what = what;
    }

    public MxMessage(int what, String msg1) {
        this.what = what;
        this.msg1 = msg1;
    }

    public MxMessage(int what, String msg1, String msg2) {
        this.what = what;
        this.msg1 = msg1;
        this.msg2 = msg2;
    }

    public MxMessage(int what, String msg1, String msg2, Object obj) {
        this.what = what;
        this.msg1 = msg1;
        this.msg2 = msg2;
        this.obj = obj;
    }

    protected MxMessage(Parcel in) {
        what = in.readInt();
        msg1 = in.readString();
        msg2 = in.readString();
        if (in.readInt() == 1) {
            this.obj = in.readParcelable(getClass().getClassLoader());
        } else if (in.readInt() == 2) {
            this.obj = in.readSerializable();
        } else {
            this.obj = null;
        }
    }

    public static final Creator<MxMessage> CREATOR = new Creator<MxMessage>() {
        @Override
        public MxMessage createFromParcel(Parcel in) {
            return new MxMessage(in);
        }

        @Override
        public MxMessage[] newArray(int size) {
            return new MxMessage[size];
        }
    };

    @Override
    public String toString() {
        return "MxMessage{" +
                "what=" + what +
                ", msg1='" + msg1 + '\'' +
                ", msg2='" + msg2 + '\'' +
                ", obj=" + obj +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(what);
        dest.writeString(msg1);
        dest.writeString(msg2);
        if (obj instanceof Parcelable) {
            dest.writeInt(1);
            dest.writeParcelable((Parcelable) obj, flags);
        } else if (obj instanceof Serializable) {
            dest.writeInt(2);
            dest.writeSerializable((Serializable) obj);
        } else {
            dest.writeInt(0);
        }
    }
}
