package com.iyuba.music.service;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.iyuba.music.R;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.local_music.LocalMusicActivity;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.widget.bitmap.ReadBitmap;

import java.util.LinkedList;

public class NotificationUtil {
    public static final String PAUSE_FLAG = "pause_flag";
    public static final String PLAY_FLAG = "play_flag";
    static final int NOTIFICATION_ID = 209;
    private Notification notification;
    private int notificationTextColor;

    private NotificationUtil() {
        notificationTextColor = isDarkNotificationTheme() ? 0xffcdcdcd : 0xff4c4c4c;
    }

    public static NotificationUtil getInstance() {
        return SingleInstanceHelper.instance;
    }

    private static int findColor(ViewGroup viewGroupSource) {
        int color = Color.TRANSPARENT;
        LinkedList<ViewGroup> viewGroups = new LinkedList<>();
        viewGroups.add(viewGroupSource);
        while (viewGroups.size() > 0) {
            ViewGroup viewGroup1 = viewGroups.getFirst();
            for (int i = 0; i < viewGroup1.getChildCount(); i++) {
                if (viewGroup1.getChildAt(i) instanceof ViewGroup) {
                    viewGroups.add((ViewGroup) viewGroup1.getChildAt(i));
                } else if (viewGroup1.getChildAt(i) instanceof TextView) {
                    if (((TextView) viewGroup1.getChildAt(i)).getCurrentTextColor() != -1) {
                        color = ((TextView) viewGroup1.getChildAt(i)).getCurrentTextColor();
                    }
                }
            }
            viewGroups.remove(viewGroup1);
        }
        return color;
    }

