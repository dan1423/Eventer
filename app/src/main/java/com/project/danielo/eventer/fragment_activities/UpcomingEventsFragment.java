package com.project.danielo.eventer.fragment_activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.danielo.eventer.Custom_Classes.CustomClickListener;
import com.project.danielo.eventer.Custom_Classes.CustomLocationManager;
import com.project.danielo.eventer.adapter.CustomEventObject;
import com.project.danielo.eventer.adapter.CustomEventObjectAdapter;
import com.project.danielo.eventer.dialog_fragments.AboutEventFragment;
import com.project.danielo.eventer.sqllite.DBHandler;
import com.project.danielo.eventer.R;
import com.project.danielo.eventer.StaticVariables;

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
    CustomEventObjectAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customLocationManager = new CustomLocationManager(getContext());
        long l = customLocationManager.currentTime();
       currentDate = new Date(l);
        dbHandler = new DBHandler(getContext(),null,null, StaticVariables.DATABASE_VERSION);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        upcomingView = inflater.inflate(R.layout.layout_for_upcoming_fragments,null,false);

        customEventObjects = dbHandler.queryAllEvents();
        customEventObjects = getUpcomingEventsInTheCurrentMonth(customEventObjects, currentDate);

        if(customEventObjects.isEmpty()){
           upcomingView =  inflater.inflate(R.layout.layout_for_not_available_dialog,null,false);
           return upcomingView;
        }

        recyclerView = (RecyclerView)upcomingView.findViewById(R.id.recycler_view_for_upcoming_events);
        adapter = new CustomEventObjectAdapter(getContext(), customEventObjects, new CustomClickListener() {

            @Override
            public void onDeleteClick(int pos) {
                openCannotDeleteDialog();
            }

            @Override
            public void onEditClick(int pos) {
              openCannotEditDialog();
            }

            @Override
            public void onInfoClick(int pos) {
                openAboutEventDialog(customEventObjects.get(pos));
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



    private void openCannotEditDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Note");
        alertDialog.setMessage("You cannot edit from this menu, Edit from main menu");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void openCannotDeleteDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Note");
        alertDialog.setMessage("You cannot delete from this menu, Delete from main menu");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void openAboutEventDialog(CustomEventObject customEventObject){

        Fragment fragment = new AboutEventFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction .setCustomAnimations(R.anim.grow_from_center, R.anim.blank,
                R.anim.blank, R.anim.shrink_to_center);
        fragmentTransaction.replace(R.id.main_nav,fragment);
        fragmentTransaction.addToBackStack(null);

        Bundle bundle = new Bundle();
        bundle.putSerializable("event_object", customEventObject);

        fragment.setArguments(bundle);
        fragmentTransaction.commit();
    }


}
