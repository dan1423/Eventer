package com.example.danny.paymentreminder.dialog_fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.danny.paymentreminder.Custom_Classes.AddAndEditMethods;
import com.example.danny.paymentreminder.Custom_Classes.CustomDateParser;
import com.example.danny.paymentreminder.R;
import com.example.danny.paymentreminder.adapter.CustomEventObject;
import com.example.danny.paymentreminder.notification_package.CustomNotification;
import com.example.danny.paymentreminder.sqllite.DBHandler;

import java.util.Calendar;
import java.util.Date;

public class AddNewEventDialogFragment extends Fragment{

    public AddNewEventDialogFragment(){

    }

    //initialize views
    private View addNewPaymentView;
    Spinner spinnerPaymentType;
    Button btnAdd;
    EditText editTextPaymentDate, editTextPaymentName,editTextTime;
    TextView textErrorMessage;
    private ImageView imgExitFrag;
    AddAndEditMethods methods;

    //initialize custom objects
    DBHandler dbHandler;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = new DBHandler(getContext(),null, null, 1);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        addNewPaymentView = inflater.inflate(R.layout.layout_for_add_event,container,false);
        spinnerPaymentType = (Spinner)addNewPaymentView.findViewById(R.id.spinner_event_type);
        btnAdd = (Button)addNewPaymentView.findViewById(R.id.button_add_new);
        editTextPaymentDate = (EditText)addNewPaymentView.findViewById(R.id.editText_date_of_event);
        editTextPaymentName = (EditText)addNewPaymentView.findViewById(R.id.editText_event_name);
        editTextTime = (EditText)addNewPaymentView.findViewById(R.id.editText_time_of_event);
        textErrorMessage = (TextView)addNewPaymentView.findViewById(R.id.text_error_messge);
        imgExitFrag = (ImageView)addNewPaymentView.findViewById(R.id.img_exit_fragment);

        methods = new AddAndEditMethods(getContext(),dbHandler,addNewPaymentView);


        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.event_type_array,
                        android.R.layout.simple_spinner_item);
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerPaymentType.setAdapter(staticAdapter);


        //when he user clicks the date text box, we want to open a calendar dialog
        editTextPaymentDate.setOnClickListener(new View.OnClickListener() {
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

            setLayoutEvents();

        return addNewPaymentView;
    }

    private  void setLayoutEvents(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!areFieldsSet()){
                    errorMessageHandler("Please enter an event name",View.VISIBLE);
                }  else if(methods.doesNameExists(editTextPaymentName.getText().toString())){
                    errorMessageHandler("Event name exists, please choose a different one",View.VISIBLE);
                }else{
                    textErrorMessage.setVisibility(View.INVISIBLE);

                    //combine time and date
                    Date date = methods.mergeDateAndTime(methods.getDateFromString ((editTextPaymentDate.getText().toString())),
                                                    methods.getTimeFromString(editTextTime.getText().toString()));

                    String paymentType = spinnerPaymentType.getSelectedItem().
                            toString().replace("event","").trim();
                    CustomEventObject customEventObject = new CustomEventObject(editTextPaymentName.getText().toString()
                            ,date.getTime(),paymentType);

                  long id = saveEventToDatabase(customEventObject);
                  setupNotification(customEventObject, id);
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

    private void errorMessageHandler(String msg, int visibility){
        textErrorMessage.setText(msg);
        textErrorMessage.setVisibility(visibility);
    }

    //check if all input fields are filled/correct
    private boolean areFieldsSet(){
        if(editTextPaymentName.getText().toString().trim().length() <= 0){
            return false;
        }
        if(editTextPaymentDate.getText().toString().trim().length() <= 0){
            return false;
        }
        if(editTextTime.getText().toString().trim().length() <= 0){
            return false;
        }

        return true;
    }

    public long saveEventToDatabase(CustomEventObject customEventObject){
        long id = dbHandler.addEvent(customEventObject);
        return id;
    }

    //set notification for this event
    public void setupNotification(CustomEventObject e, long id){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();

        cal.setTimeInMillis(e.getEventDate());
        int notId = (int)id;
        CustomDateParser parser = new CustomDateParser(e.getEventDate());
        parser.setDateAndTime();
        String notificationContent = "Starts at "+parser.getTime();
        CustomNotification notification = new CustomNotification(getContext(), e.getEventName(),
                                                notificationContent, notId,notId,cal.getTimeInMillis());
        notification.setNotification();
        Log.i("CUSTOM",notification.toString());

    }

}
