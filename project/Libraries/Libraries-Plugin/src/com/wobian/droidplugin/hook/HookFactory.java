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

package com.wobian.droidplugin.hook;

import android.app.Application;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import com.wobian.droidplugin.hook.binder.IAlarmManagerBinderHook;
import com.wobian.droidplugin.hook.binder.IAppOpsServiceBinderHook;
import com.wobian.droidplugin.hook.binder.IAudioServiceBinderHook;
import com.wobian.droidplugin.hook.binder.IClipboardBinderHook;
import com.wobian.droidplugin.hook.binder.IContentServiceBinderHook;
import com.wobian.droidplugin.hook.binder.IDisplayManagerBinderHook;
import com.wobian.droidplugin.hook.binder.IGraphicsStatsBinderHook;
import com.wobian.droidplugin.hook.binder.IInputMethodManagerBinderHook;
import com.wobian.droidplugin.hook.binder.ILocationManagerBinderHook;
import com.wobian.droidplugin.hook.binder.IMediaRouterServiceBinderHook;
import com.wobian.droidplugin.hook.binder.IMmsBinderHook;
import com.wobian.droidplugin.hook.binder.IMountServiceBinderHook;
import com.wobian.droidplugin.hook.binder.INotificationManagerBinderHook;
import com.wobian.droidplugin.hook.binder.IPhoneSubInfoBinderHook;
import com.wobian.droidplugin.hook.binder.ISearchManagerBinderHook;
import com.wobian.droidplugin.hook.binder.ISessionManagerBinderHook;
import com.wobian.droidplugin.hook.binder.ISmsBinderHook;
import com.wobian.droidplugin.hook.binder.ISubBinderHook;
import com.wobian.droidplugin.hook.binder.ITelephonyBinderHook;
import com.wobian.droidplugin.hook.binder.ITelephonyRegistryBinderHook;
import com.wobian.droidplugin.hook.binder.IWifiManagerBinderHook;
import com.wobian.droidplugin.hook.binder.IWindowManagerBinderHook;
import com.wobian.droidplugin.hook.proxy.IActivityManagerHook;
import com.wobian.droidplugin.hook.proxy.IPackageManagerHook;
import com.wobian.droidplugin.hook.proxy.ISensorManagerHook;
import com.wobian.droidplugin.hook.proxy.InstrumentationHook;
import com.wobian.droidplugin.hook.proxy.LibCoreHook;
import com.wobian.droidplugin.hook.proxy.PluginCallbackHook;
import com.wobian.droidplugin.hook.xhook.SQLiteDatabaseHook;
import com.wobian.helper.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/2.
 */
public class HookFactory {

    private static final String TAG = HookFactory.class.getSimpleName();
    private static HookFactory sInstance = null;

    private HookFactory() {
    }

    public static HookFactory getInstance() {
        synchronized (HookFactory.class) {
            if (sInstance == null) {
                sInstance = new HookFactory();
            }
        }
        return sInstance;
    }


    private List<Hook> mHookList = new ArrayList<Hook>(3);

    public void setHookEnable(boolean enable) {
        synchronized (mHookList) {
            for (Hook hook : mHookList) {
                hook.setEnable(enable);
            }
        }
    }

    public void setHookEnable(boolean enable, boolean reinstallHook) {
        synchronized (mHookList) {
            for (Hook hook : mHookList) {
                hook.setEnable(enable, reinstallHook);
            }
        }
    }

    public void setHookEnable(Class hookClass, boolean enable) {
        synchronized (mHookList) {
            for (Hook hook : mHookList) {
                if (hookClass.isInstance(hook)) {
                    hook.setEnable(enable);
                }
            }
        }
    }

    public void installHook(Hook hook, ClassLoader cl) {
        try {
            hook.onInstall(cl);
            synchronized (mHookList) {
                mHookList.add(hook);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "installHook %s error", throwable, hook);
        }
    }


    public final void installHook(Context context, ClassLoader classLoader) throws Throwable {
        installHook(new IClipboardBinderHook(context), classLoader);
        //for ISearchManager
        installHook(new ISearchManagerBinderHook(context), classLoader);
        //for INotificationManager
//        installHook(new INotificationManagerBinderHook(context), classLoader);
        installHook(new IMountServiceBinderHook(context), classLoader);
        installHook(new IAudioServiceBinderHook(context), classLoader);
        installHook(new IAlarmManagerBinderHook(context), classLoader);
        installHook(new IContentServiceBinderHook(context), classLoader);
        installHook(new IWindowManagerBinderHook(context), classLoader);
        if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP_MR1) {
            installHook(new IGraphicsStatsBinderHook(context), classLoader);
        }
//        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
//            installHook(new WebViewFactoryProviderHook(context), classLoader);
//        }
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            installHook(new IMediaRouterServiceBinderHook(context), classLoader);
        }
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            installHook(new ISessionManagerBinderHook(context), classLoader);
        }
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
            installHook(new IWifiManagerBinderHook(context), classLoader);
        }

        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
            installHook(new IInputMethodManagerBinderHook(context), classLoader);
        }
        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            installHook(new ILocationManagerBinderHook(context), classLoader);
        }

        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            installHook(new ITelephonyRegistryBinderHook(context), classLoader);
        }

        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            installHook(new ISubBinderHook(context), classLoader);
        }

        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            installHook(new IPhoneSubInfoBinderHook(context), classLoader);
        }

        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            installHook(new ITelephonyBinderHook(context), classLoader);
        }

        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            installHook(new ISmsBinderHook(context), classLoader);
        }

        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            installHook(new IMmsBinderHook(context), classLoader);
        }

        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            installHook(new IAppOpsServiceBinderHook(context), classLoader);
        }

        installHook(new IPackageManagerHook(context), classLoader);
        installHook(new ISensorManagerHook(context), classLoader);
        installHook(new IActivityManagerHook(context), classLoader);
        installHook(new PluginCallbackHook(context), classLoader);
        installHook(new InstrumentationHook(context), classLoader);
        installHook(new LibCoreHook(context), classLoader);

        installHook(new SQLiteDatabaseHook(context), classLoader);

        installHook(new IDisplayManagerBinderHook(context), classLoader);
    }

    public final void onCallApplicationOnCreate(Context context, Application app) {
        installHook(new SQLiteDatabaseHook(context), app.getClassLoader());
    }
}
