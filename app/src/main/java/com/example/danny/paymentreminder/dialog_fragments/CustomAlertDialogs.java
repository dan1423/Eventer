package com.example.danny.paymentreminder.dialog_fragments;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.danny.paymentreminder.Custom_Classes.CustomDateParser;
import com.example.danny.paymentreminder.R;
import com.example.danny.paymentreminder.adapter.CustomEventObject;


public class CustomAlertDialogs {

    private Activity activity;

    public CustomAlertDialogs(Activity activity){
        this.activity = activity;
    }
    public void showEventDetails(CustomEventObject customEventObject){

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity,android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.about_event, null);
        builder.setView(dialogView);
        final AlertDialog d = builder.show();
        d.setTitle("About This Event");


        TextView eventName = (TextView) dialogView.findViewById(R.id.dialog_event_name);
        TextView eventDate = (TextView) dialogView.findViewById(R.id.dialog_event_date);
        TextView eventType = (TextView) dialogView.findViewById(R.id.dialog_event_type);
        Button btnClose  = (Button)dialogView.findViewById(R.id.dialog_close_btn);

        eventName.setText(customEventObject.getEventName());

        CustomDateParser parser = new CustomDateParser(customEventObject.getEventDate());
        parser.setDateAndTime();

        String date = parser.getDate();
        String time = parser.getTime();

        eventDate.setText("On "+date + " at "+time);

        eventType.setText(customEventObject.getEventType());

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });


        builder.create();
    }

    public void confirmationDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.success_notification_dialog, null);
        builder.setView(dialogView);
        final AlertDialog d = builder.show();
        //d.requestWindowFeature(d.getWindow().FEATURE_NO_TITLE);
       d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                       d.dismiss();
                    }
                },
                1000
        );



        builder.create();
    }
}
