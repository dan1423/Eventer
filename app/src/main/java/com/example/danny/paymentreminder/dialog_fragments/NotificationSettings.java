package com.example.danny.paymentreminder.dialog_fragments;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.danny.paymentreminder.Custom_Classes.CustomDateParser;
import com.example.danny.paymentreminder.R;
import com.example.danny.paymentreminder.StaticVariables;
import com.example.danny.paymentreminder.adapter.CustomEventObject;
import com.example.danny.paymentreminder.notification_package.CustomNotification;
import com.example.danny.paymentreminder.sqllite.DBHandler;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.VIBRATOR_SERVICE;

public class NotificationSettings extends Fragment {

    public NotificationSettings(){

    }

    private View notificationSettingsView;
    Switch switcthNotification, switchVibrate;
    ImageView imageViewExitNot;
    DBHandler dbHandler;
    ArrayList<CustomEventObject>customEventObjects;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = new DBHandler(getContext(),null,null, StaticVariables.VERSION);
        customEventObjects = new ArrayList<>();
        customEventObjects = dbHandler.queryAllEvents();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        notificationSettingsView = inflater.inflate(R.layout.notification_settings,null,false);
        switcthNotification = (Switch)notificationSettingsView.findViewById(R.id.switch_notification);
        switchVibrate  = (Switch)notificationSettingsView.findViewById(R.id.switch_vibrate);
        imageViewExitNot = (ImageView)notificationSettingsView.findViewById(R.id.img_exit_notitication);


        if(customEventObjects.size() !=0){
            if(isNotificationOn(customEventObjects.get(0).getEventId())){
                switcthNotification.setChecked(true);
            }else{
                switcthNotification.setChecked(false);
            }
        }


        switcthNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    addAllNotifications();
                }else{
                    removeAllNotifications();
                    }
                }

        });

        //handle vibration mode
        if(isVibrateOn()){
            switchVibrate.setChecked(true);
        }else{
            switchVibrate.setChecked(false);
        }
       //
        switchVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                   vibrate();
                }else{
                    removeVibrate();
                }
            }

        });

        imageViewExitNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitFragment();
            }
        });

        return notificationSettingsView;

    }

    private void addAllNotifications(){
        for(int i = 0;i  < customEventObjects.size(); i++){
            setupNotification(customEventObjects.get(i),customEventObjects.get(i).getEventId());
        }
    }

    private void removeAllNotifications(){
        for(int i = 0;i  < customEventObjects.size(); i++){
            deleteEventNotification(customEventObjects.get(i));
        }
    }

    private void vibrate(){
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
        } else {
            ((Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }

    private void removeVibrate(){
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE)).cancel();
        } else {
            ((Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE)).cancel();
        }
    }

    public void setupNotification(CustomEventObject e, long id){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();

        cal.setTimeInMillis(e.getEventDate());
        int notId = (int)id;
        CustomDateParser parser = new CustomDateParser(e.getEventDate());
        parser.setDateAndTime();
        String notificationContent = "Starts at "+parser.getTime();
        CustomNotification notification = new CustomNotification(getContext(), e.getEventName(),
                notificationContent, notId,notId,cal.getTimeInMillis());
        notification.setNotification();
        Log.i("CUSTOM",notification.toString());

    }

    private void deleteEventNotification(CustomEventObject e){
        CustomNotification notification = new CustomNotification(getContext(),e.getEventId());
        notification.removeNotification();
    }

    private boolean isVibrateOn(){
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE)).hasVibrator();
        } else {
            ((Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE)).hasVibrator();
        }

        return false;
    }

    private boolean isNotificationOn(int notId){
        boolean alarmUp = (PendingIntent.getBroadcast(getContext(), notId,
                new Intent("com.my.package.MY_UNIQUE_ACTION"),
                PendingIntent.FLAG_NO_CREATE) != null);

        return alarmUp;
    }


    private void exitFragment(){
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();

    }
}
