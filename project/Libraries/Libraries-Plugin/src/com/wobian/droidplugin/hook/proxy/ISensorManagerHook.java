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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
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
    private String STARTACTION="com.wobian.server.STARTSTEP";
    private String STOPACTION="com.wobian.server.STOPSTEP";
    private StepReceive mStepReceive = null;
    final Context receContext ;
    private static boolean isStart = false;
    private static Handler mThreadHander;
    private final static  int UPDATE_STEP_MESSAGE=1;
    public static volatile float tmpstepCount = 1000;
    private static boolean isFirst = true;

    private static boolean hasSendMessage = false;

    private static HashMap<SensorEventListener, SensorEvent> mMaps = new HashMap<SensorEventListener, SensorEvent>();

    public ISensorManagerHook(Context hostContext) {
        super(hostContext);
        isStart = true;
        receContext = hostContext;
        IntentFilter filter = new IntentFilter();
        filter.addAction(STARTACTION);
        filter.addAction(STOPACTION);
        mStepReceive = new StepReceive();
        hostContext.registerReceiver(mStepReceive, filter);
        HandlerThread mHandlerThread = new HandlerThread("MySensorListener");
        mHandlerThread.start();
        mThreadHander = new Handler(mHandlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case UPDATE_STEP_MESSAGE:
                        if(!isStart){
                            break;
                        }
                        MySensorListener msl = (MySensorListener) msg.obj;
                        if (msl != null && msl.mListener != null && mMaps != null){
                            Log.d(TAG,"startAutoStep mMaps length:"+mMaps.size());
                            startAutoStep(msl.mListener,mMaps.get(msl));
                        }
                        sendMessageDelayed(Message.obtain(msg), 1000);

                        break;
                    default:
                        super.handleMessage(msg);
                        break;
                }
            }
        };
    }

    public class StepReceive extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String Action = intent.getAction();
            if (STARTACTION.equals(Action)){
                fixSensorManager(receContext);
                isStart = true;
            }else if (STOPACTION.equals(Action)){
                isStart = false;
                hasSendMessage = false;
//                mMaps.clear();
            }
        }
    }
    @Override
    protected BaseHookHandle createHookHandle() {
        return new ISensorManagerHookHandle(mHostContext);
    }


    public Handler mMainHandler = new Handler();
    private void startAutoStep(final SensorEventListener listener,final SensorEvent event){
        synchronized (this) {
            Log.d(TAG, "run start isStart:"+isStart);
//            try {
                for(int i = 0; i<1; i++)  {
                    event.values[0]=tmpstepCount;
                    if (event.values[0] <11000) {
                        if(event.values[0] !=0 && event.values[0]%2 ==0){
                            event.accuracy =0;
                        }else{
                            event.accuracy =3;
                        }
                        if (isStart){
                            mMainHandler.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     listener.onSensorChanged(event);
                                 }
                            });
                            tmpstepCount=tmpstepCount+1;
                            Log.d(TAG, "tmpstepCount:"+tmpstepCount);
                        }
                    }

                }
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                Log.d(TAG, "InterruptedException:"+e.getMessage());
//            }
            Log.d(TAG, "run end ");
        }
    }


    static class MySensorListener implements SensorEventListener, InvocationHandler{
        private SensorEventListener mListener;
        public static volatile float stepCount=1;

        public MySensorListener(SensorEventListener listener) {
            mListener = listener;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
//            Log.d(TAG, "zhf dispatchSensorEvent tmpstepCount:"+tmpstepCount+" stepCount:"+stepCount+" t.accuracy:"+event.accuracy+""+event.values[0]);
            if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
                if(!hasSendMessage && isStart){
                    hasSendMessage = true;
                    mMaps.put(this, event);
                    Message m = Message.obtain();
                    m.obj = this;
                    m.what = UPDATE_STEP_MESSAGE;
                    mThreadHander.sendMessage(m);
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
    }

    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
        Log.d(TAG, "ISensorManagerHook  onInstall   classLoader="+classLoader);
//        fixSensorManager(mHostContext);
    }

    private static Object[] getCaches(Context context) throws IllegalAccessException {
        return (Object[]) FieldUtils.readField(context, "mServiceCache", true);
    }

    private static void fixSensorManager(Context context){
        if(!isFirst){
            return;
        }
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
                            isFirst = false;
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
//        Log.d(TAG, "ISensorManagerHook  fixContextSensorManager   context="+context.getBaseContext());
//        Message m = Message.obtain();
//        m.obj = context.getBaseContext();
//        mHandler.sendMessageDelayed(m, 5000);
    }
}