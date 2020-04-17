package com.google.android.libraries.launcherclient;

import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;
import android.view.WindowManager;

public interface ILauncherOverlay extends IInterface {
    void onScroll(float f) throws RemoteException;

    void closeOverlay(int i) throws RemoteException;

    void windowAttached2(Bundle bundle, ILauncherOverlayCallback dVar) throws RemoteException;

    void windowAttached(WindowManager.LayoutParams layoutParams, ILauncherOverlayCallback dVar, int i) throws RemoteException;

    void windowDetached(boolean changingConfigurations) throws RemoteException;

    void startScroll() throws RemoteException;

    void endScroll() throws RemoteException;

    void mo15b(int i) throws RemoteException;

    void requestVoiceDetection(boolean start) throws RemoteException;

    void mo17c() throws RemoteException;

    void openOverlay(int i) throws RemoteException;

    void mo19d() throws RemoteException;

    boolean isVoiceDetectionRunning() throws RemoteException;

    boolean mo21f() throws RemoteException;
}
