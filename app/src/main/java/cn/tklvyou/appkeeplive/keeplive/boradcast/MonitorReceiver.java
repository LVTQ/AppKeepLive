package cn.tklvyou.appkeeplive.keeplive.boradcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.tklvyou.appkeeplive.keeplive.foreground.DaemonService;


public class MonitorReceiver extends BroadcastReceiver {
    private static final String TAG = "MonitorReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive(): intent: " + intent.toUri(0));
        Intent target = new Intent(context, DaemonService.class);
        context.startService(target);
    }

}