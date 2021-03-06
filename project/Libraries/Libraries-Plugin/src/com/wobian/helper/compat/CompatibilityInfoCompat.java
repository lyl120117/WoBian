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

package com.wobian.helper.compat;

import com.wobian.droidplugin.reflect.FieldUtils;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/5/1.
 */
public class CompatibilityInfoCompat {

    private static Class sClass;

    private static Class getMyClass() throws ClassNotFoundException {
        if (sClass == null) {
            sClass = Class.forName("android.content.res.CompatibilityInfo");
        }
        return sClass;
    }

    private static Object sDefaultCompatibilityInfo;

    public static Object DEFAULT_COMPATIBILITY_INFO() throws IllegalAccessException, ClassNotFoundException {
        if (sDefaultCompatibilityInfo==null) {
            sDefaultCompatibilityInfo = FieldUtils.readStaticField(getMyClass(), "DEFAULT_COMPATIBILITY_INFO");
        }
        return sDefaultCompatibilityInfo;
    }
}
