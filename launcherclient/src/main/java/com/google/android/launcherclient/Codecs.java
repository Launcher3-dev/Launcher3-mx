package com.google.android.launcherclient;

import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;

public class Codecs {
    private Codecs() {
    }

    public static boolean check(Parcel parcel) {
        return parcel.readInt() != 0;
    }

    public static void writeChangingConfiguration(Parcel parcel, boolean changingConfigurations) {
        parcel.writeInt(changingConfigurations ? 1 : 0);
    }

    public static void writeParcelable(Parcel parcel, Parcelable parcelable) {
        if (parcelable == null) {
            parcel.writeInt(0);
            return;
        }
        parcel.writeInt(1);
        parcelable.writeToParcel(parcel, 0);
    }

    public static void writeInterfaceToken(Parcel parcel, IInterface iInterface) {
        if (iInterface == null) {
            parcel.writeStrongBinder(null);
        } else {
            parcel.writeStrongBinder(iInterface.asBinder());
        }
    }

    static {
        Codecs.class.getClassLoader();
    }
}
