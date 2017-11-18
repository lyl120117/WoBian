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

package com.morgoo.droidplugin.hook.handle;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.Log;

import java.lang.reflect.Method;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/6.
 */
public class IAlarmManagerHandle extends BaseHookHandle {

    public IAlarmManagerHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("set", new set(mHostContext));

    }

    private static class MyBaseHandler extends HookedMethodHandler {

        public MyBaseHandler(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            Log.d("LYL", "beforeInvoke   receiver=" + receiver);
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                if (args != null && args.length > 0) {
                    for (int index = 0; index < args.length; index++) {
                        if (args[index] instanceof String) {
                            String callingPkg = (String) args[index];
                            if (!TextUtils.equals(callingPkg, mHostContext.getPackageName()) && PluginManager.getInstance().isPluginPackage(callingPkg)) {
                                args[index] = mHostContext.getPackageName();
                            }
                        }
                    }
                }

            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private static class set extends MyBaseHandler {
        public set(Context context) {
            super(context);
        }
    }
}

