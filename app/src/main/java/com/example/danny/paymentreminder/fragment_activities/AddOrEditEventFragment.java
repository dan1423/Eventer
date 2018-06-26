package com.example.danny.paymentreminder.fragment_activities;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.danny.paymentreminder.Custom_Classes.CustomDateParser;
import com.example.danny.paymentreminder.sqllite.DBHandler;
import com.example.danny.paymentreminder.adapter.EventObject;
import com.example.danny.paymentreminder.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*The purpose of this class is to add new event date or edit event
*  all input fields are required
*  information will be saved to phone's internal storage
* */

public class AddOrEditEventFragment extends Fragment {

    public AddOrEditEventFragment(){

    }
    private View addNewPaymentView;
    Spinner spinnerPaymentType;
    Button btnMain;
    EditText editTextPaymentDate, editTextPaymentName;
    TextView textErrorMessage;
    private ImageView imgExitFrag;

    String errorMessage = "";

    DBHandler dbHandler;

    Bundle bundle;
    EventObject eventObjectForEditMode;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = new DBHandler(getContext(),null, null, 1);
       /*  saveEventToDatabase(new EventObject("Test1",1529545260000L,"One-time"));
        saveEventToDatabase(new EventObject("Test2",1529458860000L,"Monthly"));
        saveEventToDatabase(new EventObject("Test3",new Date().getTime(),"Yearly"));
        saveEventToDatabase(new EventObject("Test4",1529372460000L,"Monthly"));*/

        bundle = new Bundle();
        bundle = this.getArguments();
        if (bundle != null) {//only available when user wants to edit an event
            eventObjectForEditMode = (EventObject) bundle.getSerializable("event_object");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        addNewPaymentView = inflater.inflate(R.layout.layout_for_add_event,container,false);
        spinnerPaymentType = (Spinner)addNewPaymentView.findViewById(R.id.spinner_event_type);
        btnMain = (Button)addNewPaymentView.findViewById(R.id.button_add_new);
        editTextPaymentDate = (EditText)addNewPaymentView.findViewById(R.id.editText_date_of_event);
        editTextPaymentName = (EditText)addNewPaymentView.findViewById(R.id.editText_event_name);
        textErrorMessage = (TextView)addNewPaymentView.findViewById(R.id.text_error_messge);
        imgExitFrag = (ImageView)addNewPaymentView.findViewById(R.id.img_exit_fragment);



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
                openCalendarDialog();
            }
        });




        if(bundle !=null){
            setEditMode();
        }else{
           setAddMode();
        }



        return addNewPaymentView;
    }



    /*****************USER WANTS TO ADD NEW EVENT****************************************/
    private  void setAddMode(){
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!areFieldsSet()){
                    errorMessageHandler("Please enter an event name",View.VISIBLE);
                }  else if(doesNameExists(editTextPaymentName.getText().toString())){
                    errorMessageHandler("Event name exists, please choose a different one",View.VISIBLE);
                }else{
                    textErrorMessage.setVisibility(View.INVISIBLE);
                    Date date = ( getDateFromString ((editTextPaymentDate.getText().toString())));
                    String paymentType = spinnerPaymentType.getSelectedItem().
                            toString().replace("event","").trim();
                    EventObject eventObject = new EventObject(editTextPaymentName.getText().toString()
                            ,date.getTime(),paymentType);
                    saveEventToDatabase(eventObject);
                }

            }
        });

    }

    /*****************************METHODS USED BY ADD MODE*************************************/
    private boolean saveEventToDatabase(EventObject eventObject){
        //  Log.i("event: ", eventObjectForEditMode.toString());

        dbHandler.addEvent(eventObject);

        dbHandler.databaseToString();
        return true;
    }

    private boolean doesNameExists(String nameOfPayment){
        dbHandler = new DBHandler(getContext(),null, null, 1);
        String s = dbHandler.databaseToString();
        if(s.equals(nameOfPayment.trim())){
            return true;
        }
        return false;
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
                    errorMessageHandler("Event name has to be  longer than 3 letters",View.VISIBLE);
                }else{
                    errorMessageHandler("",View.INVISIBLE);
                    Date date = ( getDateFromString ((editTextPaymentDate.getText().toString())));
                    String paymentType = spinnerPaymentType.getSelectedItem().
                            toString().replace("event","").trim();
                    EventObject eventObject = new EventObject(editTextPaymentName.getText().toString()
                            ,date.getTime(),paymentType);
                    eventObject.setEventId(eventObjectForEditMode.getEventId());

                    updateEvent(eventObject);
                }
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




    /****************************MEDTHODS USED BY EDIT MODE**********************************/
    private void setEditFields(){
        btnMain.setText("Save Changes");
        editTextPaymentName.setText(eventObjectForEditMode.getEventName());
        long d = eventObjectForEditMode.getEventDate();
        editTextPaymentDate.setText(new CustomDateParser(d).convertLongToDate());


        int posOfType = getSpinnerSelectionId(eventObjectForEditMode.getEventType());
        if(posOfType != -1){
            spinnerPaymentType.setSelection(posOfType);
        }

       btnMain.setText("Save Changes");

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

    private void updateEvent(EventObject eventObject){
        dbHandler.updateEvent(eventObject);
    }

    /*******************************METHODS USED BY BOTH MODES***************************************************/
    private void errorMessageHandler(String msg, int visibility){
        textErrorMessage.setText(msg);
        textErrorMessage.setVisibility(visibility);
    }

    private Date getDateFromString(String d){

        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");

        try {
            Date date = formatter.parse(d);
            return date;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //check if all input fields are filled/correct
    private boolean areFieldsSet(){
        if(editTextPaymentName.getText().toString().trim().length() <= 0){
            return false;
        }
        if(editTextPaymentDate.getText().toString().trim().length() <= 0){
            return false;
        }

        return true;
    }

    private void openCalendarDialog(){

        int mYear = 0;
        int mMonth = 0;
        int mDay = 0;

        DatePickerDialog dpd = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    final Calendar myCalendar = Calendar.getInstance();
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "MM-dd-yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        editTextPaymentDate.setText(sdf.format(myCalendar.getTime()));

                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMinDate(System.currentTimeMillis());
        dpd.show();

    }

}
