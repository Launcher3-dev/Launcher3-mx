package com.codemx.effectivecard.launcherclient;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ILauncherOverlayCallback extends IInterface {
    void overlayScrollChanged(float var1) throws RemoteException;

    void overlayStatusChanged(int var1) throws RemoteException;

    void requestStatusbarState(int state) throws RemoteException;

    void requestSearchActivity() throws RemoteException;

    abstract class Stub extends Binder implements ILauncherOverlayCallback {
        private static final String DESCRIPTOR = "com.google.android.libraries.launcherclient.ILauncherOverlayCallback";
        static final int TRANSACTION_overlayScrollChanged = 1;
        static final int TRANSACTION_overlayStatusChanged = 2;
        static final int TRANSACTION_requestStatusbarState = 3;
        static final int TRANSACTION_requestSearchActivity = 4;

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static ILauncherOverlayCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            } else {
                IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
                return iin instanceof ILauncherOverlayCallback ? (ILauncherOverlayCallback) iin : new Proxy(obj);
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case TRANSACTION_overlayScrollChanged:
                    data.enforceInterface(DESCRIPTOR);
                    float _arg01 = data.readFloat();
                    this.overlayScrollChanged(_arg01);
                    return true;
                case TRANSACTION_overlayStatusChanged:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    this.overlayStatusChanged(_arg0);
                    return true;
                case TRANSACTION_requestStatusbarState:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    this.requestStatusbarState(_arg02);
                    return true;
                case TRANSACTION_requestSearchActivity:
                    data.enforceInterface(DESCRIPTOR);
                    this.requestSearchActivity();
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements ILauncherOverlayCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            public void overlayScrollChanged(float progress) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeFloat(progress);
                    this.mRemote.transact(1, _data,  null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void overlayStatusChanged(int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(status);
                    this.mRemote.transact(2, _data,  null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override
            public void requestStatusbarState(int state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(state);
                    this.mRemote.transact(3, _data,  null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override
            public void requestSearchActivity() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(4, _data,  null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
