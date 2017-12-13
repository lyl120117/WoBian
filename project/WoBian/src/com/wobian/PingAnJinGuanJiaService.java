package com.wobian;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.alipay.euler.andfix.AndFix;
import com.wobian.droidplugin.hook.binder.MyServiceManager;
import com.wobian.helper.Log;
import com.wobian.zhook.AndFixManager;

import java.lang.reflect.Method;

/**
 * Created by liyangliu on 2017/12/4.
 */

public class PingAnJinGuanJiaService extends Service {
    private final static String TAG = PingAnJinGuanJiaService.class.getSimpleName();
    private WindowManager mWm;
    private WindowManager.LayoutParams mParams;
    private FrameLayout mToucherLayout;
    private int mScreenWidth, mScreenHeight;


    private Utils mUtils;

    //状态栏高度.
    private int mStatusBarHeight = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Log.d(TAG, "onCreate   mWm="+mWm);
        AndFixManager afm = new AndFixManager(this);

        mUtils = new Utils();
        testFix();
//        mWm = (WindowManager) MyServiceManager.getOriginService(Context.WINDOW_SERVICE);
        Log.d(TAG, "onCreate      add=");
        Log.d(TAG, "onCreate      add="+mUtils.sub(5, 10));
        createToucher();
    }

    private void testFix(){
        try {
            Method origin = mUtils.getClass().getDeclaredMethod("add",new Class[]{int.class, int.class} );
            Method method = mUtils.getClass().getDeclaredMethod("sub", new Class[]{int.class, int.class});
            AndFix.addReplaceMethod(origin, method);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void createToucher() {
        //赋值WindowManager&LayoutParam.
        mParams = new WindowManager.LayoutParams();
        mWm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置效果为背景透明.
        mParams.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        mScreenWidth = mWm.getDefaultDisplay().getWidth();
        mScreenHeight = mWm.getDefaultDisplay().getHeight();

        //设置窗口初始停靠位置.
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.x = 0;
        mParams.y = mScreenHeight/2;

        //设置悬浮窗口长宽数据.
        mParams.width = 150;
        mParams.height = 150;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        mToucherLayout = (FrameLayout) inflater.inflate(R.layout.toucherlayout, null);
        //添加toucherlayout
        mWm.addView(mToucherLayout, mParams);


//        mToucherLayout.getMeasuredWidth();
//        mToucherLayout.getMeasuredHeight();
//
//        //主动计算出当前View的宽高信息.
//        mToucherLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);


        //View bgView = toucherLayout.findViewById(R.id.bg);
        //toucherLayout.setBackground(new BitmapDrawable(ViewToBitmapUtil.convertViewToBitmap(bgView)));

        //用于检测状态栏高度.
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            mStatusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Log.i(TAG, "状态栏高度为:" + mStatusBarHeight);



    }
}
