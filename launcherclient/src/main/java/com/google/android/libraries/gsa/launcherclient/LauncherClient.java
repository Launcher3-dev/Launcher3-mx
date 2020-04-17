package com.google.android.libraries.gsa.launcherclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.launcherclient.Constant;
import com.google.android.libraries.launcherclient.ILauncherOverlayCallbackSub;
import com.google.android.libraries.launcherclient.ILauncherOverlay;

import java.io.PrintWriter;

public class LauncherClient {

    private static int sServiceVersion = -1;
    public final Activity mActivity;
    public final LauncherClientCallbacks mLauncherClientCallbacks;
    private final EventLogArray mClientLog;
    public final EventLogArray mServiceLog;
    public final SimpleServiceConnection mSimpleServiceConnection;
    public final AppServiceConnection sApplicationConnection;
    private final BroadcastReceiver mUpdateReceiver;
    private ILauncherOverlay mOverlay;
    public int mState;
    private boolean mDestroyed;
    public int mServiceStatus;
    private int mServiceConnectionOptions;
    private WindowManager.LayoutParams mWindowAttrs;
    private OverlayCallbacks mCurrentCallbacks;

    public static class ClientOptions {
        public final int f19a;

        public ClientOptions(boolean z, boolean z2, boolean z3) {
            int i;
            int i2;
            int i3 = 0;
            if (z) {
                i = 1;
            } else {
                i = 0;
            }
            int i4 = i | 0;
            if (z2) {
                i2 = 2;
            } else {
                i2 = 0;
            }
            this.f19a = (z3 ? 4 : i3) | i2 | i4;
        }
    }

    public LauncherClient(Activity activity) {
        this(activity, new LauncherClientCallbacksAdapter());
    }

    public LauncherClient(Activity activity, LauncherClientCallbacks launcherClientCallbacks) {
        this(activity, launcherClientCallbacks, new ClientOptions(true, true, true));
    }

    private static class OverlayCallbacks extends ILauncherOverlayCallbackSub implements Handler.Callback {

        private final Handler mUIHandler = new Handler(Looper.getMainLooper(), this);

        private LauncherClient mClient;

        private WindowManager mWindowManager;

        private int mWindowShift;

        private Window mWindow;

        private boolean mWindowHidden = false;

        OverlayCallbacks() {
        }

        public final void setClient(LauncherClient launcherClient) {
            this.mClient = launcherClient;
            this.mWindowManager = launcherClient.mActivity.getWindowManager();
            Point point = new Point();
            this.mWindowManager.getDefaultDisplay().getRealSize(point);
            this.mWindowShift = -Math.max(point.x, point.y);
            this.mWindow = launcherClient.mActivity.getWindow();
        }

        public final void clear() {
            this.mClient = null;
            this.mWindowManager = null;
            this.mWindow = null;
        }

        public final void overlayScrollChanged(float f) throws RemoteException {
            this.mUIHandler.removeMessages(2);
            Message.obtain(this.mUIHandler, 2, f).sendToTarget();
            if (f > 0.0f && this.mWindowHidden) {
                this.mWindowHidden = false;
            }
        }

        public final void overlayStatusChanged(int i) {
            Message.obtain(this.mUIHandler, 4, i, 0).sendToTarget();
        }

