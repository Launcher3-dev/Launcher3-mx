package com.codemx.effectivecard;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.WindowManager;

import com.codemx.effectivecard.launcherclient.ILauncherOverlay;
import com.codemx.effectivecard.launcherclient.ILauncherOverlayCallback;

/**
 * Created by yuchuan
 * DATE 2020/4/17
 * TIME 15:05
 */
public class CardService extends Service {

    private ILauncherOverlay.Stub mStub;
    private ILauncherOverlayCallback mCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        mStub = new CardBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    // Launcher通过服务会调用这里的函数来达到与launcher同步，然后控制window滑动
    private static class CardBinder extends ILauncherOverlay.Stub {

        @Override
        public void startScroll() throws RemoteException {
        }

        @Override
        public void onScroll(float progress) throws RemoteException {
        }

        @Override
        public void endScroll() throws RemoteException {
        }

        @Override
        public void windowAttached(WindowManager.LayoutParams layoutParams,
                                   ILauncherOverlayCallback overlayCallback, int flags) throws RemoteException {

        }

        @Override
        public void windowDetached(boolean isChangingConfigurations) throws RemoteException {
        }

        @Override
        public void closeOverlay(int flags) throws RemoteException {

        }

        @Override
        public void onPause() throws RemoteException {
        }

        @Override
        public void onResume() throws RemoteException {
        }

        @Override
        public void openOverlay(int flags) throws RemoteException {
        }

        @Override
        public void requestVoiceDetection(boolean start) throws RemoteException {

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

        }

        @Override
        public void enableTransparentWallpaper(boolean isTransparent) throws RemoteException {

        }

        @Override
        public void enableLoopWithOverlay(boolean enableLoop) throws RemoteException {

        }
    }

}
