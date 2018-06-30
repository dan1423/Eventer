package com.example.danny.paymentreminder.fragment_activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.danny.paymentreminder.Custom_Classes.CustomClickListener;
import com.example.danny.paymentreminder.dialog_fragments.AddNewEventDialogFragment;
import com.example.danny.paymentreminder.dialog_fragments.CustomAlertDialogs;
import com.example.danny.paymentreminder.dialog_fragments.EditEventDialogFragment;
import com.example.danny.paymentreminder.sqllite.DBHandler;
import com.example.danny.paymentreminder.adapter.EventObject;
import com.example.danny.paymentreminder.adapter.EventObjectAdapter;
import com.example.danny.paymentreminder.R;
import com.example.danny.paymentreminder.StaticVariables;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment {

    public EventListFragment(){

    }

    DBHandler dbHandler;

    private View eventsView;
    private RecyclerView recyclerView;
    private List<EventObject> eventObjects;
    private ImageView imgAdd;
    EventObjectAdapter adapter;


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
        imgAdd = (ImageView)eventsView.findViewById(R.id.img_add_new_event);


       //user clicks add button
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddNewEvent();
            }
        });



        //get all events from database
       eventObjects = dbHandler.queryAllEvents();
       for(int i = 0; i < eventObjects.size(); i++){
           Log.i("EVENT: ",eventObjects.get(i).toString());
       }

       //set edit and delete events
          adapter = new EventObjectAdapter(getContext(), eventObjects, new CustomClickListener() {
            @Override
            public void onDeleteClick(int pos) {
                showDeleteConfirmationDialog(eventObjects.get(pos), pos);

            }

             @Override
             public void onEditClick(int pos) {
                 openEditEventDialog(eventObjects.get(pos));
             }

              @Override
              public void onInfoClick(int pos) {
                showEventDetails(pos);
              }
          });

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);

       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return eventsView;

    }



    private void openAddNewEvent(){
        Fragment fragment = new AddNewEventDialogFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction .setCustomAnimations(R.anim.grow_from_center, R.anim.blank,
                R.anim.blank, R.anim.shrink_to_center);
        fragmentTransaction.replace(R.id.main_layout_screen_bottom_nav,fragment);
        fragmentTransaction.addToBackStack(null);


        fragmentTransaction.commit();

    }


    private void openEditEventDialog(EventObject eventObject){
        Fragment fragment = new EditEventDialogFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction .setCustomAnimations(R.anim.grow_from_center, R.anim.blank,
                R.anim.blank, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_layout_screen_bottom_nav,fragment);
        fragmentTransaction.addToBackStack(null);

        Bundle bundle = new Bundle();
        bundle.putSerializable("event_object", eventObject);

        fragment.setArguments(bundle);
        fragmentTransaction.commit();
    }

    private void showDeleteConfirmationDialog(final EventObject eventObject, final int pos){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_delete_events_confirmation, null);
        builder.setView(dialogView);
        final AlertDialog d = builder.show();
        d.setTitle("Delete " + eventObject.getEventName() +" ?");


        Button btnYes = (Button)dialogView.findViewById(R.id.btnYes);
        Button btnNo = (Button)dialogView.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvent(pos);
                d.dismiss();
            }


        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        builder.create();
    }

    private void deleteEvent( int pos){
        DBHandler dbHandler = new DBHandler(getContext(), null, null, 1);
        dbHandler.deleteEvent(eventObjects.get(pos));
        eventObjects.remove(pos);
        adapter.notifyItemRemoved(pos);
        adapter.notifyItemRangeChanged(pos, eventObjects.size());
    }

    private void showEventDetails(int pos){
        CustomAlertDialogs dialogs = new CustomAlertDialogs(getActivity());
        dialogs.showEventDetails(eventObjects.get(pos));
    }


}
