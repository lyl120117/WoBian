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

package com.wobian.droidplugin.hook.binder;

import android.content.Context;
import android.os.IBinder;

import com.wobian.droidplugin.hook.BaseHookHandle;
import com.wobian.droidplugin.hook.handle.IAlarmManagerHandle;
import com.wobian.helper.compat.IAlarmManagerCompat;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/6.
 */
public class IAlarmManagerBinderHook extends BinderHook {

    private final static String SERVICE_NAME = Context.ALARM_SERVICE;

    public IAlarmManagerBinderHook(Context hostContext) {
        super(hostContext);
    }

    @Override
    public Object getOldObj() throws Exception {
        IBinder iBinder = MyServiceManager.getOriginService(SERVICE_NAME);
        return IAlarmManagerCompat.asInterface(iBinder);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new IAlarmManagerHandle(mHostContext);
    }
}
