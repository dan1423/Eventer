package com.example.danny.paymentreminder.fragment_activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.danny.paymentreminder.Custom_Classes.CustomClickListener;
import com.example.danny.paymentreminder.sqllite.DBHandler;
import com.example.danny.paymentreminder.adapter.EventObject;
import com.example.danny.paymentreminder.adapter.EventObjectAdapter;
import com.example.danny.paymentreminder.R;
import com.example.danny.paymentreminder.StaticVariables;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;

public class EventListFragment extends Fragment {

    public EventListFragment(){

    }

    DBHandler dbHandler;

    private View eventsView;
    private RecyclerView recyclerView;
    private List<EventObject> eventObjects;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = new DBHandler(getContext(),null,null, StaticVariables.VERSION);
        eventObjects = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        eventsView = inflater.inflate(R.layout.recycler_view_for_list_events, container, false);
        recyclerView = (RecyclerView)eventsView.findViewById(R.id.recycler_view);

        //get all events from database
       eventObjects = dbHandler.queryAllEvents();

       for(int i = 0; i < eventObjects.size(); i++){
           Log.i("EVENT: ",eventObjects.get(i).toString());
       }

        EventObjectAdapter adapter = new EventObjectAdapter(getContext(), eventObjects, new CustomClickListener() {
            @Override
            public void onItemClick(int pos) {
               sendToDetailedEvent(eventObjects.get(pos));
            }
        });

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), HORIZONTAL);
        recyclerView.addItemDecoration(itemDecor);

       /* RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);*/

       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return eventsView;

    }





    private void sendToDetailedEvent(EventObject eventObject){
        Fragment fragment = new DetailedEventFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_layout_screen_bottom_nav,fragment);
        fragmentTransaction.addToBackStack(null);

        Bundle bundle = new Bundle();
        bundle.putSerializable("event_object", eventObject);

        fragment.setArguments(bundle);
        fragmentTransaction.commit();
    }


}
