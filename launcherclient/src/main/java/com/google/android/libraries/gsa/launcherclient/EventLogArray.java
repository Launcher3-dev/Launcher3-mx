package com.google.android.libraries.gsa.launcherclient;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class EventLogArray {

    private final String tag;

    private final LogInfo[] logInfos;

    private int f36c = 0;

    public EventLogArray(String str, int i) {
        this.tag = str;
        this.logInfos = new LogInfo[i];
    }

    public final void mo63a(String str) {
        m76a(0, str, 0.0f);
    }

    public final void mo65a(String str, int i) {
        m76a(2, str, (float) i);
    }

    public final void mo64a(String str, float f) {
        m76a(1, str, f);
    }

    public final void mo67a(String str, boolean z) {
        m76a(z ? 3 : 4, str, 0.0f);
    }

    private final void m76a(int i, String str, float f) {
        int length = ((this.f36c + this.logInfos.length) - 1) % this.logInfos.length;
        int length2 = ((this.f36c + this.logInfos.length) - 2) % this.logInfos.length;
        if (!m77a(this.logInfos[length], i, str) || !m77a(this.logInfos[length2], i, str)) {
            if (this.logInfos[this.f36c] == null) {
                this.logInfos[this.f36c] = new LogInfo((byte) 0);
            }
            this.logInfos[this.f36c].mo68a(i, str, f);
            this.f36c = (this.f36c + 1) % this.logInfos.length;
            return;
        }
        this.logInfos[length].mo68a(i, str, f);
        LogInfo.m83a(this.logInfos[length2]);
    }

    public final void print(String str, PrintWriter printWriter) {
        String str2 = this.tag;
        printWriter.println(new StringBuilder(String.valueOf(str).length() + 15 + String.valueOf(str2).length()).append(str).append(str2).append(" event history:").toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("  HH:mm:ss.SSSZ  ", Locale.US);
        Date date = new Date();
        for (int i = 0; i < this.logInfos.length; i++) {
            LogInfo logInfo = this.logInfos[(((this.f36c + this.logInfos.length) - i) - 1) % this.logInfos.length];
            if (logInfo != null) {
                date.setTime(logInfo.f40d);
                StringBuilder append = new StringBuilder(str).append(simpleDateFormat.format(date)).append(logInfo.f38b);
                switch (logInfo.f37a) {
                    case 1:
                        append.append(": ").append(logInfo.f39c);
                        break;
                    case 2:
                        append.append(": ").append((int) logInfo.f39c);
                        break;
                    case 3:
                        append.append(": true");
                        break;
                    case 4:
                        append.append(": false");
                        break;
                }
                if (logInfo.f41e > 0) {
                    append.append(" & ").append(logInfo.f41e).append(" similar events");
                }
                printWriter.println(append);
            }
        }
    }

    private static boolean m77a(LogInfo eVar, int i, String str) {
        return eVar != null && eVar.f37a == i && eVar.f38b.equals(str);
    }
}
