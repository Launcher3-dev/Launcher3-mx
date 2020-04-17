package com.codemx.effectivecard.launcherclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManager.LayoutParams;

import com.android.mxlibrary.util.XLog;
import com.codemx.effectivecard.CardService;

public class LauncherClient {
    private static final boolean HIDE_WINDOW_WHEN_OVERLAY_OPEN = false;
    private static final String TAG = "DrawerOverlayClient";
    private static final boolean DEBUG = false;
    private static final int VERSION = 0;

    private static final int OPTIONS_FLAG_OVERLAY = 1;
    private static final int OPTIONS_FLAG_HOTWORD = 2;
    private static final int OPTIONS_FLAG_DEFAULT = 3;

    private static final int SERVICE_STATUS_DEFAULT = 0;
    private static final int SERVICE_STATUS_OVERLAY_ATTACHED = 1;
    private static final int SERVICE_STATUS_HOTWORD_ACTIVE = 2;

    private static final int OVERLAY_OPTION_FLAG_IMMEDIATE = 0;
    private static final int OVERLAY_OPTION_FLAG_ANIMATE = 1;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED = 1;
    private static final int STATE_CONNECTING = 2;
    private static AppServiceConnection sApplicationConnection;
    private final Activity mActivity;
    private final Intent mServiceIntent;
    private final LauncherClientCallbacks mLauncherClientCallbacks;
    private final OverlayServiceConnection mServiceConnection;
    private final BroadcastReceiver mUpdateReceiver;
    protected ILauncherOverlay mOverlay;
    private int mState;
    private boolean mIsResumed;
    private boolean mDestroyed;
    private boolean mNeedsServiceUnbind;
    private int mServiceStatus;
    private int mServiceConnectionOptions;
    private LayoutParams mWindowAttrs;
    private OverlayCallbacks mCurrentCallbacks;

    public LauncherClient(Activity activity) {
        this(activity, new LauncherClientCallbacksAdapter());
    }

    public LauncherClient(Activity activity, LauncherClientCallbacks callbacks) {
        this(activity, callbacks, true);
    }

    public LauncherClient(Activity activity, LauncherClientCallbacks callbacks, boolean overlayEnabled) {
        this(activity, callbacks, Constant.GSA_PACKAGE, overlayEnabled);
    }

