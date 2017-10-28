package com.chaychan.wechatnotification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SendNotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);
    }

    public void sendNotification(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NotificationUtils.showNotification(SendNotificationActivity.this,"private","25","http://up.qqjia.com/z/14/tu17250_8.jpg","小明","今天去哪里玩呢?");
            }
        }).start();
    }
}
