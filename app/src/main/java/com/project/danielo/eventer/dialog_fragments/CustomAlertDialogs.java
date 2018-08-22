package com.project.danielo.eventer.dialog_fragments;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.danielo.eventer.Custom_Classes.CustomDateParser;
import com.project.danielo.eventer.R;
import com.project.danielo.eventer.adapter.CustomEventObject;


public class CustomAlertDialogs {

    private Activity activity;

    public CustomAlertDialogs(Activity activity){
        this.activity = activity;
    }
    public void showEventDetails(CustomEventObject customEventObject){

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity,android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.layot_for_event_details_dialog, null);
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
        eventDate.setText("Event on "+ parser.getDate() + " at "+parser.getTime());

        eventType.setText("This is a "+customEventObject.getEventType() +" event");

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });


        builder.create();
    }


}
