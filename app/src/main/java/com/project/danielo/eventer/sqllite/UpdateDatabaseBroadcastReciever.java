package com.project.danielo.eventer.sqllite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.project.danielo.eventer.StaticVariables;
import com.project.danielo.eventer.notification_package.NotificationsSetter;

public class UpdateDatabaseBroadcastReciever extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        DBHandler dbHandler = new DBHandler(context,null,null, StaticVariables.DATABASE_VERSION);
        NotificationsSetter setter;

        UpdateDatabase  updateDatabase = new UpdateDatabase(context);
        updateDatabase.updateAllEvents();

        setter = new NotificationsSetter(dbHandler,context);

        if(isNotificationsOn(context)){
            setter.addAllNotifications();
        }else{
            setter.removeAllNotifications();
        }


    }

    private boolean isNotificationsOn(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isOn = preferences.getBoolean("notifications", true);
        return isOn;
    }

    }