        public final boolean handleMessage(Message message) {
            if (this.mClient == null) {
                return true;
            }
            switch (message.what) {
                case 2:
                    if ((this.mClient.mServiceStatus & 1) != 0) {
                        float floatValue = (Float) message.obj;
                        this.mClient.mLauncherClientCallbacks.onOverlayScrollChanged(floatValue);
                        if (floatValue <= 0.0f) {
                            this.mClient.mServiceLog.mo63a("onScroll 0, overlay closed");
                        } else if (floatValue >= 1.0f) {
                            this.mClient.mServiceLog.mo63a("onScroll 1, overlay opened");
                        } else {
                            this.mClient.mServiceLog.mo64a("onScroll", floatValue);
                        }
                    }
                    return true;
                case 3:
                    WindowManager.LayoutParams attributes = this.mWindow.getAttributes();
                    if ((Boolean) message.obj) {
                        attributes.x = this.mWindowShift;
                        attributes.flags |= 512;
                    } else {
                        attributes.x = 0;
                        attributes.flags &= -513;
                    }
                    this.mWindowManager.updateViewLayout(this.mWindow.getDecorView(), attributes);
                    return true;
                case 4:
                    this.mClient.notifyStatusChanged(message.arg1);
                    this.mClient.mServiceLog.mo65a("stateChanged", message.arg1);
                    if (this.mClient.mLauncherClientCallbacks instanceof PrivateCallbacks) {
                        int i = message.arg1;
                        ((PrivateCallbacks) this.mClient.mLauncherClientCallbacks).mo71a();
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    public LauncherClient(Activity activity, LauncherClientCallbacks launcherClientCallbacks, ClientOptions clientOptions) {
        this.mClientLog = new EventLogArray("Client", 20);
        this.mServiceLog = new EventLogArray("Service", 10);
        this.mUpdateReceiver = new UpdateReceiver(this);
        this.mState = 0;
        this.mDestroyed = false;
        this.mServiceStatus = 0;
        this.mActivity = activity;
        this.mLauncherClientCallbacks = launcherClientCallbacks;
        this.mSimpleServiceConnection = new SimpleServiceConnection(activity, 65);
        this.mServiceConnectionOptions = clientOptions.f19a;
        this.sApplicationConnection = AppServiceConnection.m69a((Context) activity);
        this.mOverlay = this.sApplicationConnection.mo58a(this);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme("package");
        if (Build.VERSION.SDK_INT >= 19) {
            intentFilter.addDataSchemeSpecificPart(Constant.GSA_PACKAGE, 0);
        }
        this.mActivity.registerReceiver(this.mUpdateReceiver, intentFilter);
        if (sServiceVersion <= 0) {
            loadApiVersion(activity);
        }
        reconnect();
        if (Build.VERSION.SDK_INT >= 19 && this.mActivity.getWindow() != null
                && this.mActivity.getWindow().peekDecorView() != null
                && this.mActivity.getWindow().peekDecorView().isAttachedToWindow()) {
            onAttachedToWindow();
        }
    }

    public final void onAttachedToWindow() {
        if (!this.mDestroyed) {
            this.mClientLog.mo63a("attachedToWindow");
            setWindowAttrs(this.mActivity.getWindow().getAttributes());
        }
    }

    public final void onDetachedFromWindow() {
        if (!this.mDestroyed) {
            this.mClientLog.mo63a("detachedFromWindow");
            setWindowAttrs((WindowManager.LayoutParams) null);
        }
    }

    public void onResume() {
        if (!this.mDestroyed) {
            this.mState |= 2;
            if (!(this.mOverlay == null || this.mWindowAttrs == null)) {
                try {
                    if (sServiceVersion < 4) {
                        this.mOverlay.mo19d();
                    } else {
                        this.mOverlay.mo15b(this.mState);
                    }
                } catch (RemoteException e) {
                }
            }
            this.mClientLog.mo65a("stateChanged ", this.mState);
        }
    }

    public void onPause() {
        if (!this.mDestroyed) {
            this.mState &= -3;
            if (!(this.mOverlay == null || this.mWindowAttrs == null)) {
                try {
                    if (sServiceVersion < 4) {
                        this.mOverlay.mo17c();
                    } else {
                        this.mOverlay.mo15b(this.mState);
                    }
                } catch (RemoteException e) {
                }
            }
            this.mClientLog.mo65a("stateChanged ", this.mState);
        }
    }

    public void onStart() {
        if (!this.mDestroyed) {
            this.sApplicationConnection.mo60a(false);
            reconnect();
            this.mState |= 1;
            if (!(this.mOverlay == null || this.mWindowAttrs == null)) {
                try {
                    this.mOverlay.mo15b(this.mState);
                } catch (RemoteException e) {
                }
            }
            this.mClientLog.mo65a("stateChanged ", this.mState);
        }
    }

    public void onStop() {
        if (!this.mDestroyed) {
            this.sApplicationConnection.mo60a(true);
            this.mSimpleServiceConnection.unbindService();
            this.mState &= -2;
            if (!(this.mOverlay == null || this.mWindowAttrs == null)) {
                try {
                    this.mOverlay.mo15b(this.mState);
                } catch (RemoteException e) {
                }
            }
            this.mClientLog.mo65a("stateChanged ", this.mState);
        }
    }

    public void onDestroy() {
        m51a(!this.mActivity.isChangingConfigurations());
    }

    public void disconnect() {
        m51a(true);
    }

    public void setClientOptions(ClientOptions clientOptions) {
        if (clientOptions.f19a != this.mServiceConnectionOptions) {
            this.mServiceConnectionOptions = clientOptions.f19a;
            if (this.mWindowAttrs != null) {
                applyWindowToken();
            }
            this.mClientLog.mo65a("setClientOptions ", this.mServiceConnectionOptions);
        }
    }

    private final void m51a(boolean z) {
        if (!this.mDestroyed) {
            this.mActivity.unregisterReceiver(this.mUpdateReceiver);
        }
        this.mDestroyed = true;
        this.mSimpleServiceConnection.unbindService();
        if (this.mCurrentCallbacks != null) {
            this.mCurrentCallbacks.clear();
            this.mCurrentCallbacks = null;
        }
        this.sApplicationConnection.mo59a(this, z);
    }

    public void reconnect() {
        if (!this.mDestroyed) {
            if (!this.sApplicationConnection.mo74c() || !this.mSimpleServiceConnection.mo74c()) {
                this.mActivity.runOnUiThread(new NotifyStatusRunnable(this));
            }
        }
    }

    private final void setWindowAttrs(WindowManager.LayoutParams layoutParams) {
        if (this.mWindowAttrs != layoutParams) {
            this.mWindowAttrs = layoutParams;
            if (this.mWindowAttrs != null) {
                applyWindowToken();
            } else if (this.mOverlay != null) {
                try {
                    this.mOverlay.windowDetached(this.mActivity.isChangingConfigurations());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                this.mOverlay = null;
            }
        }
    }

    private final void applyWindowToken() {
        if (this.mOverlay != null) {
            try {
                if (this.mCurrentCallbacks == null) {
                    this.mCurrentCallbacks = new OverlayCallbacks();
                }
                this.mCurrentCallbacks.setClient(this);
                if (sServiceVersion < 3) {
                    this.mOverlay.windowAttached(this.mWindowAttrs, this.mCurrentCallbacks, this.mServiceConnectionOptions);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("layout_params", this.mWindowAttrs);
                    bundle.putParcelable("configuration", this.mActivity.getResources().getConfiguration());
                    bundle.putInt("client_options", this.mServiceConnectionOptions);
                    this.mOverlay.windowAttached2(bundle, this.mCurrentCallbacks);
                }
                if (sServiceVersion >= 4) {
                    this.mOverlay.mo15b(this.mState);
                } else if ((this.mState & 2) != 0) {
                    this.mOverlay.mo19d();
                } else {
                    this.mOverlay.mo17c();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private final boolean isConnected() {
        return this.mOverlay != null;
    }

    public void startMove() {
        this.mClientLog.mo63a("startMove");
        if (isConnected()) {
            try {
                this.mOverlay.startScroll();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void endMove() {
        this.mClientLog.mo63a("endMove");
        if (isConnected()) {
            try {
                this.mOverlay.endScroll();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateMove(float progressX) {
        this.mClientLog.mo64a("updateMove", progressX);
        if (isConnected()) {
            try {
                this.mOverlay.onScroll(progressX);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private static int m45a(int i) {
        if (i > 0 && i <= 2047) {
            return (i << 2) | 1;
        }
        throw new IllegalArgumentException("Invalid duration");
    }

    public void hideOverlay(boolean z) {
        this.mClientLog.mo67a("hideOverlay", z);
        if (this.mOverlay != null) {
            try {
                this.mOverlay.closeOverlay(z ? 1 : 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void hideOverlay(int i) {
        int a = m45a(i);
        this.mClientLog.mo65a("hideOverlay", i);
        if (this.mOverlay != null) {
            try {
                this.mOverlay.closeOverlay(a);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void showOverlay(boolean z) {
        this.mClientLog.mo67a("showOverlay", z);
        if (this.mOverlay != null) {
            try {
                this.mOverlay.openOverlay(z ? 1 : 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void showOverlay(int i) {
        int a = m45a(i);
        this.mClientLog.mo65a("showOverlay", i);
        if (this.mOverlay != null) {
            try {
                this.mOverlay.openOverlay(a);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void requestHotwordDetection(boolean z) {
        this.mClientLog.mo67a("requestHotwordDetection", z);
        if (this.mOverlay != null) {
            try {
                this.mOverlay.requestVoiceDetection(z);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void reattachOverlay() {
        this.mClientLog.mo63a("reattachOverlay");
        if (this.mWindowAttrs != null && sServiceVersion >= 7) {
            applyWindowToken();
        }
    }

    public final void mo28a(ILauncherOverlay aVar) {
        this.mServiceLog.mo67a("Connected", aVar != null);
        this.mOverlay = aVar;
        if (this.mOverlay == null) {
            notifyStatusChanged(0);
        } else if (this.mWindowAttrs != null) {
            applyWindowToken();
        }
    }

    public final void notifyStatusChanged(int status) {
        boolean z2 = true;
        if (this.mServiceStatus != status) {
            this.mServiceStatus = status;
            if ((status & 2) == 0) {
                z2 = false;
            }
            mLauncherClientCallbacks.onServiceStateChanged((status & 1) != 0, z2);
        }
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(String.valueOf(str).concat("LauncherClient"));
        String concat = String.valueOf(str).concat("  ");
        printWriter.println(new StringBuilder(concat.length() + 18).append(concat).append("isConnected: ").append(isConnected()).toString());
        printWriter.println(new StringBuilder(concat.length() + 18).append(concat).append("act.isBound: ").append(this.mSimpleServiceConnection.mo73b()).toString());
        printWriter.println(new StringBuilder(concat.length() + 18).append(concat).append("app.isBound: ").append(this.sApplicationConnection.mo73b()).toString());
        printWriter.println(new StringBuilder(concat.length() + 27).append(concat).append("serviceVersion: ").append(sServiceVersion).toString());
        printWriter.println(new StringBuilder(concat.length() + 17).append(concat).append("clientVersion: 14").toString());
        printWriter.println(new StringBuilder(concat.length() + 27).append(concat).append("mActivityState: ").append(this.mState).toString());
        printWriter.println(new StringBuilder(concat.length() + 27).append(concat).append("mServiceStatus: ").append(this.mServiceStatus).toString());
        printWriter.println(new StringBuilder(concat.length() + 45).append(concat).append("mCurrentServiceConnectionOptions: ").append(this.mServiceConnectionOptions).toString());
        this.mClientLog.print(concat, printWriter);
        this.mServiceLog.print(concat, printWriter);
    }

    static Intent getIntent(Context context) {
        String packageName = context.getPackageName();
        return new Intent(Constant.ACTION)
                .setPackage(Constant.GSA_PACKAGE)
                .setData(Uri.parse(new StringBuilder(String.valueOf(packageName).length() + 18)
                        .append("app://")
                        .append(packageName)
                        .append(":")
                        .append(Process.myUid())
                        .toString()).buildUpon()
                        .appendQueryParameter("v", Integer.toString(9))
                        .appendQueryParameter("cv", Integer.toString(14))
                        .build());
    }

    public static void loadApiVersion(Context context) {
        ResolveInfo resolveService = context.getPackageManager().resolveService(getIntent(context), PackageManager.GET_META_DATA);
        if (resolveService == null || resolveService.serviceInfo.metaData == null) {
            sServiceVersion = 1;
        } else {
            sServiceVersion = resolveService.serviceInfo.metaData.getInt("service.api.version", 1);
        }
    }
}
