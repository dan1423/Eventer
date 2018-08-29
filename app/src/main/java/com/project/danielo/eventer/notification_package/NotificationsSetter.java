package com.project.danielo.eventer.notification_package;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.project.danielo.eventer.Custom_Classes.CustomDateParser;
import com.project.danielo.eventer.adapter.CustomEventObject;
import com.project.danielo.eventer.sqllite.DBHandler;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationsSetter {
    DBHandler dbHandler;
    Context context;
    ArrayList<CustomEventObject>customEventObjects;
    public NotificationsSetter(DBHandler dbHandler, Context context){
        this.dbHandler = dbHandler;
        this.context = context;
        customEventObjects = dbHandler.queryAllEvents();
    }

    public NotificationsSetter(Context context){
        this.context = context;
    }

    public void addAllNotifications(){
        addNotifications();
    }

    public void removeAllNotifications(){
        removeNotifications();
    }

    private void addNotifications(){
        for(int i = 0;i  < customEventObjects.size(); i++){
            setUpInitialNotification(customEventObjects.get(i),customEventObjects.get(i).getEventId());
            setUpMinutesBeforeNotification(customEventObjects.get(i),customEventObjects.get(i).getEventId());
            setUpFinalNotification(customEventObjects.get(i),customEventObjects.get(i).getEventId());
        }
    }

    private void removeNotifications(){
        for(int i = 0;i  < customEventObjects.size(); i++){
            deleteEventNotification(customEventObjects.get(i));
        }
    }

    //set up a single notification for an event, excluding past events
    public void setUpInitialNotification(CustomEventObject e, long id){

        //set calendar to exact time of event
        Calendar presentTime = Calendar.getInstance();
        presentTime.setTimeInMillis(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();

        cal.setTimeInMillis(e.getEventDate());

        if(cal.before(presentTime)){//time has past, no need to set notification
            return;
        }

        //set calendar for the midnight the event occurs
        Calendar dayOfEvent = Calendar.getInstance();
        dayOfEvent.set(Calendar.HOUR_OF_DAY,0);
        dayOfEvent.set(Calendar.MINUTE,0);
        dayOfEvent.set(Calendar.SECOND,0);
        dayOfEvent.set(Calendar.MILLISECOND,0);
        dayOfEvent.set(Calendar.MONTH,cal.getTime().getMonth());
        dayOfEvent.set(Calendar.DATE,cal.getTime().getDate());


        int notId = (int)id;
        CustomDateParser parser = new CustomDateParser(e.getEventDate());
        parser.setDateAndTime();
        String notificationContent = "Today at "+parser.getTime();

        //to differentiate other notitifactions for the same event, we must make the tag unique
        String notTag = notId+"one";
        CustomNotification notification = new CustomNotification(context, e.getEventName(),
                notificationContent, notTag,notId,notId,dayOfEvent.getTimeInMillis());
        notification.setNotification();
       // Log.i("CUSTOM",notification.toString());

    }

    //this notification is shown time before event based on prefered time in notification setting
    public void setUpMinutesBeforeNotification(CustomEventObject e, long id){

        int minutesBefore = getMinutesBeforeNotification();

        //set calendar to exact time of event
        Calendar presentTime = Calendar.getInstance();
        presentTime.setTimeInMillis(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();

        cal.setTimeInMillis(e.getEventDate());

        if(cal.before(presentTime)){//time has past, no need to set notification
            return;
        }

        //set calendar for the midnight the event occurs
        Calendar dayOfEvent = Calendar.getInstance();
        dayOfEvent.setTimeInMillis(e.getEventDate());
        dayOfEvent.add(Calendar.MINUTE,-minutesBefore);

        int notId = (int)id;
        notId = notId * -1;//negative id to differentiate second notification
        CustomDateParser parser = new CustomDateParser(e.getEventDate());
        parser.setDateAndTime();
        String notificationContent = "Starts at "+parser.getTime();
       // notificationContent+= e.getEventNote();

        //to differentiate other notifications for the same event, we must make the tag unique
        String notTag = notId+"two";

        CustomNotification notification = new CustomNotification(context, e.getEventName(),
                notificationContent, notTag,notId,notId,dayOfEvent.getTimeInMillis());
        notification.setNotification();


    }

    //set up notification matching the actual time of event
    public void setUpFinalNotification(CustomEventObject e, long id){

        //set calendar to exact time of event
        Calendar presentTime = Calendar.getInstance();
        presentTime.setTimeInMillis(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();

        cal.setTimeInMillis(e.getEventDate());

        if(cal.before(presentTime)){//time has past, no need to set notification
            return;
        }


        int notId = (int)id;
        CustomDateParser parser = new CustomDateParser(e.getEventDate());
        parser.setDateAndTime();
        String notificationContent = "Starts now";


        //to differentiate other notifications for the same event, we must make the tag unique
        String notTag = "final";

        CustomNotification notification = new CustomNotification(context, e.getEventName(),
                notificationContent,notTag,notId,notId,cal.getTimeInMillis());
        notification.setNotification();

    }

    private void deleteEventNotification(CustomEventObject e){
        CustomNotification notification = new CustomNotification(context,e.getEventId());
        notification.removeNotification();

    }

    private int getMinutesBeforeNotification(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int minutesBefore = preferences.getInt("minutesBeforeEvent", 1);
        return minutesBefore;
    }

}
