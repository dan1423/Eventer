package com.example.danny.paymentreminder.fragment_activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.danny.paymentreminder.Custom_Classes.CustomDateParser;
import com.example.danny.paymentreminder.sqllite.DBHandler;
import com.example.danny.paymentreminder.adapter.EventObject;
import com.example.danny.paymentreminder.R;

public class DetailedEventFragment extends Fragment {
    private View detailedView;
    EventObject eventObject;
    Bundle bundle;

    TextView txtEventName,txtEventDate,txtEventType;
    Button btnEditEvent, btnDeleteEvent;
    ImageView imageViewExit;

    public DetailedEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       bundle = new Bundle();
        bundle = this.getArguments();
        if (bundle != null) {
           eventObject = (EventObject) bundle.getSerializable("event_object");

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        detailedView = inflater.inflate(R.layout.layout_for_detailed_event, container, false);
        txtEventName = (TextView)detailedView.findViewById(R.id.txt_event_name);
        txtEventDate = (TextView)detailedView.findViewById(R.id.txt_event_date);
        txtEventType = (TextView)detailedView.findViewById(R.id.txt_event_type);

        btnEditEvent = (Button)detailedView.findViewById(R.id.btn_edit_event);
        btnDeleteEvent = (Button)detailedView.findViewById(R.id.btn_delete_event);

        imageViewExit = (ImageView)detailedView.findViewById(R.id.img_exit_detailed);

        txtEventName.setText(eventObject.getEventName().toUpperCase());
        txtEventDate.setText("NEXT DATE: "+ ( new CustomDateParser(eventObject.getEventDate()).convertLongToDate().toString()));
        txtEventType.setText("EVENT TYPE: "+eventObject.getEventType());

        btnEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToEditEvent();
            }
        });

        imageViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToPreviousFragment();
            }
        });

        btnDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    deleteEvent();
            }
        });

        return detailedView;

    }

    private void deleteEvent(){
        DBHandler dbHandler = new DBHandler(getContext(), null, null, 1);
        dbHandler.deleteEvent(eventObject);
    }

    private void sendToEditEvent(){
        Fragment fragment = new AddOrEditEventFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_layout_screen_bottom_nav,fragment);
        fragmentTransaction.addToBackStack(null);

        Bundle bundle = new Bundle();
        bundle.putSerializable("event_object", eventObject);

        fragment.setArguments(bundle);
        fragmentTransaction.commit();
    }


    private void backToPreviousFragment(){
        FragmentManager fm = getFragmentManager();
            fm.popBackStack();
    }


}

