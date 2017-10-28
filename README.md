## Blog
[http://blog.csdn.net/chay_chan/article/details/78375524](http://blog.csdn.net/chay_chan/article/details/78375524)

## 支持和鼓励

如果你觉得我的代码对你有帮助，希望你可以帮忙star一下，让更多人可以看到，帮助到更多人。

### 先上效果图
![](http://img.blog.csdn.net/20171028122458460?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvQ2hheV9DaGFu/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


![](http://img.blog.csdn.net/20171028122519635?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvQ2hheV9DaGFu/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


### 点击通知栏后跳转并传值
![](http://img.blog.csdn.net/20171028122534451?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvQ2hheV9DaGFu/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


### app下载体验

[点击下载](https://raw.githubusercontent.com/chaychan/WeChatNotification/master/apk/wechat_notification.apk)

### 通知栏图片

通知栏的小图标建议使用纯白色，在通知栏显示的时候就是白色，拉下来的时候就会变成灰色的
在图片文件夹中放置不同尺寸的图标

24 × 24 (mdpi) 
36 × 36 (hdpi) 
48 × 48 (xhdpi) 
72 × 72 (xxhdpi) 
96 × 96 (xxxhdpi)

### 封装好的通知工具类

调用弹出通知栏的方法：

```
public static void showNotification(Context context, String conversationType, String targetId, String avatar, String name, String content) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        mBuilder.setPriority(Notification.PRIORITY_MAX);//可以让通知显示在最上面
        mBuilder.setSmallIcon(R.mipmap.small_icon);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setAutoCancel(true);

        if (shouldRemind(true)) {
            mBuilder.setDefaults(Notification.DEFAULT_ALL);//使用默认的声音、振动、闪光
        }

        Bitmap bmIcon = BitmapFactory.decodeResource(
                context.getResources(), R.mipmap.ic_launcher);

        Bitmap largeIcon = bmIcon;
        Bitmap bmAvatar = GetImageInputStream(avatar);
        if (bmAvatar != null) {
            //如果可以获取到网络头像则用网络头像
            largeIcon = bmAvatar;
        }

        content = name + ":" + content; //内容为 xxx:内容
        //        int unreadCount = RongIMClient.getInstance().getUnreadCount(conversationType, targetId);
        //        if (unreadCount > 1) {
        //            //如果未读数大于1，则还有拼接上[x条]
        //            String num = String.format(UIUtils.getString(R.string.notification_num_format), unreadCount);
        //            content = num + content;//内容为 [x条] xxx:内容
        //        }

        mBuilder.setLargeIcon(largeIcon);

        mBuilder.setContentTitle(name);

        Intent intent = getIntent(context, conversationType, targetId, name);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, getRandomNum(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //通知首次出现在通知栏，带上升动画效果的
        mBuilder.setTicker(content);
        //内容
        mBuilder.setContentText(content);

        mBuilder.setContentIntent(pendingIntent);
        Notification notification = mBuilder.build();

        int notifyId = 0;
        try {
            notifyId = Integer.parseInt(targetId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            notifyId = -1;
        }

        //弹出通知栏
        notificationManager.notify(notifyId, notification);
    }
```

获取网络图片(头像)的方法

```
/**
     * 获取网络图片
     *
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
    public static Bitmap GetImageInputStream(String imageurl) {
        URL url;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            url = new URL(imageurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
```

通知栏跳转意图的创建

```
/**
     * 产生对应的intent
     */
    private static Intent getIntent(Context context, String conversationType, String targetId, String title) {
        Intent intent = new Intent();
        intent.setAction("ActionConversation");
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);

        Uri.Builder uriBuilder = Uri.parse("wechat://" + context.getPackageName()).buildUpon();

        uriBuilder.appendPath("conversation");
        uriBuilder.appendQueryParameter(Constant.CONVERSATION_TYPE, conversationType)
                .appendQueryParameter(Constant.TARGET_ID, targetId)
                .appendQueryParameter(Constant.TITLE, title);

        intent.setData(uriBuilder.build());
        Log.i("uri: ", uriBuilder.build().toString());
        return intent;
    }
```

在Manifest中，配置对应配置点击通知栏跳转的相应activity的uri配置:

```
 <activity
            android:name=".MainActivity"
            android:configChanges="keyboardHidden|screenSize|orientation"
            android:screenOrientation="portrait"
            android:launchMode="singleTask"
            >

            ...

            <intent-filter>
                <action android:name="ActionConversation"/>
                <category android:name="android.intent.category.DEFAULT"/>
                <data
                    android:host="com.chaychan.wechatnotification"
                    android:pathPrefix="/conversation"
                    android:scheme="wechat"/>
            </intent-filter>

  </activity>
```


其中  intent-filter 中配置了activity的匹配规则，action对应getIntent()方法中设置的action的值，data中的scheme和pathPrefit也对应getIntent()方法中设置的uri的路径。

### MainActivity中处理传递的Intent

```
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
```

在onNewIntent()处理通知栏传递过来的intent，根据传递过来的uri进行跳转。    

### 源码下载：

[https://github.com/chaychan/WeChatNotification](https://github.com/chaychan/WeChatNotification)