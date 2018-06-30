package com.example.danny.paymentreminder.dialog_fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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

import com.example.danny.paymentreminder.R;
import com.example.danny.paymentreminder.adapter.EventObject;
import com.example.danny.paymentreminder.sqllite.DBHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNewEventDialogFragment extends Fragment{

    public AddNewEventDialogFragment(){

    }

    //initialize views
    private View addNewPaymentView;
    Spinner spinnerPaymentType;
    Button btnMain;
    EditText editTextPaymentDate, editTextPaymentName;
    TextView textErrorMessage;
    private ImageView imgExitFrag;

    //initialize custom objects
    DBHandler dbHandler;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = new DBHandler(getContext(),null, null, 1);
      /* saveEventToDatabase(new EventObject("Test1",1529545260000L,"One-time"));
        saveEventToDatabase(new EventObject("Test2",1529458860000L,"Monthly"));
        saveEventToDatabase(new EventObject("Test3",new Date().getTime(),"Yearly"));
        saveEventToDatabase(new EventObject("Test4",1529372460000L,"Monthly"));*/
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

            setLayoutEvents();

        return addNewPaymentView;
    }

    private  void setLayoutEvents(){
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

        imgExitFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });

    }

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