    Notification initNotification() {
        notification = new Notification();
        Context context = RuntimeManager.getContext();

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
        contentView.setOnClickPendingIntent(R.id.notify_close, receiveCloseIntent());
        contentView.setOnClickPendingIntent(R.id.notify_latter, receiveNextIntent());
        contentView.setOnClickPendingIntent(R.id.notify_play, receivePauseIntent());
        contentView.setOnClickPendingIntent(R.id.notify_formmer, receiveBeforeIntent());

        contentView.setTextViewText(R.id.notify_title, context.getString(R.string.app_name));
        contentView.setTextViewText(R.id.notify_singer, context.getString(R.string.app_corp));
        contentView.setImageViewResource(R.id.notify_img, R.drawable.default_music);
        contentView.setImageViewResource(R.id.notify_play, R.drawable.play);
        setTextViewColor(contentView);

        NotificationCompat.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT > 26) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel("209", "209", NotificationManager.IMPORTANCE_LOW));
            notificationBuilder = new NotificationCompat.Builder(context, "209");
        } else {
            notificationBuilder = new NotificationCompat.Builder(context);
        }
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_circle);
        notificationBuilder.setContent(contentView);
        notificationBuilder.setLargeIcon(ReadBitmap.readBitmap(RuntimeManager.getContext(), R.drawable.ic_launcher_circle));
        notificationBuilder.setAutoCancel(false);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setCustomBigContentView(contentView);
        notificationBuilder.setColor(GetAppColor.getInstance().getAppColor());
        notification = notificationBuilder.build();
        return notification;
    }

    void updateNotification(String imgUrl) {
        Context context = RuntimeManager.getContext();
        Article curArticle = StudyManager.getInstance().getCurArticle();
        Intent intent;
        if (StudyManager.getInstance().getApp().equals("101")) {
            intent = new Intent(context, LocalMusicActivity.class);
        } else {
            intent = new Intent(context, StudyActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT);

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
        contentView.setOnClickPendingIntent(R.id.notify_close, receiveCloseIntent());
        contentView.setOnClickPendingIntent(R.id.notify_latter, receiveNextIntent());

        contentView.setOnClickPendingIntent(R.id.notify_play, receivePauseIntent());

        contentView.setOnClickPendingIntent(R.id.notify_formmer, receiveBeforeIntent());
        contentView.setImageViewResource(R.id.notify_play, R.drawable.pause);
        switch (StudyManager.getInstance().getApp()) {
            case "209":
                contentView.setTextViewText(R.id.notify_title, curArticle.getTitle());
                contentView.setTextViewText(R.id.notify_singer, curArticle.getSinger());
                contentView.setTextViewText(R.id.notify_singer, context.getString(R.string.article_singer, curArticle.getSinger()));
                //contentView.setTextViewText(R.id.notify_announcer, context.getString(R.string.article_announcer, curArticle.getBroadcaster()));
                break;
            case "101":
                contentView.setTextViewText(R.id.notify_title, curArticle.getTitle());
                contentView.setTextViewText(R.id.notify_singer, curArticle.getSinger());
                //contentView.setTextViewText(R.id.notify_singer, context.getString(R.string.article_singer, curArticle.getSinger()));
                //contentView.setTextViewText(R.id.notify_announcer, context.getString(R.string.article_duration, curArticle.getBroadcaster()));
                break;
            default:
                contentView.setTextViewText(R.id.notify_singer, curArticle.getTitle());
                contentView.setTextViewText(R.id.notify_title, curArticle.getTitle_cn());
                //contentView.setTextViewText(R.id.notify_announcer, context.getString(R.string.app_intro));
                break;
        }
        contentView.setImageViewResource(R.id.notify_img, R.drawable.default_music);
        setTextViewColor(contentView);
        NotificationCompat.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT > 26) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel("209", "209", NotificationManager.IMPORTANCE_LOW));
            notificationBuilder = new NotificationCompat.Builder(context, "209");
        } else {
            notificationBuilder = new NotificationCompat.Builder(context);
        }

        notificationBuilder.setContentTitle(context.getString(R.string.app_name));
        notificationBuilder.setContentText(curArticle.getTitle());
        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_circle);
        notificationBuilder.setContent(contentView);
        notificationBuilder.setLargeIcon(ReadBitmap.readBitmap(RuntimeManager.getContext(), R.drawable.ic_launcher_circle));
        notificationBuilder.setOngoing(true);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setCustomBigContentView(contentView);
        notificationBuilder.setColor(GetAppColor.getInstance().getAppColor());
        notification = notificationBuilder.build();
        if (!StudyManager.getInstance().getApp().equals("101")) {

            NotificationTarget notificationTarget = new NotificationTarget(context, R.id.notify_img, contentView, notification, NOTIFICATION_ID);
            Glide.with(context).asBitmap().load(imgUrl).into(notificationTarget);
        }
        ((NotificationManager) RuntimeManager.getContext().getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
    }

    public void updatePlayStateNotification(String cmd) {
        if (cmd.equals(PAUSE_FLAG)) {
            notification.bigContentView.setImageViewResource(R.id.notify_play, R.drawable.play);
        } else if (cmd.equals(PLAY_FLAG)) {
            notification.bigContentView.setImageViewResource(R.id.notify_play, R.drawable.pause);
        }
        ((NotificationManager) RuntimeManager.getContext().getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
    }

    public void removeNotification() {
        ((NotificationManager) RuntimeManager.getContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
    }

    private PendingIntent receivePauseIntent() {
        Intent intent = new Intent("iyumusic.pause");
        return PendingIntent.getBroadcast(RuntimeManager.getContext(), 0, intent, FLAG_UPDATE_CURRENT);
    }

    private PendingIntent receiveNextIntent() {
        Intent intent = new Intent("iyumusic.next");
        return PendingIntent.getBroadcast(RuntimeManager.getContext(), 0, intent, FLAG_UPDATE_CURRENT);
    }

    private PendingIntent receiveBeforeIntent() {
        Intent intent = new Intent("iyumusic.before");
        return PendingIntent.getBroadcast(RuntimeManager.getContext(), 0, intent, FLAG_UPDATE_CURRENT);
    }

    private PendingIntent receiveCloseIntent() {
        Intent intent = new Intent("iyumusic.close");
        return PendingIntent.getBroadcast(RuntimeManager.getContext(), 0, intent, FLAG_UPDATE_CURRENT);
    }

    private void setTextViewColor(RemoteViews contentView) {
        int color = notificationTextColor;
        contentView.setInt(R.id.notify_title, "setTextColor", color);
        contentView.setInt(R.id.notify_singer, "setTextColor", color);
        contentView.setInt(R.id.notify_close, "setColorFilter", color);
        contentView.setInt(R.id.notify_play, "setColorFilter", color);
        contentView.setInt(R.id.notify_formmer, "setColorFilter", color);
        contentView.setInt(R.id.notify_latter, "setColorFilter", color);
    }

    private boolean isDarkNotificationTheme() {
        return !isSimilarColor(Color.BLACK, getNotificationColor());
    }

    /**
     * 获取通知栏颜色
     *
     * @return
     */
    private int getNotificationColor() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(RuntimeManager.getContext());
        Notification notification = builder.build();
        int layoutId;
        if (notification.contentView != null) {
            layoutId = notification.contentView.getLayoutId();
        } else {
            layoutId = R.layout.notification;
        }
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(RuntimeManager.getContext()).inflate(layoutId, null, false);
        if (viewGroup.findViewById(android.R.id.title) != null) {
            return ((TextView) viewGroup.findViewById(android.R.id.title)).getCurrentTextColor();
        }
        return findColor(viewGroup);
    }

    private boolean isSimilarColor(int baseColor, int color) {
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        return value < 180.0;
    }

    private static class SingleInstanceHelper {
        private static NotificationUtil instance = new NotificationUtil();
    }
}