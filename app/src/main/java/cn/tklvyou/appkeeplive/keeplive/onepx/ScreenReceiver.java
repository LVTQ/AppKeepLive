package cn.tklvyou.appkeeplive.keeplive.onepx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import cn.tklvyou.appkeeplive.LiveApplication;


public class ScreenReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenReceiver";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private CheckTopTask mCheckTopTask = new CheckTopTask(LiveApplication.getAppContext());

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "onReceive(): context = [" + context + "], intent = [" + intent + "]");
        String action = intent.getAction();
        // 这里可以启动一些服务
        try {
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                Log.i(TAG, "锁屏开启一像素");
                CheckTopTask.startForeground(context);
                mHandler.postDelayed(mCheckTopTask, 3000);
            } else if (Intent.ACTION_USER_PRESENT.equals(action) || Intent.ACTION_SCREEN_ON.equals(action)) {
                Log.i(TAG, "开屏关闭一像素");
                OnePxActivity onePxActivity = OnePxActivity.instance != null ? OnePxActivity.instance.get() : null;
                if (onePxActivity != null) {
                    onePxActivity.finishSelf();
                }
                mHandler.removeCallbacks(mCheckTopTask);
            }
        } catch (Exception e) {
            Log.e(TAG, "e:", e);
        }
    }
}