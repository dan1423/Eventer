package com.project.danielo.eventer.notification_package;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.project.danielo.eventer.MainActivity;
import com.project.danielo.eventer.R;

import java.util.Calendar;
import java.util.Date;


public class CustomNotification {

    private Context context;
    private String notificationTitle;
    private String notificationContent;
    private int notId;
    private int requestCode;
    private String notTag;
    long timeInMillis;


    //constructor for setting notification
    public CustomNotification(Context context, String notificationTitle,
                              String notificationContent,String notTag, int notId, int requestCode,long time) {

        this.context = context;
        this.notificationTitle = notificationTitle;
        this.notificationContent = notificationContent;
        this.notId = notId;
        this.notTag = notTag;
        this.requestCode = requestCode;
        this.timeInMillis = time;


    }

    //constructor for removing notification
    public CustomNotification(Context context,int requestCode){
        this.context = context;
        this.requestCode = requestCode;
    }


    public void setNotification(){
        scheduleNotification(getNotification(notificationContent,notificationTitle), notId, notTag);
    }

    public void removeNotification(){
        removeNotification(requestCode);
    }

    private void removeNotification(int requestCode){
       AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context, CustomReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        alarmManager.cancel(pendingIntent);
    }


    private void scheduleNotification(Notification notification, int notId, String tag){

        Intent notificationIntent = new Intent(context, CustomReciever.class);
        notificationIntent.putExtra(CustomReciever.NOTIFICATION_ID, notId);
        notificationIntent.putExtra(CustomReciever.NOTIFICATION, notification);
        notificationIntent.putExtra(CustomReciever.NOTIFICATION_TAG, tag);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        long dd =  (timeInMillis/60000) - (Calendar.getInstance().getTimeInMillis()/60000) ;
        long futureInMillis = SystemClock.elapsedRealtime() +(dd *60000) ;

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);

    }


    // private void set
    private Notification getNotification(String content, String notificationTitle) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setShowWhen(true);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.icons8_calendar_50);

        return builder.build();
    }


    @Override
    public String toString() {
        return "CustomNotification{" +
                "context=" + context +
                ", notificationTitle='" + notificationTitle + '\'' +
                ", notificationContent='" + notificationContent + '\'' +
                ", notId=" + notId +
                ", requestCode=" + requestCode +
                ", notTag='" + notTag + '\'' +
                ", timeInMillis=" + timeInMillis +
                '}';
    }
}



