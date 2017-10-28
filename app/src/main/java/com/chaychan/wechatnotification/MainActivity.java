package com.chaychan.wechatnotification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (!uri.getScheme().equals("wechat")) {
            return;
        }
        if (uri.getPath().contains("/conversation")) {
            String conversationType = uri.getQueryParameter(Constant.CONVERSATION_TYPE);
            String targetId = uri.getQueryParameter(Constant.TARGET_ID);
            String title = uri.getQueryParameter(Constant.TITLE);

            Intent conversationIntent = new Intent(this, ConversationActivity.class);
            conversationIntent.putExtra(Constant.TITLE,title);
            conversationIntent.putExtra(Constant.TARGET_ID,targetId);
            conversationIntent.putExtra(Constant.CONVERSATION_TYPE,conversationType);
            startActivity(conversationIntent);
        }
    }

    public void openSend(View view){
        Intent intent = new Intent(this, SendNotificationActivity.class);
        startActivity(intent);
    }
}
