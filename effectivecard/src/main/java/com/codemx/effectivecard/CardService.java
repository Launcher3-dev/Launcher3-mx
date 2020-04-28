package com.codemx.effectivecard;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.mxlibrary.util.XLog;
import com.codemx.effectivecard.launcherclient.ILauncherOverlay;
import com.codemx.effectivecard.launcherclient.ILauncherOverlayCallback;
import com.codemx.effectivecard.launcherclient.MxLayoutParams;
import com.codemx.effectivecard.launcherclient.MxMessage;

import java.lang.ref.WeakReference;

/**
 * Created by yuchuan
 * DATE 2020/4/17
 * TIME 15:05
 */
public class CardService extends Service {

    private ILauncherOverlay.Stub mStub;

    @Override
    public void onCreate() {
        super.onCreate();
        XLog.d(XLog.getTag(), "onCreate");
        mStub = new CardBinder(new CardWindowManager(this));
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
        private WeakReference<IWindowCallback> mWeakReference;

        CardBinder(IWindowCallback windowCallback) {
            mWeakReference = new WeakReference<>(windowCallback);
        }

        @Override
        public void startScroll() throws RemoteException {
            XLog.d(XLog.getTag(), "startScroll");
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                callback.startScroll();
            }
        }

        @Override
        public void onScroll(float progress, boolean isRtl) throws RemoteException {
            XLog.d(XLog.getTag(), "startScroll#progress= " + progress + " ,isRtl= " + isRtl);
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                callback.onScroll(progress, isRtl);
            }
        }

        @Override
        public void endScroll() throws RemoteException {
            XLog.d(XLog.getTag(), "endScroll");
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                callback.endScroll();
            }
        }

        @Override
        public void windowAttached(MxLayoutParams layoutParams, ILauncherOverlayCallback overlayCallback, int flags) throws RemoteException {
            XLog.d(XLog.getTag(), "windowAttached " + layoutParams);
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                callback.windowAttached(layoutParams, overlayCallback, flags);
            }
        }

        @Override
        public void windowDetached(boolean isChangingConfigurations) throws RemoteException {
            XLog.d(XLog.getTag(), "windowDetached " + isChangingConfigurations);
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                callback.windowDetached(isChangingConfigurations);
            }
        }

        @Override
        public void openOverlay(int flags) throws RemoteException {
            XLog.d(XLog.getTag(), "openOverlay " + flags);
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                callback.openOverlay(flags);
            }
        }

        @Override
        public void closeOverlay(int flags) throws RemoteException {
            XLog.d(XLog.getTag(), "closeOverlay " + flags);
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                callback.closeOverlay(flags);
            }
        }

        @Override
        public void onResume() throws RemoteException {
            XLog.d(XLog.getTag(), "onResume ");
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                callback.onResume();
            }
        }

        @Override
        public void onPause() throws RemoteException {
            XLog.d(XLog.getTag(), "onPause ");
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                callback.onPause();
            }
        }

        @Override
        public void onTransact(MxMessage msg) throws RemoteException {
            XLog.d(XLog.getTag(), "onPause ");
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                callback.onTransact(msg);
            }
        }

        @Override
        public void requestVoiceDetection(boolean start) throws RemoteException {
            XLog.d(XLog.getTag(), "requestVoiceDetection " + start);
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                callback.requestVoiceDetection(start);
            }
        }

        @Override
        public String getVoiceSearchLanguage() throws RemoteException {
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                return callback.getVoiceSearchLanguage();
            }
            return null;
        }

        @Override
        public boolean isVoiceDetectionRunning() throws RemoteException {
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                return callback.isVoiceDetectionRunning();
            }
            return false;
        }

        @Override
        public void enableScroll(boolean left, boolean right) throws RemoteException {
            XLog.d(XLog.getTag(), "enableScroll left: " + left + " ,right: " + right);
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                callback.enableScroll(left, right);
            }
        }

        @Override
        public void enableTransparentWallpaper(boolean isTransparent) throws RemoteException {
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                callback.enableTransparentWallpaper(isTransparent);
            }
        }

        @Override
        public void enableLoopWithOverlay(boolean enableLoop) throws RemoteException {
            IWindowCallback callback = mWeakReference.get();
            if (callback != null) {
                callback.enableLoopWithOverlay(enableLoop);
            }
        }
    }

}
