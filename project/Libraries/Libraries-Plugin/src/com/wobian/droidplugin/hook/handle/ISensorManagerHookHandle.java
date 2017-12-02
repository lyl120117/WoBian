/*
**        DroidPlugin Project
**
** Copyright(c) 2015 Andy Zhang <zhangyong232@gmail.com>
**
** This file is part of DroidPlugin.
**
** DroidPlugin is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** DroidPlugin is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with DroidPlugin.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/

package com.wobian.droidplugin.hook.handle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.IServiceConnection;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;

import com.wobian.droidplugin.PluginManagerService;
import com.wobian.droidplugin.PluginPatchManager;
import com.wobian.droidplugin.am.RunningActivities;
import com.wobian.droidplugin.core.Env;
import com.wobian.droidplugin.core.PluginProcessManager;
import com.wobian.droidplugin.hook.BaseHookHandle;
import com.wobian.droidplugin.hook.proxy.IContentProviderHook;
import com.wobian.droidplugin.hook.proxy.ISensorManagerHook;
import com.wobian.droidplugin.pm.PluginManager;
import com.wobian.droidplugin.reflect.FieldUtils;
import com.wobian.droidplugin.reflect.MethodUtils;
import com.wobian.droidplugin.reflect.Utils;
import com.wobian.droidplugin.stub.MyFakeIBinder;
import com.wobian.droidplugin.stub.ServcesManager;
import com.wobian.droidplugin.stub.ShortcutProxyActivity;
import com.wobian.helper.Log;
import com.wobian.helper.MyProxy;
import com.wobian.helper.compat.ActivityManagerCompat;
import com.wobian.helper.compat.ContentProviderHolderCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/2/28.
 */
public class ISensorManagerHookHandle extends BaseHookHandle {

    private static final String TAG = ISensorManagerHook.class.getSimpleName();

    public ISensorManagerHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("registerListener", new registerListener(mHostContext));


    }

    private static class registerListener extends ReplaceCallingPackageHookedMethodHandler {

        public registerListener(Context hostContext) {
            super(hostContext);
        }


        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {

            Log.d(TAG, "ISensorManagerHookHandle    beforeInvoke    "+receiver.getClass().getName()
                +", "+method.getName()+", "+args.length);
            return super.beforeInvoke(receiver, method, args);
        }
    }
}
