package com.finebot.nkv;

import android.content.Context;
import android.content.Intent;

import com.finebot.log.Log;
import com.finebot.utils.Utils;

public class DnSer1 extends BDSer {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            String from = intent.getStringExtra(EXTRA_FROM);
            onBaseDaemonAlive(this, from);
        }
        return result;
    }

    private static String getProcessName(Context context) {
        String cmdLine = Utils.getCmdLine();
        if (cmdLine != null && cmdLine.startsWith(context.getPackageName()) && cmdLine.contains(":")) {
            String substring = cmdLine.substring(cmdLine.lastIndexOf(":") + 1);
            return substring;
        }
        return context.getPackageName();
    }

    private static void onBaseDaemonAlive(Context context, String from) {
        String processName = getProcessName(context);
        Log.iv(Log.TAG, "start from : " + from + " , process name : " + processName);
    }
}
