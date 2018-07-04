package com.example.danny.paymentreminder.fragment_activities;

import android.content.ClipData;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.danny.paymentreminder.Custom_Classes.CustomClickListener;
import com.example.danny.paymentreminder.Custom_Classes.CustomLocationManager;
import com.example.danny.paymentreminder.adapter.CustomEventObject;
import com.example.danny.paymentreminder.adapter.CustomEventObjectAdapter;
import com.example.danny.paymentreminder.sqllite.DBHandler;
import com.example.danny.paymentreminder.R;
import com.example.danny.paymentreminder.StaticVariables;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class UpcomingEventsFragment extends Fragment {

    public UpcomingEventsFragment(){

    }
    private View upcomingView;
    CustomLocationManager customLocationManager;
    DBHandler dbHandler;
    Date currentDate;
    RecyclerView recyclerView;
    ArrayList<CustomEventObject> customEventObjects;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customLocationManager = new CustomLocationManager(getContext());
        long l = customLocationManager.currentTime();
       currentDate = new Date(l);
        dbHandler = new DBHandler(getContext(),null,null, StaticVariables.VERSION);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        upcomingView = inflater.inflate(R.layout.layout_for_upcoming_fragments,null,false);

        customEventObjects = dbHandler.queryAllEvents();
        customEventObjects = getUpcomingEventsInTheCurrentMonth(customEventObjects, currentDate);

        if(customEventObjects.isEmpty()){
           upcomingView =  inflater.inflate(R.layout.not_available_layout,null,false);
           return upcomingView;
        }

        recyclerView = (RecyclerView)upcomingView.findViewById(R.id.recycler_view_for_upcoming_events);
        CustomEventObjectAdapter adapter = new CustomEventObjectAdapter(getContext(), customEventObjects, new CustomClickListener() {

            @Override
            public void onDeleteClick(int pos) {
                //sendToDetailedEvent(customEventObjects.get(pos));
            }

            @Override
            public void onEditClick(int pos) {
               // showDeleteConfirmationDialog();
            }

            @Override
            public void onInfoClick(int pos) {

            }
        });

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        return upcomingView;
    }

    //deal with payment type(if one time payment or not)
    /*to set upcoming events in the same month as current date,
     we must compare dates of the present day with events saved.
     if the event date is not in the same months as present day, it is discarded
     if event is older than present date, it is also discarded
     */
    private ArrayList<CustomEventObject> getUpcomingEventsInTheCurrentMonth
                                    (ArrayList<CustomEventObject> customEventObjects1, Date currentDate){
        Iterator<CustomEventObject> it = customEventObjects1.iterator();
        while(it.hasNext()){
            if(isDateAnUpcomingEventInCurrentMonth(currentDate,new Date(it.next().getEventDate())) == false ){
               it.remove();
            }
        }
        return customEventObjects1;
    }

    private boolean isDateAnUpcomingEventInCurrentMonth(Date currentDate, Date dateOfEvent){
       if(dateOfEvent.before(currentDate)||
               dateOfEvent.getMonth() != currentDate.getMonth()||
               dateOfEvent.getYear() != currentDate.getYear()
               ){
           return false;
       }
       return true;
    }


}
