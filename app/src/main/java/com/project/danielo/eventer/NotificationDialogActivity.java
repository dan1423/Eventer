package com.project.danielo.eventer;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.danielo.eventer.Custom_Classes.CustomDateParser;
import com.project.danielo.eventer.adapter.CustomEventObject;

public class NotificationDialogActivity extends AppCompatActivity {

    private CustomEventObject eventObject;
    private ImageView imgExit;
    private TextView txtName,txtDate,txtNote,txtType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_dialog);
        eventObject = (CustomEventObject) getIntent().getSerializableExtra("event_object");

        imgExit = (ImageView)findViewById(R.id.img_exit_notification_Activity);
        txtName = (TextView)findViewById(R.id.txt_notification_name);
        txtDate = (TextView)findViewById(R.id.txt_notification_date);
        txtNote = (TextView)findViewById(R.id.txt_notification_note);
        txtType = (TextView)findViewById(R.id.txt_notification_type);

        txtName.setText(eventObject.getEventName());
        CustomDateParser parser = new CustomDateParser(eventObject.getEventDate());
        parser.setDateAndTime();
       txtDate.setText("Event on "+ parser.getDate().trim() + " at "+parser.getTime());

       txtType.setText("This is a "+eventObject.getEventType().toLowerCase() +" event");

        String currentEventNote = eventObject.getEventNote();
        if(!currentEventNote.trim().isEmpty()){
           txtNote.setText("Note: "+currentEventNote);
           txtNote.setTypeface(null, Typeface.NORMAL);
        }

        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               backToPreviousActivity();
            }
        });



    }

    private void backToPreviousActivity(){
        Intent myIntent = new Intent(NotificationDialogActivity.this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
        startActivity(myIntent);
    }
}
