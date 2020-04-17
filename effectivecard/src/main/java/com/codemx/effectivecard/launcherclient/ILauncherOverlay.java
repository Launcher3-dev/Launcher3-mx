package com.codemx.effectivecard.launcherclient;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public interface ILauncherOverlay extends IInterface {
    void startScroll() throws RemoteException;

    void onScroll(float progress) throws RemoteException;

    void endScroll() throws RemoteException;

    void windowAttached(WindowManager.LayoutParams layoutParams, ILauncherOverlayCallback overlayCallback, int flags) throws RemoteException;

    void windowDetached(boolean isChangingConfigurations) throws RemoteException;

    void closeOverlay(int flags) throws RemoteException;

    void onPause() throws RemoteException;

    void onResume() throws RemoteException;

    void openOverlay(int flags) throws RemoteException;

    void requestVoiceDetection(boolean start) throws RemoteException;

    String getVoiceSearchLanguage() throws RemoteException;

    boolean isVoiceDetectionRunning() throws RemoteException;

    void enableScroll(boolean left, boolean right) throws RemoteException;

    void enableTransparentWallpaper(boolean isTransparent) throws RemoteException;

    void enableLoopWithOverlay(boolean enableLoop) throws RemoteException;

    abstract class Stub extends Binder implements ILauncherOverlay {
        private static final String DESCRIPTOR = "com.google.android.libraries.launcherclient.ILauncherOverlay";
        static final int TRANSACTION_startScroll = 1;
        static final int TRANSACTION_onScroll = 2;
        static final int TRANSACTION_endScroll = 3;
        static final int TRANSACTION_windowAttached = 4;
        static final int TRANSACTION_windowDetached = 5;
        static final int TRANSACTION_closeOverlay = 6;
        static final int TRANSACTION_onPause = 7;
        static final int TRANSACTION_onResume = 8;
        static final int TRANSACTION_openOverlay = 9;
        static final int TRANSACTION_requestVoiceDetection = 10;
        static final int TRANSACTION_getVoiceSearchLanguage = 11;
        static final int TRANSACTION_isVoiceDetectionRunning = 12;

        static final int TRANSACTION_enableScroll = 13;
        static final int TRANSACTION_enableTransparentWallpaper = 2002;
        static final int TRANSACTION_enableLoopWithOverlay = 2003;

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static ILauncherOverlay asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            } else {
                IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
                return iin instanceof ILauncherOverlay ? (ILauncherOverlay) iin : new Proxy(obj);
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            boolean _result;
            int _result2;
            switch (code) {
                case TRANSACTION_startScroll:
                    data.enforceInterface(DESCRIPTOR);
                    this.startScroll();
                    return true;
                case TRANSACTION_onScroll:
                    data.enforceInterface(DESCRIPTOR);
                    float _result4 = data.readFloat();
                    this.onScroll(_result4);
                    return true;
                case TRANSACTION_endScroll:
                    data.enforceInterface(DESCRIPTOR);
                    this.endScroll();
                    return true;
                case TRANSACTION_windowAttached:
                    data.enforceInterface(DESCRIPTOR);
                    LayoutParams _result3;
                    if (0 != data.readInt()) {
                        _result3 = LayoutParams.CREATOR.createFromParcel(data);
                    } else {
                        _result3 = null;
                    }
                    ILauncherOverlayCallback _arg1 = ILauncherOverlayCallback.Stub.asInterface(data.readStrongBinder());
                    int _arg2 = data.readInt();
                    this.windowAttached(_result3, _arg1, _arg2);
                    return true;
                case TRANSACTION_windowDetached:
                    data.enforceInterface(DESCRIPTOR);
                    _result = 0 != data.readInt();
                    this.windowDetached(_result);
                    return true;
                case TRANSACTION_closeOverlay:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = data.readInt();
                    this.closeOverlay(_result2);
                    return true;
                case TRANSACTION_onPause:
                    data.enforceInterface(DESCRIPTOR);
                    this.onPause();
                    return true;
                case TRANSACTION_onResume:
                    data.enforceInterface(DESCRIPTOR);
                    this.onResume();
                    return true;
                case TRANSACTION_openOverlay:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = data.readInt();
                    this.openOverlay(_result2);
                    return true;
                case TRANSACTION_requestVoiceDetection:
                    data.enforceInterface(DESCRIPTOR);
                    _result = 0 != data.readInt();
                    this.requestVoiceDetection(_result);
                    return true;
                case TRANSACTION_getVoiceSearchLanguage:
                    data.enforceInterface(DESCRIPTOR);
                    String _result1 = this.getVoiceSearchLanguage();
                    reply.writeNoException();
                    reply.writeString(_result1);
                    return true;
                case TRANSACTION_isVoiceDetectionRunning:
                    data.enforceInterface(DESCRIPTOR);
                    _result = this.isVoiceDetectionRunning();
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case TRANSACTION_enableScroll:
                    data.enforceInterface(DESCRIPTOR);
                    boolean arg1 = 0 != data.readInt();
                    boolean arg2 = 0 != data.readInt();
                    this.enableScroll(arg1, arg2);
                    return true;
                case TRANSACTION_enableTransparentWallpaper:
                    data.enforceInterface(DESCRIPTOR);
                    _result = 0 != data.readInt();
                    this.enableTransparentWallpaper(_result);
                    return true;
                case TRANSACTION_enableLoopWithOverlay:
                    data.enforceInterface(DESCRIPTOR);
                    _result = 0 != data.readInt();
                    this.enableLoopWithOverlay(_result);
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements ILauncherOverlay {
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

            public void startScroll() throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }

            }

            public void onScroll(float progress) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeFloat(progress);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }

            }

            public void endScroll() throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }

            }

            public void windowAttached(LayoutParams layoutParams, ILauncherOverlayCallback overlayCallback, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if (layoutParams != null) {
                        _data.writeInt(1);
                        layoutParams.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }

                    _data.writeStrongBinder(overlayCallback != null ? overlayCallback.asBinder() : null);
                    _data.writeInt(flags);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }

            }

            public void windowDetached(boolean isChangingConfigurations) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(isChangingConfigurations ? 1 : 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }

            }

            public void closeOverlay(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }

            }

            public void onPause() throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }

            }

            public void onResume() throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }

            }

            public void openOverlay(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }

            }

            public void requestVoiceDetection(boolean start) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(start ? 1 : 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }

            }

            public String getVoiceSearchLanguage() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean isVoiceDetectionRunning() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            @Override
            public void enableScroll(boolean left, boolean right) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(left ? 1 : 0);
                    _data.writeInt(right ? 1 : 0);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override
            public void enableTransparentWallpaper(boolean isTransparent) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(isTransparent ? 1 : 0);
                    this.mRemote.transact(TRANSACTION_enableTransparentWallpaper, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override
            public void enableLoopWithOverlay(boolean enableLoop) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(enableLoop ? 1 : 0);
                    this.mRemote.transact(TRANSACTION_enableLoopWithOverlay, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
