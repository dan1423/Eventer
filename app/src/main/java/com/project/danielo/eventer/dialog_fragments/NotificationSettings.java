package com.project.danielo.eventer.dialog_fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.project.danielo.eventer.R;
import com.project.danielo.eventer.StaticVariables;
import com.project.danielo.eventer.adapter.CustomEventObject;
import com.project.danielo.eventer.notification_package.NotificationsSetter;
import com.project.danielo.eventer.sqllite.DBHandler;

import java.util.ArrayList;
/*The purpose of this class is to set up notifications of events*/
public class NotificationSettings extends Fragment {

    public NotificationSettings(){

    }

    private View notificationSettingsView;
    Switch switcthNotification, switchVibrate;
    ImageView imageViewExitNot;
    DBHandler dbHandler;
    Spinner spinnerReminder;
    ArrayList<CustomEventObject>customEventObjects;
    NotificationsSetter setter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = new DBHandler(getContext(),null,null, StaticVariables.DATABASE_VERSION);
        customEventObjects = new ArrayList<>();
        customEventObjects = dbHandler.queryAllEvents();
        setter = new NotificationsSetter(dbHandler,getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        notificationSettingsView = inflater.inflate(R.layout.layout_for_notification_settings,null,false);
        switcthNotification = (Switch)notificationSettingsView.findViewById(R.id.switch_notification);
        switchVibrate  = (Switch)notificationSettingsView.findViewById(R.id.switch_vibrate);
        imageViewExitNot = (ImageView)notificationSettingsView.findViewById(R.id.img_exit_notitication);
        spinnerReminder = (Spinner)notificationSettingsView.findViewById(R.id.spinner_reminder);



        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.reminder_list,
                        android.R.layout.simple_spinner_item);
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerReminder.setAdapter(staticAdapter);
        spinnerReminder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item is selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                    handleNotificationType(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, just another required interface callback
            }

        });


            if(isNotificationsOn()){
                switcthNotification.setChecked(true);
            }else{
                switcthNotification.setChecked(false);
            }



        switcthNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setter.addAllNotifications();
                    turnOnNotifications();
                    spinnerReminder.setEnabled(true);
                    switchVibrate.setEnabled(true);
                }else{
                    setter.removeAllNotifications();
                    turnOffNotifications();
                    spinnerReminder.setEnabled(false);
                    switchVibrate.setEnabled(false);
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
                   turnOnVibration();
                }else{
                    turnOffVibration();
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

    private boolean isNotificationsOn(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isOn = preferences.getBoolean("notifications", true);
        return isOn;
    }

    private void turnOffNotifications(){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putBoolean("notifications",false);
        editor.apply();
    }

    private void turnOnNotifications(){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putBoolean("notifications",true);
        editor.apply();
    }

    private boolean isVibrateOn(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isVibrateOn = preferences.getBoolean("vibration", true);
        return isVibrateOn;
    }

    private void turnOnVibration(){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putBoolean("vibration",true);
        editor.apply();
    }

    private void turnOffVibration(){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putBoolean("vibration",false);
        editor.apply();
    }



    private void handleNotificationType(int pos){
       int minutesBeforeEvent = 1;

       switch (pos){
           case 0:minutesBeforeEvent = 5;
           break;
           case 1:minutesBeforeEvent = 10;
           break;
           case 2:minutesBeforeEvent = 15;
           break;
           case 3: minutesBeforeEvent = 30;
           break;
           case 4:minutesBeforeEvent = 60;
           break;
           default:minutesBeforeEvent = 1;
       }

       SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
       editor.putInt("minutesBeforeEvent",minutesBeforeEvent);
       editor.apply();

    }
    private void exitFragment(){
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
    }
}
