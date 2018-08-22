package com.project.danielo.eventer.dialog_fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.project.danielo.eventer.Custom_Classes.AddAndEditMethods;
import com.project.danielo.eventer.R;
import com.project.danielo.eventer.StaticVariables;
import com.project.danielo.eventer.adapter.CustomEventObject;
import com.project.danielo.eventer.sqllite.DBHandler;

import java.util.Date;

public class AddNewEventDialogFragment extends Fragment{

    public AddNewEventDialogFragment(){

    }

    //initialize views
    private View addNewEventView;
    Spinner spinnerEventType;
    Button btnAdd;
    EditText addEventType, addEventName, addEventTime;
    private ImageView imgExitFrag;
    AddAndEditMethods methods;

    //initialize custom objects
    DBHandler dbHandler;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = new DBHandler(getContext(),null, null, StaticVariables.DATABASE_VERSION);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        addNewEventView = inflater.inflate(R.layout.layout_for_add_event,container,false);
        spinnerEventType = (Spinner) addNewEventView.findViewById(R.id.spinner_event_type);
        btnAdd = (Button) addNewEventView.findViewById(R.id.button_add_new);
        addEventType = (EditText) addNewEventView.findViewById(R.id.editText_date_of_event);
        addEventName = (EditText) addNewEventView.findViewById(R.id.editText_event_name);
        addEventTime = (EditText) addNewEventView.findViewById(R.id.editText_time_of_event);
        imgExitFrag = (ImageView) addNewEventView.findViewById(R.id.img_exit_fragment);

        methods = new AddAndEditMethods(getContext(),dbHandler, addNewEventView);


        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.event_type_array,
                        android.R.layout.simple_spinner_item);
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerEventType.setAdapter(staticAdapter);


        //when he user clicks the date text box, we want to open a calendar dialog
        addEventType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               methods.openCalendarDialog();
            }
        });

        addEventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               methods.openTimeDialog();
            }
        });

            setLayoutEvents();

        return addNewEventView;
    }

    private  void setLayoutEvents(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!areFieldsSet()){
                   methods.showEnterNameDialog();
                }  else{
                    //combine time and date
                    Date date = methods.mergeDateAndTime(methods.getDateFromString ((addEventType.getText().toString())),
                                                    methods.getTimeFromString(addEventTime.getText().toString()));

                    String paymentType = spinnerEventType.getSelectedItem().
                            toString().replace("event","").trim();
                    CustomEventObject customEventObject = new CustomEventObject(addEventName.getText().toString()
                            ,date.getTime(),paymentType);

                  long id = methods.addNewEventToDatabase(customEventObject);
                  if(isNotificationOn()){
                     methods.setupNotification(customEventObject,id);
                  }

                  exitAddDialog();
                }

            }
        });

        imgExitFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               exitAddDialog();
            }
        });

    }

    private void exitAddDialog(){
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
    }


    //check if all input fields are filled/correct
    private boolean areFieldsSet(){
        if(addEventName.getText().toString().trim().length() <= 0){
            return false;
        }
        if(addEventType.getText().toString().trim().length() <= 0){
            return false;
        }
        if(addEventTime.getText().toString().trim().length() <= 0){
            return false;
        }

        return true;
    }

    //check if notitifcations settings are on before adding event notification
    private boolean isNotificationOn(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isOn = preferences.getBoolean("notifications", true);
        return isOn;
    }


}
