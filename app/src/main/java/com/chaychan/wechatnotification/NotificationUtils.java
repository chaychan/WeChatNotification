package com.chaychan.wechatnotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * @author ChayChan
 * @description: 通知栏工具
 * @date 2017/9/26  9:51
 */

public class NotificationUtils {

    private static final Random RANDOM = new Random();
    private static final long REMIND_PERIOD = 5 * 1000; //提醒（响铃震动）的周期

    private static MediaPlayer mPlayer;

    private static long mNotificationRemindTime; //通知栏上次提醒时间
    private static long mForegroundRemindPreTime;//前台上次提醒时间

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


    /**
     * 判断是否需要提醒，根据是否超过周期
     *
     * @return
     */
    private static boolean shouldRemind(boolean isNotification) {
        if (isNotification) {
            if (System.currentTimeMillis() - mNotificationRemindTime >= REMIND_PERIOD) {
                mNotificationRemindTime = System.currentTimeMillis();
                return true;
            }
            return false;
        }

        //如果是判断前台提醒
        if (System.currentTimeMillis() - mForegroundRemindPreTime >= REMIND_PERIOD) {
            mForegroundRemindPreTime = System.currentTimeMillis();
            return true;
        }

        return false;
    }

    /**
     * 响铃
     *
     * @param context
     */
    private static void ring(Context context) {
        try {
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                mPlayer.setDataSource(context, uri);
                mPlayer.prepare();
            }
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
                mPlayer.start();
            }
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayer.release();
                    ;
                    mPlayer = null;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 震动
     *
     * @param context
     */
    private static void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{100, 300, 200, 300}, -1);//表示停100ms, 震300ms, 停200ms，震300ms
    }

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

    public static int getRandomNum() {
        return RANDOM.nextInt(Integer.MAX_VALUE);
    }
}
