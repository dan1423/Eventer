package com.example.danny.paymentreminder;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*The purpose of this class is to add new payment date
*  all input fields are required
*  information will be saved to phone's internal storage
* */

public class FragmentAddNewEvent extends Fragment {

    public FragmentAddNewEvent(){

    }
    private View addNewPaymentView;
    Spinner spinnerPaymentType;
    Button btnAdd;
    EditText editTextPaymentDate, editTextPaymentName;
    TextView textErrorMessage;

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
        spinnerPaymentType = (Spinner)addNewPaymentView.findViewById(R.id.spinner_payment_type);
        btnAdd = (Button)addNewPaymentView.findViewById(R.id.button_add_new);
        editTextPaymentDate = (EditText)addNewPaymentView.findViewById(R.id.editText_date_of_event);
        editTextPaymentName = (EditText)addNewPaymentView.findViewById(R.id.editText_event_name);
        textErrorMessage = (TextView)addNewPaymentView.findViewById(R.id.text_error_messge);



        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.payment_type_array,
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

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!areFieldsSet()){
                    textErrorMessage.setVisibility(View.VISIBLE);
                }else if(doesNameExists(editTextPaymentName.getText().toString())){
                  textErrorMessage.setText("Name already exists, choose a diffeent name");
                  textErrorMessage.setVisibility(View.VISIBLE);
                }else{
                    textErrorMessage.setVisibility(View.INVISIBLE);
                    long date = getDateFromString(editTextPaymentDate.getText().toString()).getTime();
                    EventObject eventObject = new EventObject(editTextPaymentName.getText().toString()
                            ,date,spinnerPaymentType.getSelectedItemPosition());
                    saveEventToDatabase(eventObject);
                }
            }
        });


        return addNewPaymentView;
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

    //check if all input fields are filled/correct
    private boolean areFieldsSet(){
       if(editTextPaymentName.getText().toString().trim().length() < 3){
           return false;
       }
       if(editTextPaymentDate.getText().toString().trim().length() <= 0){
           return false;
       }

       return true;
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
    private boolean saveEventToDatabase(EventObject eventObject){
      //  Log.i("event: ", eventObject.toString());

        dbHandler.addEvent(eventObject);

        dbHandler.databaseToString();
        return true;
    }

    private boolean doesNameExists(String nameOfPayment){
        dbHandler = new DBHandler(getContext(),null, null, 1);
        String s = dbHandler.databaseToString();
        if(s.contains(nameOfPayment)){
            return true;
        }
        return false;
    }

}
