package com.chaychan.wechatnotification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ConversationActivity extends AppCompatActivity {

    private TextView mTvTitle;
    private TextView mTvTargetId;
    private TextView mTvType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        initView();
        initData();
    }

    private void initView() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTargetId = (TextView) findViewById(R.id.tv_targetId);
        mTvType = (TextView) findViewById(R.id.tv_type);
    }

    private void initData() {
        Intent intent = getIntent();

        String title = intent.getStringExtra(Constant.TITLE);
        String targetId = intent.getStringExtra(Constant.TARGET_ID);
        String type = intent.getStringExtra(Constant.CONVERSATION_TYPE);

        mTvTitle.setText("标题(对方的昵称): " + title);
        mTvTargetId.setText("targetId: " + targetId);
        mTvType.setText("会话类型: " + type);
    }
}
