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

package com.wobian.droidplugin.hook.proxy;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

import com.wobian.droidplugin.hook.BaseHookHandle;
import com.wobian.droidplugin.hook.handle.ISensorManagerHookHandle;
import com.wobian.droidplugin.reflect.FieldUtils;
import com.wobian.droidplugin.reflect.Utils;
import com.wobian.helper.Log;
import com.wobian.helper.MyProxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;


/**
 * Hook some function on IPackageManager
 * <p/>
 * Code by Andy Zhang (zhangyong232@gmail.com) on  2015/2/5.
 */
public class ISensorManagerHook extends ProxyHook {

    private static final String TAG = ISensorManagerHook.class.getSimpleName();

    public ISensorManagerHook(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new ISensorManagerHookHandle(mHostContext);
    }


    static class MySensorListener implements SensorEventListener, InvocationHandler{
        private SensorEventListener mListener;

        public MySensorListener(SensorEventListener listener){
            mListener = listener;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            //Log.d(TAG, "zhf dispatchSensorEvent tmpstepCount:"+tmpstepCount+" stepCount:"+stepCount+" t.accuracy:"+event.accuracy);
            if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
                if(myThread == null){
                    Log.d(TAG, "myThread id null:"+(myThread == null));
                    myThread = new MyThread(mListener, event);
                    myThread.start();
                }
            }else {
                mListener.onSensorChanged(event);
            }
//            Log.d(TAG, "onSensorChanged  event="+event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            mListener.onAccuracyChanged(sensor, accuracy);
            Log.d(TAG, "onAccuracyChanged  sensor="+sensor);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "MySensorListener  invoke   method="+method.getName());
            return null;
        }
        public static volatile float stepCount=1;
        public static volatile float tmpstepCount = 0;
        public static volatile float realyCount = 0;
        public boolean stepflag = false;
        MyThread myThread = null;
        public class MyThread extends Thread {
            private SensorEventListener mListener;
            private SensorEvent st;
            public MyThread(SensorEventListener listener,SensorEvent t){
                this.mListener = listener;
                this.st = t;
            }
            public void run(){
                Log.d(TAG, "run");
                synchronized (this) {
                    try {
                        for(int i = 0; i<5000; i++)  {
                            st.values[0]=tmpstepCount;
                            if (st.values[0] <16000) {
                                if(st.values[0] !=0 && st.values[0]%2 ==0){
                                    st.accuracy =0;
                                }else{
                                    st.accuracy =3;
                                }
                                mListener.onSensorChanged(st);
                                Thread.sleep(300);
                                tmpstepCount=tmpstepCount+2;
                            }
                            Log.d(TAG, "tmpstepCount:"+tmpstepCount+" :"+stepCount);
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        Log.d(TAG, "InterruptedException:"+e.getMessage());
                    }
                    myThread = null;
                    stepflag = false;
                }
            }
        }
    }

    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
        Log.d(TAG, "ISensorManagerHook  onInstall   classLoader="+classLoader);
        fixSensorManager(mHostContext);
    }

    private static Object[] getCaches(Context context) throws IllegalAccessException {
        return (Object[]) FieldUtils.readField(context, "mServiceCache", true);
    }

    private static void fixSensorManager(Context context){
        try {
            Object currentSensorManager = context.getSystemService(Context.SENSOR_SERVICE);
            Log.d(TAG, "ISensorManagerHook  fixSensorManager   currentSensorManager="+currentSensorManager+", context="+context);
            Object[] objects = getCaches(context);
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];
                if(object instanceof SensorManager){
                    Object listener = FieldUtils.readField(object, "mSensorListeners", true);
                    HashMap map = (HashMap) listener;
                    HashMap newMap = new HashMap();
                    Log.d(TAG, "ISensorManagerHook  fixSensorManager   object="+object+", map="+map);
                    if(map.size() > 0){
                        for (Object key : map.keySet()) {
                            SensorEventListener sel = (SensorEventListener) key;
                            Object value = map.get(key);
                            Class<?> selClass = sel.getClass();
                            List<Class<?>> interfaces = Utils.getAllInterfaces(selClass);
                            Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
                            Object newSm = MyProxy.newProxyInstance(selClass.getClassLoader(), ifs, new MySensorListener(sel));
//                            map.put(newSm, value);
                            SensorEventListener newSel = new MySensorListener(sel);
//                            map.put(newSel, value);
                            newMap.put(newSel, value);
                            Log.d(TAG, "ISensorManagerHook  fixSensorManager   sel="+sel+", newSm="+newSm
                                +", newSel="+newSel);


                            //
                            Class cls = Class.forName("android.hardware.SystemSensorManager$SensorEventQueue");
                            Field field = cls.getDeclaredField("mListener");
                            FieldUtils.writeField(field, value, newSel);
                            Log.d(TAG, "ISensorManagerHook  fixSensorManager   writeField mListener success  newSel="+newSel);

                        }

                        FieldUtils.writeField(object, "mSensorListeners", newMap);
                        Log.d(TAG, "ISensorManagerHook  fixSensorManager   writeField mSensorListeners success  newMap="+newMap);
                    }
                }
            }

            FieldUtils.writeField(context, "mServiceCache", objects);
            Log.d(TAG, "ISensorManagerHook  fixSensorManager   writeField mServiceCache success  sm="+objects);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private static final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            Context context = (Context) msg.obj;
            fixSensorManager(context);
        }
    };
    public static void fixContextSensorManager(Activity context){
        Log.d(TAG, "ISensorManagerHook  fixContextSensorManager   context="+context.getBaseContext());
        Message m = Message.obtain();
        m.obj = context.getBaseContext();
        mHandler.sendMessageDelayed(m, 5000);
    }
}