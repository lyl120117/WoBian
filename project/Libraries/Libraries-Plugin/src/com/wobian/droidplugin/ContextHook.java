package com.wobian.droidplugin;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.view.Display;

import com.wobian.droidplugin.hook.binder.IInputMethodManagerBinderHook;
import com.wobian.droidplugin.hook.binder.MyServiceManager;
import com.wobian.droidplugin.reflect.MethodUtils;
import com.wobian.helper.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by liyangliu on 2017/12/18.
 */

public class ContextHook extends Context {
    private final static String TAG = ContextHook.class.getSimpleName();
    private Context origin;
    public ContextHook(Context context){
        origin = context;
    }

    public String getBasePackageName(){
        try {
            return (String) MethodUtils.invokeMethod(origin, "getBasePackageName");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getOpPackageName(){
        try {
            return (String) MethodUtils.invokeMethod(origin, "getOpPackageName");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getUserId() {
        try {
            return (Integer) MethodUtils.invokeMethod(origin, "getUserId");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public AssetManager getAssets() {
        return origin.getAssets();
    }

    @Override
    public Resources getResources() {
        return origin.getResources();
    }

    @Override
    public PackageManager getPackageManager() {
        return origin.getPackageManager();
    }

    @Override
    public ContentResolver getContentResolver() {
        return origin.getContentResolver();
    }

    @Override
    public Looper getMainLooper() {
        return origin.getMainLooper();
    }

    @Override
    public Context getApplicationContext() {
        return origin.getApplicationContext();
    }

    @Override
    public void setTheme(int resid) {
        origin.setTheme(resid);
    }

    @Override
    public Resources.Theme getTheme() {
        return origin.getTheme();
    }

    @Override
    public ClassLoader getClassLoader() {
        return origin.getClassLoader();
    }

    @Override
    public String getPackageName() {
        return origin.getPackageName();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return origin.getApplicationInfo();
    }

    @Override
    public String getPackageResourcePath() {
        return origin.getPackageResourcePath();
    }

    @Override
    public String getPackageCodePath() {
        return origin.getPackageCodePath();
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return origin.getSharedPreferences(name, mode);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean moveSharedPreferencesFrom(Context sourceContext, String name) {
        return origin.moveSharedPreferencesFrom(sourceContext, name);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean deleteSharedPreferences(String name) {
        return origin.deleteSharedPreferences(name);
    }

    @Override
    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        return origin.openFileInput(name);
    }

    @Override
    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        return origin.openFileOutput(name, mode);
    }

    @Override
    public boolean deleteFile(String name) {
        return origin.deleteFile(name);
    }

    @Override
    public File getFileStreamPath(String name) {
        return getFileStreamPath(name);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public File getDataDir() {
        return origin.getDataDir();
    }

    @Override
    public File getFilesDir() {
        return origin.getFilesDir();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public File getNoBackupFilesDir() {
        return origin.getNoBackupFilesDir();
    }

    @Override
    public File getExternalFilesDir(String type) {
        return origin.getExternalFilesDir(type);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public File[] getExternalFilesDirs(String type) {
        return origin.getExternalFilesDirs(type);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public File getObbDir() {
        return origin.getObbDir();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public File[] getObbDirs() {
        return origin.getObbDirs();
    }

    @Override
    public File getCacheDir() {
        return origin.getCacheDir();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public File getCodeCacheDir() {
        return origin.getCodeCacheDir();
    }

    @Override
    public File getExternalCacheDir() {
        return origin.getExternalCacheDir();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public File[] getExternalCacheDirs() {
        return origin.getExternalCacheDirs();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public File[] getExternalMediaDirs() {
        return origin.getExternalMediaDirs();
    }

    @Override
    public String[] fileList() {
        return origin.fileList();
    }

    @Override
    public File getDir(String name, int mode) {
        return origin.getDir(name, mode);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return origin.openOrCreateDatabase(name, mode, factory);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return origin.openOrCreateDatabase(name, mode, factory, errorHandler);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean moveDatabaseFrom(Context sourceContext, String name) {
        return origin.moveDatabaseFrom(sourceContext, name);
    }

    @Override
    public boolean deleteDatabase(String name) {
        return origin.deleteDatabase(name);
    }

    @Override
    public File getDatabasePath(String name) {
        return origin.getDatabasePath(name);
    }

    @Override
    public String[] databaseList() {
        return origin.databaseList();
    }

    @Override
    public Drawable getWallpaper() {
        return origin.getWallpaper();
    }

    @Override
    public Drawable peekWallpaper() {
        return origin.peekWallpaper();
    }

    @Override
    public int getWallpaperDesiredMinimumWidth() {
        return origin.getWallpaperDesiredMinimumWidth();
    }

    @Override
    public int getWallpaperDesiredMinimumHeight() {
        return origin.getWallpaperDesiredMinimumHeight();
    }

    @Override
    public void setWallpaper(Bitmap bitmap) throws IOException {
        origin.setWallpaper(bitmap);
    }

    @Override
    public void setWallpaper(InputStream data) throws IOException {
        origin.setWallpaper(data);
    }

    @Override
    public void clearWallpaper() throws IOException {
        origin.clearWallpaper();
    }

    @Override
    public void startActivity(Intent intent) {
        origin.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivity(Intent intent, Bundle options) {
        origin.startActivity(intent, options);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void startActivities(Intent[] intents) {
        origin.startActivities(intents);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivities(Intent[] intents, Bundle options) {
        origin.startActivities(intents, options);
    }

    @Override
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        origin.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        origin.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    @Override
    public void sendBroadcast(Intent intent) {
        origin.sendBroadcast(intent);
    }

    @Override
    public void sendBroadcast(Intent intent, String receiverPermission) {
        origin.sendBroadcast(intent, receiverPermission);
    }

    @Override
    public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
        origin.sendOrderedBroadcast(intent, receiverPermission);
    }

    @Override
    public void sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        origin.sendOrderedBroadcast(intent, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user) {
        origin.sendBroadcastAsUser(intent, user);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission) {
        origin.sendBroadcastAsUser(intent, user, receiverPermission);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        origin.sendBroadcastAsUser(intent, user, receiverPermission);
    }

    @Override
    public void sendStickyBroadcast(Intent intent) {
        origin.sendStickyBroadcast(intent);
    }

    @Override
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        origin.sendStickyOrderedBroadcast(intent, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override
    public void removeStickyBroadcast(Intent intent) {
        origin.removeStickyBroadcast(intent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {
        origin.sendStickyBroadcastAsUser(intent, user);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        origin.sendStickyOrderedBroadcastAsUser(intent, user, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {
        origin.removeStickyBroadcastAsUser(intent, user);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return origin.registerReceiver(receiver, filter);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags) {
        return origin.registerReceiver(receiver, filter, flags);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        return origin.registerReceiver(receiver, filter, broadcastPermission, scheduler);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler, int flags) {
        return origin.registerReceiver(receiver, filter, broadcastPermission, scheduler, flags);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        origin.unregisterReceiver(receiver);
    }

    @Override
    public ComponentName startService(Intent service) {
        return origin.startService(service);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public ComponentName startForegroundService(Intent service) {
        return origin.startForegroundService(service);
    }



    @Override
    public boolean stopService(Intent service) {
        return origin.stopService(service);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return origin.bindService(service, conn, flags);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        origin.unbindService(conn);
    }

    @Override
    public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
        return origin.startInstrumentation(className, profileFile, arguments);
    }

    private ArrayList<String> mFixedApps = new ArrayList<String>(){
        {
            add(IInputMethodManagerBinderHook.SERVICE_NAME);
        }
    };
    @Override
    public Object getSystemService(String name) {
        Log.d(TAG, "getSystemService    name="+name);

        Object originObj = origin.getSystemService(name);
        if(mFixedApps.contains(name)){
            if(IInputMethodManagerBinderHook.SERVICE_NAME.equals(name)){
                IInputMethodManagerBinderHook.fixedInputMethod(originObj);
            }
        }
        return originObj;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public String getSystemServiceName(Class<?> serviceClass) {
        return origin.getSystemServiceName(serviceClass);
    }

    @Override
    public int checkPermission(String permission, int pid, int uid) {
        return origin.checkPermission(permission, pid, uid);
    }

    @Override
    public int checkCallingPermission(String permission) {
        return origin.checkCallingPermission(permission);
    }

    @Override
    public int checkCallingOrSelfPermission(String permission) {
        return origin.checkCallingOrSelfPermission(permission);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public int checkSelfPermission(String permission) {
        return origin.checkSelfPermission(permission);
    }

    @Override
    public void enforcePermission(String permission, int pid, int uid, String message) {
        origin.enforcePermission(permission, pid, uid, message);
    }

    @Override
    public void enforceCallingPermission(String permission, String message) {
        origin.enforceCallingPermission(permission, message);
    }

    @Override
    public void enforceCallingOrSelfPermission(String permission, String message) {
        origin.enforceCallingOrSelfPermission(permission, message);
    }

    @Override
    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
        origin.grantUriPermission(toPackage, uri, modeFlags);
    }

    @Override
    public void revokeUriPermission(Uri uri, int modeFlags) {
        origin.revokeUriPermission(uri, modeFlags);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void revokeUriPermission(String toPackage, Uri uri, int modeFlags) {
        origin.revokeUriPermission(toPackage, uri, modeFlags);
    }

    @Override
    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        return origin.checkUriPermission(uri, pid, uid, modeFlags);
    }

    @Override
    public int checkCallingUriPermission(Uri uri, int modeFlags) {
        return origin.checkCallingUriPermission(uri, modeFlags);
    }

    @Override
    public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
        return origin.checkCallingOrSelfUriPermission(uri, modeFlags);
    }

    @Override
    public int checkUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags) {
        return checkUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags);
    }

    @Override
    public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {
        origin.enforceUriPermission(uri, pid, uid, modeFlags, message);
    }

    @Override
    public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {
        origin.enforceCallingUriPermission(uri, modeFlags, message);
    }

    @Override
    public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {
        origin.enforceCallingOrSelfUriPermission(uri, modeFlags, message);
    }

    @Override
    public void enforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags, String message) {
        origin.enforceUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags, message);
    }

    @Override
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        return origin.createPackageContext(packageName, flags);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public Context createContextForSplit(String splitName) throws PackageManager.NameNotFoundException {
        return origin.createContextForSplit(splitName);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public Context createConfigurationContext(Configuration overrideConfiguration) {
        return origin.createConfigurationContext(overrideConfiguration);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public Context createDisplayContext(Display display) {
        return origin.createDisplayContext(display);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public Context createDeviceProtectedStorageContext() {
        return origin.createDeviceProtectedStorageContext();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean isDeviceProtectedStorage() {
        return origin.isDeviceProtectedStorage();
    }
}
