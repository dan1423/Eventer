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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.danny.paymentreminder.Custom_Classes.CustomClickListener;
import com.example.danny.paymentreminder.adapter.CustomEventObject;
import com.example.danny.paymentreminder.adapter.CustomEventObjectAdapter;
import com.example.danny.paymentreminder.dialog_fragments.AddNewEventDialogFragment;
import com.example.danny.paymentreminder.dialog_fragments.CustomAlertDialogs;
import com.example.danny.paymentreminder.dialog_fragments.EditEventDialogFragment;
import com.example.danny.paymentreminder.sqllite.DBHandler;
import com.example.danny.paymentreminder.R;
import com.example.danny.paymentreminder.StaticVariables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class EventListFragment extends Fragment {

    public EventListFragment(){

    }

    DBHandler dbHandler;

    private View eventsView;
    private RecyclerView recyclerView;
    private List<CustomEventObject> customEventObjects;
    private ImageView imgAdd;
    Spinner sortSpinner;
    CustomEventObjectAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = new DBHandler(getContext(),null,null, StaticVariables.VERSION);
        customEventObjects = new ArrayList<>();
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        eventsView = inflater.inflate(R.layout.recycler_view_for_list_events, container, false);
        recyclerView = (RecyclerView)eventsView.findViewById(R.id.recycler_view);
        imgAdd = (ImageView)eventsView.findViewById(R.id.img_add_new_event);
        sortSpinner = (Spinner)eventsView.findViewById(R.id.spinner_sort_dropdown);

        //set spinner
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.sort_type_list,
                        android.R.layout.simple_spinner_item);
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sortSpinner.setAdapter(staticAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item is selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                sort(pos);
                refreshList();

            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, just another required interface callback
            }

        });


        //user clicks add button
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddNewEvent();
            }
        });



        //get all events from database
       customEventObjects = dbHandler.queryAllEvents();
       for(int i = 0; i < customEventObjects.size(); i++){
           Log.i("EVENT: ", customEventObjects.get(i).toString());
       }

       //set edit and delete events
          adapter = new CustomEventObjectAdapter(getContext(), customEventObjects, new CustomClickListener() {
            @Override
            public void onDeleteClick(int pos) {
                showDeleteConfirmationDialog(customEventObjects.get(pos), pos);

            }

             @Override
             public void onEditClick(int pos) {
                 openEditEventDialog(customEventObjects.get(pos));
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

    //based on user's selection, we sort recyclerview using Java collections
    private void sort(final int pos){
        Collections.sort(customEventObjects, new Comparator<CustomEventObject>() {
            @Override public int compare(CustomEventObject ev1, CustomEventObject ev2) {

                if(pos == 1){
                    return new Date(ev1.getEventDate()) . compareTo(new Date(ev2.getEventDate()));
                }else if(pos == 2){
                    return new Date(ev2.getEventDate()) . compareTo(new Date(ev1.getEventDate()));

                }
                return ev1.getEventName() . compareTo(ev2.getEventName());

            }

        });
    }

    private void refreshList(){
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);

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


    private void openEditEventDialog(CustomEventObject customEventObject){
        Fragment fragment = new EditEventDialogFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction .setCustomAnimations(R.anim.grow_from_center, R.anim.blank,
                R.anim.blank, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_layout_screen_bottom_nav,fragment);
        fragmentTransaction.addToBackStack(null);

        Bundle bundle = new Bundle();
        bundle.putSerializable("event_object", customEventObject);

        fragment.setArguments(bundle);
        fragmentTransaction.commit();
    }

    private void showDeleteConfirmationDialog(final CustomEventObject customEventObject, final int pos){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_delete_events_confirmation, null);
        builder.setView(dialogView);
        final AlertDialog d = builder.show();
        d.setTitle("Delete " + customEventObject.getEventName() +" ?");


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
        dbHandler.deleteEvent(customEventObjects.get(pos));
        customEventObjects.remove(pos);
        adapter.notifyItemRemoved(pos);
        adapter.notifyItemRangeChanged(pos, customEventObjects.size());
    }

    private void showEventDetails(int pos){
        CustomAlertDialogs dialogs = new CustomAlertDialogs(getActivity());
        dialogs.showEventDetails(customEventObjects.get(pos));
    }


}
