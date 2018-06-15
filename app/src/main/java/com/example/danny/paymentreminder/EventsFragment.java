package com.example.danny.paymentreminder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {

    public EventsFragment(){

    }

    DBHandler dbHandler;

    private View eventsView;
    private RecyclerView recyclerView;
    private List<EventObject> eventObjects;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = new DBHandler(getContext(),null,null,1);
        eventObjects = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        eventsView = inflater.inflate(R.layout.recycler_view_for_list_events, container, false);
        recyclerView = (RecyclerView)eventsView.findViewById(R.id.recycler_view);


        EventObject eventObject = new EventObject("sdfsdfsd",(long)345453,1);
        EventObject eventObject2 = new EventObject("sdfsdfsd",(long)345453,1);
        EventObject eventObject3 = new EventObject("sdfsdfsd",(long)345453,1);
        eventObjects.add(eventObject);
        eventObjects.add(eventObject2);
        eventObjects.add(eventObject3);

        EventObjectAdapter adapter = new EventObjectAdapter(getContext(), eventObjects);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return eventsView;

    }
}
