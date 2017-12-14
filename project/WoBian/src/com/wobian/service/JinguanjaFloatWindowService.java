package com.wobian.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.app.Service;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wobian.util.CommandExecution;
import com.wobian.R;

/**
 * Created by zhuhaifeng on 2017/12/13.
 */

public class JinguanjaFloatWindowService extends Service{
    public static String TAG= "JinguanjaFloatWindowService";
    public WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;
    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Log.i(TAG, "FloatWindowService onStart");
        if (intent != null) {
            CreateInputWindow();
        }else {
            onDestroy();
        }
    }

    private float mTouchX;
    private float mTouchY;
    private float x;
    private float y;
    private int currentX = -1;
    private int currentY = -1;
    private boolean  isMove = false;
    private boolean  isRight = false;
    private boolean  currentOri = true;
    private boolean  lastMoveState=false;
    private LinearLayout mFloatLayout;
    private Button mFloatView,startStep,stopStep;
    private EditText mText;

    public void CreateInputWindow(){
        wmParams = new WindowManager.LayoutParams();
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // To the upper left corner of the screen as the origin, set X, y initial value, relative to the gravity
        if (currentX != -1 &&  currentY != -1) {
            wmParams.x =currentX;
            wmParams.y = currentY;
        }else {
            DisplayMetrics dm = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(dm);
            wmParams.x = dm.widthPixels;
            wmParams.y = dm.heightPixels/5;
        }
        //Set the suspended window length and width data
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //Gets the layout of the floating window view
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        //add mFloatLayout to window
        mWindowManager.addView(mFloatLayout, wmParams);
        mFloatView = (Button)mFloatLayout.findViewById(R.id.float_id);
        startStep =  (Button)mFloatLayout.findViewById(R.id.startstep);
        stopStep = (Button)mFloatLayout.findViewById(R.id.stopstep);
        mFloatView.setText("发送");
        mFloatView.setOnClickListener(listener);
        startStep.setOnClickListener(listener);
        stopStep.setOnClickListener(listener);
        mText =(EditText)mFloatLayout.findViewById(R.id.float_edit);
        mFloatView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                //To obtain the coordinates of the relative screen, that is, the upper left corner of the screen as the origin
                x = event.getRawX();
                y = event.getRawY() - 10;
                Log.i(TAG, "currX" + x + "====currY" + y);

                int screenWidth = getBaseContext().getResources().getDisplayMetrics().widthPixels;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
                        // 获取相对View的坐标，即以此View左上角为原点
                        mTouchX = event.getX();
                        mTouchY = event.getY();
                        isMove = false;
                        Log.i(TAG, "startX" + mTouchX + "----startY" + mTouchY);
                        updateWindowFocus(false);
                        break;
                    case MotionEvent.ACTION_MOVE: // Capture finger touch movement
//                        float moveX = event.getX() - mTouchX;//X轴距离
//                        float moveY = event.getY() - mTouchY;//y轴距离
//                        if (moveX >0 ||moveY >0){
//                            isMove = true;
//                            mTouchX = event.getX();
//                            mTouchY = event.getY();
//                            updateViewPosition();
//                        }
//                        Log.d(TAG, "dx = " + dx + "; dy = " + dy);
                        if(x > 35 && (screenWidth - x) >35) {
                            isMove = true;
                            updateViewPosition();
                        }
                        break;
                    case MotionEvent.ACTION_UP: // Capture finger touch and leave the action
                        lastMoveState = isMove;
                        if(isMove) {
                            isMove = false;
                            float halfScreen = screenWidth/2;
                            if(x <= halfScreen) {
                                x = 0 ;
                                isRight = false;
                            } else {
                                x = screenWidth;
                                isRight = true;
                            }
                            currentOri = isRight;
                            updateViewPosition();
                        }
                        mTouchX = mTouchY = 0;
                        break;
                }
                return false;
            }
        });
        mText.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        updateWindowFocus(true);
                        break;
                    case MotionEvent.ACTION_MOVE: // Capture finger touch movement

                        break;
                    case MotionEvent.ACTION_UP: // Capture finger touch and leave the action

                        break;
                }
                return false;
            }
        });
        mFloatLayout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                    updateWindowFocus(false);
                }
                return false;
            }
        });
    }

    public View.OnClickListener listener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if (v.getId()==R.id.float_id){
                Log.d(TAG, "click floatView");
                if (lastMoveState) {
                    lastMoveState =false;
                }else {
//                    dismissFloatView();
                    String pinganText=mText.getText().toString();
                    StringBuffer commandSb= new StringBuffer();
                    commandSb.append("input text ");
                    commandSb.append(pinganText);
                    CommandExecution.execCommand(commandSb.toString(),false);
                    Toast.makeText(getBaseContext(),pinganText,Toast.LENGTH_SHORT).show();

                }
            }else if(v.getId()==R.id.startstep){
                Intent mIntent = new Intent("com.wobian.server.STARTSTEP");
                sendBroadcast(mIntent);
                stopStep.setVisibility(View.VISIBLE);
                startStep.setVisibility(View.GONE);
            }else if(v.getId()==R.id.stopstep){
                Intent mintent = new Intent("com.wobian.server.STOPSTEP");
                sendBroadcast(mintent);
                stopStep.setVisibility(View.GONE);
                startStep.setVisibility(View.VISIBLE);
            }
        }
    };
    private void updateViewPosition() {
        //update floatwindow oriation params
        wmParams.x = (int) (x - mTouchX);
        wmParams.y = (int) (y - mTouchY);
        currentX = wmParams.x;
        currentY = wmParams.y;
        mWindowManager.updateViewLayout(mFloatLayout, wmParams); //refresh
    }

    private void updateWindowFocus(boolean flag){
        if (flag){
            wmParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        }else{

            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        mWindowManager.updateViewLayout(mFloatLayout, wmParams); //refresh
    }

    public void dismissFloatView(){
        if(mFloatLayout != null)
        {
            //remove floatView
            mWindowManager.removeView(mFloatLayout);
        }
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        dismissFloatView();
    }
}
