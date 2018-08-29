package com.project.danielo.eventer.dialog_fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.danielo.eventer.Custom_Classes.CustomDateParser;
import com.project.danielo.eventer.R;
import com.project.danielo.eventer.adapter.CustomEventObject;

public class AboutEventFragment extends Fragment {


    private TextView eventName,eventDate,eventNote,eventType;
    private ImageView imgExitFragment;
    private View aboutEventView;
    private CustomEventObject eventFromBundle;
    private Bundle bundle;


    public AboutEventFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = new Bundle();
        bundle = this.getArguments();
        if (bundle != null) {//only available when user wants to edit an event
            eventFromBundle = (CustomEventObject) bundle.getSerializable("event_object");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        aboutEventView = inflater.inflate(R.layout.layout_for_event_details_dialog,null,false);
        eventName =(TextView) aboutEventView.findViewById(R.id.txt_notification_name);
        eventDate = (TextView) aboutEventView.findViewById(R.id.txt_notification_date);
        eventNote = (TextView)aboutEventView.findViewById(R.id.txt_notification_note);
        eventType =(TextView) aboutEventView.findViewById(R.id.txt_notification_type);
        imgExitFragment =(ImageView) aboutEventView.findViewById(R.id.img_exit_notification_Activity);

        eventName.setText(eventFromBundle.getEventName());
        CustomDateParser parser = new CustomDateParser(eventFromBundle.getEventDate());
        parser.setDateAndTime();
        eventDate.setText("Event on "+ parser.getDate().trim() + " at "+parser.getTime());

        eventType.setText("This is a "+eventFromBundle.getEventType().toLowerCase() +" event");

        String currentEventNote = eventFromBundle.getEventNote();
        if(!currentEventNote.trim().isEmpty()){
            eventNote.setText("Note: "+currentEventNote);
            eventNote.setTypeface(null, Typeface.NORMAL);
        }

        imgExitFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitAddDialog();
            }
        });

        return aboutEventView;
    }

    private void exitAddDialog(){
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
    }
}
