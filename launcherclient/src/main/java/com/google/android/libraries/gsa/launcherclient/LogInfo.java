package com.google.android.libraries.gsa.launcherclient;

final class LogInfo {
    public int f37a;
    public String f38b;
    public float f39c;
    public long f40d;
    public int f41e;

    private LogInfo() {
    }

    public final void mo68a(int i, String str, float f) {
        this.f37a = i;
        this.f38b = str;
        this.f39c = f;
        this.f40d = System.currentTimeMillis();
        this.f41e = 0;
    }

    static int m83a(LogInfo eVar) {
        int i = eVar.f41e;
        eVar.f41e = i + 1;
        return i;
    }

    LogInfo(byte b) {
        this();
    }
}
