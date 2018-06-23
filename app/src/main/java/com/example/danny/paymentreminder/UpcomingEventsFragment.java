package com.example.danny.paymentreminder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;

public class UpcomingEventsFragment extends Fragment {

    public UpcomingEventsFragment(){

    }
    private View upcomingView;
    CustomLocationManager customLocationManager;
    DBHandler dbHandler;
    Date currentDate;
    RecyclerView recyclerView;
    ArrayList<EventObject> eventObjects;


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

        eventObjects = dbHandler.queryAllEvents();
        eventObjects = getUpcomingEventsInTheCurrentMonth(eventObjects, currentDate);

        if(eventObjects.isEmpty()){
           upcomingView =  inflater.inflate(R.layout.not_available_layout,null,false);
           return upcomingView;
        }

        recyclerView = (RecyclerView)upcomingView.findViewById(R.id.recycler_view_for_upcoming_events);
        EventObjectAdapter adapter = new EventObjectAdapter(getContext(), eventObjects, new CustomClickListener() {
            @Override
            public void onItemClick(int pos) {

            }
        });

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), HORIZONTAL);
        recyclerView.addItemDecoration(itemDecor);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return upcomingView;
    }

    //deal with payment type(if one time payment or not)
    /*to set upcoming events in the same month as current date,
     we must compare dates of the present day with events saved.
     if the event date is not in the same months as present day, it is discarded
     if event is older than present date, it is also discarded
     */
    private ArrayList<EventObject> getUpcomingEventsInTheCurrentMonth
                                    (ArrayList<EventObject> eventObjects1, Date currentDate){
        Iterator<EventObject> it = eventObjects1.iterator();
        while(it.hasNext()){
            if(isDateAnUpcomingEventInCurrentMonth(currentDate,new Date(it.next().getEventDate())) == false ){
               it.remove();
            }
        }
        return eventObjects1;
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
