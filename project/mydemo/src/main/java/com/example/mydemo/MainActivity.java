package com.example.mydemo;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showNotifyOnlyText();
    }


    /**
     * 最普通的通知效果
     */
    private void showNotifyOnlyText() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("我是只有文字效果的通知")
                .setContentText("我没有铃声、震动、呼吸灯,但我就是一个通知");
        manager.notify(1, builder.build());
    }

}