    public LauncherClient(Activity activity, LauncherClientCallbacks callbacks, String targetPackage, boolean overlayEnabled) {
        XLog.d(XLog.getTag(), XLog.TAG_GU_STATE + " LauncherClient  ");
        mUpdateReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.d("LauncherClient", "PACKAGE_ADDED reconnect");
                reconnect();
            }
        };
        mIsResumed = false;
        mDestroyed = false;
        mNeedsServiceUnbind = false;
        mServiceStatus = -1;
        mActivity = activity;
        mServiceIntent = getServiceIntent(activity, targetPackage);
        mLauncherClientCallbacks = callbacks;
        mState = STATE_DISCONNECTED;
        mServiceConnection = new OverlayServiceConnection();
        mServiceConnectionOptions = overlayEnabled ? OPTIONS_FLAG_DEFAULT : OPTIONS_FLAG_HOTWORD;
        IntentFilter filter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        filter.addDataScheme("package");
        filter.addDataSchemeSpecificPart(targetPackage, 0);
        mActivity.registerReceiver(mUpdateReceiver, filter);
        reconnect();
    }

    public final void onAttachedToWindow() {
        if (!mDestroyed) {
            setWindowAttrs(mActivity.getWindow().getAttributes());
        }
    }

    public final void onDetachedFromWindow() {
        if (!mDestroyed) {
            setWindowAttrs(null);
        }
    }

    public void onResume() {
        if (!mDestroyed) {
            reconnect();
            mIsResumed = true;
            if (mOverlay != null && mWindowAttrs != null) {
                try {
                    mOverlay.onResume();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onPause() {
        if (!mDestroyed) {
            mIsResumed = false;
            if (mOverlay != null && mWindowAttrs != null) {
                try {
                    mOverlay.onPause();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void onDestroy() {
        removeClient(!mActivity.isChangingConfigurations());
    }

    public void disconnect() {
        //removeClient(true);
        if (mNeedsServiceUnbind) {
            mActivity.unbindService(mServiceConnection);
            mNeedsServiceUnbind = false;
        }

        if (mCurrentCallbacks != null) {
            mCurrentCallbacks.clear();
            mCurrentCallbacks = null;
        }

        if (sApplicationConnection != null) {
            mActivity.getApplicationContext().unbindService(sApplicationConnection);
            sApplicationConnection = null;
        }
        mState = STATE_DISCONNECTED;
        mOverlay = null;
        notifyStatusChanged(STATE_DISCONNECTED);
    }

    public void setOverlayEnabled(boolean isEnabled) {
        int newOptions = isEnabled ? 3 : 2;
        if (newOptions != mServiceConnectionOptions) {
            mServiceConnectionOptions = newOptions;
            if (mWindowAttrs != null) {
                applyWindowToken();
            }
        }

    }

    private void removeClient(boolean removeAppConnection) {
        if (!mDestroyed) {
            mActivity.unregisterReceiver(mUpdateReceiver);
        }

        mDestroyed = true;

        if (mNeedsServiceUnbind) {
            mActivity.unbindService(mServiceConnection);
            mNeedsServiceUnbind = false;
        }

        if (mCurrentCallbacks != null) {
            mCurrentCallbacks.clear();
            mCurrentCallbacks = null;
        }

        if (removeAppConnection && sApplicationConnection != null) {
            mActivity.getApplicationContext().unbindService(sApplicationConnection);
            sApplicationConnection = null;
        }
    }

    private void reconnect() {
        if (!mDestroyed && mServiceConnectionOptions == OPTIONS_FLAG_DEFAULT) {
            if (mState == STATE_DISCONNECTED) {
                XLog.d(XLog.getTag(), XLog.TAG_GU_STATE + " sApplicationConnection： " + sApplicationConnection);
                if (sApplicationConnection != null && !sApplicationConnection.packageName.equals(mServiceIntent.getPackage())) {
                    mActivity.getApplicationContext().unbindService(sApplicationConnection);
                    sApplicationConnection = null;
                }
                XLog.d(XLog.getTag(), XLog.TAG_GU_STATE + " sApplicationConnection2： " + sApplicationConnection);
                if (sApplicationConnection == null) {
                    sApplicationConnection = new AppServiceConnection(mServiceIntent.getPackage());
                    if (!connectSafely(mActivity.getApplicationContext(), sApplicationConnection, Context.BIND_WAIVE_PRIORITY)) {
                        sApplicationConnection = null;
                    }
                }
                XLog.d(XLog.getTag(), XLog.TAG_GU_STATE + " sApplicationConnection3： " + sApplicationConnection);
                if (sApplicationConnection != null) {
                    mState = STATE_CONNECTING;
                    if (connectSafely(mActivity, mServiceConnection, 192)) {
                        mNeedsServiceUnbind = true;
                    } else {
                        mState = STATE_DISCONNECTED;
                    }
                }
                if (mState == STATE_DISCONNECTED) {
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            notifyStatusChanged(STATE_DISCONNECTED);
                        }
                    });
                }
            }

        }
    }

    private boolean connectSafely(Context context, ServiceConnection conn, int flags) {
        try {
            Intent intent = new Intent(Constant.ACTION);
            intent.setPackage(Constant.GSA_PACKAGE);
            intent.setClass(context, CardService.class);
            return context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
//            return context.bindService(mServiceIntent, conn, flags | Context.BIND_AUTO_CREATE);
        } catch (SecurityException var5) {
            Log.e("LauncherClient", "Unable to connect to overlay service", var5);
            return false;
        }
    }

    private void setWindowAttrs(LayoutParams windowAttrs) {
        mWindowAttrs = windowAttrs;
        if (mWindowAttrs != null) {
            applyWindowToken();
        } else if (mOverlay != null) {
            try {
                mOverlay.windowDetached(mActivity.isChangingConfigurations());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mOverlay = null;
        }

    }

    private void applyWindowToken() {
        if (mOverlay != null) {
            try {
                if (mCurrentCallbacks == null) {
                    mCurrentCallbacks = new OverlayCallbacks();
                }

                mCurrentCallbacks.setClient(this);
                Log.d("LauncherClient", "applyWindowToken mServiceConnectionOptions=" + mServiceConnectionOptions);
                mOverlay.windowAttached(mWindowAttrs, mCurrentCallbacks, mServiceConnectionOptions);
                if (mIsResumed) {
                    mOverlay.onResume();
                } else {
                    mOverlay.onPause();
                }
                mOverlay.enableTransparentWallpaper(false);
                mOverlay.enableScroll(true, false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean isConnected() {
        return mOverlay != null;
    }

    public void startMove() {
        if (isConnected()) {
            try {
                mOverlay.startScroll();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    public void enableScroll(boolean left, boolean right) {
        if (isConnected()) {
            try {
                mOverlay.enableScroll(left, right);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void endMove() {
        if (isConnected()) {
            try {
                mOverlay.endScroll();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    public void updateMove(float progressX) {
        if (isConnected()) {
            try {
                mOverlay.onScroll(progressX);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    public void hideOverlay(boolean animate) {
        if (mOverlay != null) {
            try {
                mOverlay.closeOverlay(animate ? 1 : 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    public void showOverlay(boolean animate) {
        if (mOverlay != null) {
            try {
                mOverlay.openOverlay(animate ? 1 : 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    public void requestHotwordDetection(boolean start) {
        if (mOverlay != null) {
            try {
                mOverlay.requestVoiceDetection(start);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    void notifyStatusChanged(int status) {
        if (mServiceStatus != status) {
            mServiceStatus = status;
            mLauncherClientCallbacks.onServiceStateChanged((status & 1) != VERSION, (status & 2) != VERSION);
        }

    }

    static Intent getServiceIntent(Context context, String targetPackage) {
        String packageName = context.getPackageName();
//        return new Intent(Constant.ACTION)
//                .setPackage(Constant.GSA_PACKAGE)
//                .setData(Uri.parse(new StringBuilder(String.valueOf(packageName).length() + 18)
//                        .append("app://")
//                        .append(packageName)
//                        .append(":")
//                        .append(Process.myUid())
//                        .toString()).buildUpon()
//                        .appendQueryParameter("v", Integer.toString(9))
//                        .appendQueryParameter("cv", Integer.toString(14))
//                        .build());

        Intent intent = new Intent(Constant.ACTION);
        intent.setPackage(Constant.GSA_PACKAGE);
        return intent;
    }

    private static final class AppServiceConnection implements ServiceConnection {
        public final String packageName;

        AppServiceConnection(String pkg) {
            packageName = pkg;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        public void onServiceDisconnected(ComponentName name) {
            if (name.getPackageName().equals(packageName)) {
                LauncherClient.sApplicationConnection = null;
            }
        }
    }

    public Activity getActivity() {
        return mActivity;
    }

    int getServiceStatus() {
        return mServiceStatus;
    }

    LauncherClientCallbacks getLauncherClientCallbacks() {
        return mLauncherClientCallbacks;
    }

    private class OverlayServiceConnection implements ServiceConnection {
        private OverlayServiceConnection() {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            LauncherClient.this.mState = STATE_CONNECTED;
            LauncherClient.this.mOverlay = ILauncherOverlay.Stub.asInterface(service);
            if (LauncherClient.this.mWindowAttrs != null) {
                LauncherClient.this.applyWindowToken();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            LauncherClient.this.mState = STATE_DISCONNECTED;
            LauncherClient.this.mOverlay = null;
            LauncherClient.this.notifyStatusChanged(STATE_DISCONNECTED);
        }
    }
}
