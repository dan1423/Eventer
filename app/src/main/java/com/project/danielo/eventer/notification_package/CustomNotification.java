package com.project.danielo.eventer.notification_package;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.project.danielo.eventer.NotificationDialogActivity;
import com.project.danielo.eventer.R;
import com.project.danielo.eventer.StaticVariables;
import com.project.danielo.eventer.adapter.CustomEventObject;
import com.project.danielo.eventer.sqllite.DBHandler;

import java.util.Calendar;


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
        int id = Math.abs(notId);
        CustomEventObject e = getCustomEventObjectWithId(id);
        scheduleNotification(getNotification(notificationContent,notificationTitle,e), notId, notTag);
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
    private Notification getNotification(String content, String notificationTitle, CustomEventObject eventObject) {

        Intent intent = new Intent(context,NotificationDialogActivity.class);
        intent.putExtra("event_object",eventObject);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setShowWhen(true);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(content);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        builder.setSmallIcon(R.drawable.icons8_calendar_50);
       // builder.on

        return builder.build();
    }

    private CustomEventObject getCustomEventObjectWithId(int id){
        DBHandler handler = new DBHandler(context,null,null, StaticVariables.DATABASE_VERSION);
        CustomEventObject e = handler.getEventObject(id);

        return e;
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



