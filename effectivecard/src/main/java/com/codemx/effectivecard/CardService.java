package com.codemx.effectivecard;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.mxlibrary.util.XLog;
import com.codemx.effectivecard.launcherclient.ILauncherOverlay;
import com.codemx.effectivecard.launcherclient.ILauncherOverlayCallback;
import com.codemx.effectivecard.launcherclient.MxLayoutParams;

import java.lang.ref.WeakReference;

/**
 * Created by yuchuan
 * DATE 2020/4/17
 * TIME 15:05
 */
public class CardService extends Service {

    private ILauncherOverlay.Stub mStub;
    // 向Launcher通信的回调函数，对应LauncherOverlayCallbacks
    private ILauncherOverlayCallback mOverlayCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        XLog.d(XLog.getTag(), "onCreate");
        mStub = new CardBinder(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        XLog.d(XLog.getTag(), "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        XLog.d(XLog.getTag(), "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        XLog.d(XLog.getTag(), "onDestroy");
    }

    // Launcher通过服务会调用这里的函数来达到与launcher同步，然后控制window滑动
    private static class CardBinder extends ILauncherOverlay.Stub {
        private WeakReference<CardService> mWeakReference;

        CardBinder(CardService service) {
            mWeakReference = new WeakReference<>(service);
        }

        @Override
        public void startScroll() throws RemoteException {
            XLog.d(XLog.getTag(), "startScroll");
        }

        @Override
        public void onScroll(float progress, boolean isRtl) throws RemoteException {
            XLog.d(XLog.getTag(), "startScroll#progress= " + progress + " ,isRtl= " + isRtl);
        }

        @Override
        public void endScroll() throws RemoteException {
            XLog.d(XLog.getTag(), "endScroll");
        }

        @Override
        public void windowAttached(MxLayoutParams layoutParams, ILauncherOverlayCallback overlayCallback, int flags) throws RemoteException {
            XLog.d(XLog.getTag(), "windowAttached " + layoutParams);
            if (mWeakReference != null && mWeakReference.get() != null) {
                mWeakReference.get().registerCallback(overlayCallback);
            }
        }

        @Override
        public void windowDetached(boolean isChangingConfigurations) throws RemoteException {
            XLog.d(XLog.getTag(), "windowDetached " + isChangingConfigurations);
        }

        @Override
        public void closeOverlay(int flags) throws RemoteException {
            XLog.d(XLog.getTag(), "closeOverlay " + flags);
        }

        @Override
        public void onPause() throws RemoteException {
            XLog.d(XLog.getTag(), "onPause ");
        }

        @Override
        public void onResume() throws RemoteException {
            XLog.d(XLog.getTag(), "onResume ");
        }

        @Override
        public void openOverlay(int flags) throws RemoteException {
            XLog.d(XLog.getTag(), "openOverlay "  + flags);
        }

        @Override
        public void requestVoiceDetection(boolean start) throws RemoteException {
            XLog.d(XLog.getTag(), "requestVoiceDetection "  + start);
        }

        @Override
        public String getVoiceSearchLanguage() throws RemoteException {
            return null;
        }

        @Override
        public boolean isVoiceDetectionRunning() throws RemoteException {
            return false;
        }

        @Override
        public void enableScroll(boolean left, boolean right) throws RemoteException {
            XLog.d(XLog.getTag(), "enableScroll left: "  + left + " ,right: " + right);
        }

        @Override
        public void enableTransparentWallpaper(boolean isTransparent) throws RemoteException {

        }

        @Override
        public void enableLoopWithOverlay(boolean enableLoop) throws RemoteException {

        }
    }

    private void registerCallback(ILauncherOverlayCallback overlayCallback) {
        this.mOverlayCallback = overlayCallback;
    }

}
