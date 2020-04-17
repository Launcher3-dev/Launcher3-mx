package com.google.android.launcherclient;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public class BaseProxy implements IInterface {

    private final IBinder mRemote;

    private final String token;

    protected BaseProxy(IBinder iBinder, String str) {
        this.mRemote = iBinder;
        this.token = str;
    }

    public IBinder asBinder() {
        return this.mRemote;
    }

    public final Parcel obtain() {
        Parcel obtain = Parcel.obtain();
        obtain.writeInterfaceToken(this.token);
        return obtain;
    }

    public final Parcel obtain(int i, Parcel parcel) throws RemoteException {
        parcel = Parcel.obtain();
        try {
            this.mRemote.transact(i, parcel, parcel, 0);
            parcel.readException();
            return parcel;
        } catch (RuntimeException e) {
            throw e;
        } finally {
            parcel.recycle();
        }
    }

    public final void transact(int i, Parcel parcel) throws RemoteException {
        try {
            this.mRemote.transact(i, parcel, null, 1);
        } finally {
            parcel.recycle();
        }
    }
}
