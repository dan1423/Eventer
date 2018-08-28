package com.project.danielo.eventer.dialog_fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.project.danielo.eventer.Custom_Classes.AddAndEditMethods;
import com.project.danielo.eventer.Custom_Classes.CustomDateParser;
import com.project.danielo.eventer.StaticVariables;
import com.project.danielo.eventer.adapter.CustomEventObject;
import com.project.danielo.eventer.sqllite.DBHandler;
import com.project.danielo.eventer.R;

import java.util.ArrayList;
import java.util.Date;

/*The purpose of this class is to edit event
*  all input fields are required
*  information will be saved to phone's internal storage
* */

public class EditEventDialogFragment extends DialogFragment {

    public EditEventDialogFragment(){

    }

    public static EditEventDialogFragment newInstance(String title) {
       EditEventDialogFragment frag = new EditEventDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    //initialize views
    private View editEventView;
    Spinner spinnerEventType;
    Button btnMain, btnEdit;
    EditText editTextEventDate, editTextEventName, editTextTime;
    private ImageView imgExitFrag;
    AddAndEditMethods methods;

    //initialize custom objects
    DBHandler dbHandler;
    Bundle bundle;
    CustomEventObject customEventObjectForEditMode;
    String currentEventName  = "";
    String eventNote = "";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);

        dbHandler = new DBHandler(getContext(),null, null, StaticVariables.DATABASE_VERSION);
        bundle = new Bundle();
        bundle = this.getArguments();
        if (bundle != null) {//only available when user wants to edit an event
            customEventObjectForEditMode = (CustomEventObject) bundle.getSerializable("event_object");
            currentEventName = customEventObjectForEditMode.getEventName();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        editEventView = inflater.inflate(R.layout.layout_for_add_event,container,false);
        spinnerEventType = (Spinner) editEventView.findViewById(R.id.spinner_event_type);
        btnMain = (Button) editEventView.findViewById(R.id.button_add_new);
        btnEdit = (Button) editEventView.findViewById(R.id.btn_add_note);
        editTextEventDate = (EditText) editEventView.findViewById(R.id.editText_date_of_event);
        editTextEventName = (EditText) editEventView.findViewById(R.id.editText_event_name);
        editTextTime = (EditText) editEventView.findViewById(R.id.editText_time_of_event) ;
        imgExitFrag = (ImageView) editEventView.findViewById(R.id.img_exit_fragment);

        methods = new AddAndEditMethods(getContext(),dbHandler, editEventView);



        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.event_type_array,
                        android.R.layout.simple_spinner_item);
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
      spinnerEventType.setAdapter(staticAdapter);


      //when he user clicks the date text box, we want to open a calendar dialog
        editTextEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.openCalendarDialog();
            }
        });
        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.openTimeDialog();
            }
        });

        if(bundle !=null) {
            setEditMode();
        }
        return editEventView;
    }

    /*****************USER WANTS TO EDIT AN EXISTING EVENT****************************************/
    private void setEditMode(){
        getActivity().setTitle("Edit Event");
        //change button text
        setEditFields();
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!areFieldsSet()){
                   methods.showEnterNameDialog();
                }else{

                    Date date = methods.mergeDateAndTime(methods.getDateFromString ((editTextEventDate.getText().toString())),
                            methods.getTimeFromString(editTextTime.getText().toString()));

                    String eventType = spinnerEventType.getSelectedItem().
                            toString().replace("event","").trim();
                    CustomEventObject customEventObject = new CustomEventObject(editTextEventName.getText().toString()
                            ,date.getTime(),eventType,eventNote);
                    customEventObject.setEventId(customEventObjectForEditMode.getEventId());

                    updateEvent(customEventObject);

                    //showCheckMark();
                    if(isNotificationOn()){
                        methods.setupNotification(customEventObject, customEventObject.getEventId());
                    }

                    backToPreviousFragment();

                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditNoteDialog();
            }
        });

        //show exit button
        imgExitFrag.setVisibility(View.VISIBLE);
        imgExitFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToPreviousFragment();
            }
        });


    }


    private void setEditFields(){
        btnMain.setText("Save Changes");
        editTextEventName.setText(customEventObjectForEditMode.getEventName());


        CustomDateParser parser = new CustomDateParser(customEventObjectForEditMode.getEventDate());
        parser.setDateAndTime();
        editTextEventDate.setText(parser.getDate());

        editTextTime.setText(parser.getTime());

        eventNote = customEventObjectForEditMode.getEventNote();


        int posOfType = getSpinnerSelectionId(customEventObjectForEditMode.getEventType());
        if(posOfType != -1){
            spinnerEventType.setSelection(posOfType);
        }

       btnMain.setText("Save Changes");
        btnEdit.setText("Edit Event Note");

    }

    private int getSpinnerSelectionId(String option){
        Resources res = getResources();
        String[] event_array = res.getStringArray(R.array.event_type_array);
        for(int i = 0;i < event_array.length; i++){
            if(event_array[i].trim().contains(option)){
                return i;
            }
        }
        return -1;
    }

    private void backToPreviousFragment(){
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
    }

    private void updateEvent(CustomEventObject customEventObject){
        ArrayList<CustomEventObject> eventObjects = dbHandler.queryAllEvents();
        //if the name hasn't changed, no need to update it
        if(!currentEventName.trim().equals(customEventObject.getEventName().trim())) {
            String name = customEventObject.getEventName().trim();
            int num = 1;
            for (int i = 0; i < eventObjects.size(); i++) {

                if (name.equals(eventObjects.get(i).getEventName())) {
                    name = customEventObject.getEventName() + "(" + num + ")";
                    num++;

                }
            }
            customEventObject.setEventName(name);
        }
        dbHandler.updateEvent(customEventObject);
    }

    //check if all input fields are filled/correct
    private boolean areFieldsSet(){
        if(editTextEventName.getText().toString().trim().length() <= 0){
            return false;
        }
        if(editTextEventDate.getText().toString().trim().length() <= 0){
            return false;
        }

        return true;
    }

    private void openEditNoteDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit event note");

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        input.setSingleLine(false);
        input.setText(eventNote);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventNote = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private boolean isNotificationOn(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isOn = preferences.getBoolean("notifications", true);
        return isOn;
    }

}
