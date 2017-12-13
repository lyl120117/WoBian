package com.wobian;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UtilSingle {
    private String TAG="wobian_UtilSingle";
    private static Boolean isOpen = false;
    private Context mContext;
    private static UtilSingle instance;
    private UtilSingle(Context context){
        mContext = context;
    }

    public static UtilSingle getInstance(Context context){
        if (instance ==null) {
            instance = new UtilSingle(context);
        }
        return instance;
    }

    public void startFloatWindowService(String packageName){
        if (!isOpen) {
            isOpen = true;
            Log.d(TAG, "startFloatWindowService");
            Intent intent = new Intent(mContext, JinguanjaFloatWindowService.class);
            intent.putExtra("CurrentPackageName", packageName);
            mContext.startService(intent);
        }
    }

    public void stopFloatWindowService(){
        if (isOpen) {
            isOpen = false;
            Log.d(TAG, "stopFloatWindowService");
            Intent intent = new Intent(mContext, JinguanjaFloatWindowService.class);
            mContext.stopService(intent);
        }
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
