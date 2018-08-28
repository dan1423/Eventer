package com.project.danielo.eventer.notification_package;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import static android.content.Context.VIBRATOR_SERVICE;

public class CustomReciever extends BroadcastReceiver {


        public static String NOTIFICATION_ID = "notification-id";
        public static String NOTIFICATION = "notification";
        public static String NOTIFICATION_TAG = "notification-tag";

        public void onReceive(Context context, Intent intent) {

            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = intent.getParcelableExtra(NOTIFICATION);
            int id = intent.getIntExtra(NOTIFICATION_ID, 0);
            String tag = intent.getStringExtra(NOTIFICATION_TAG);
            notificationManager.notify(tag,id, notification);
            //Intent i = new Intent(CustomReciever.this,)
            //context.startActivity();





            if(isVibrateOn(context)){
                vibrate(context);
            }

        }

        private void vibrate(Context context){
            if (Build.VERSION.SDK_INT >= 26) {
                ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
            } else {
                ((Vibrator)context.getSystemService(VIBRATOR_SERVICE)).vibrate(150);
            }
        }

    private boolean isVibrateOn(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isVibrateOn = preferences.getBoolean("vibration", true);
        return isVibrateOn;
    }


}
