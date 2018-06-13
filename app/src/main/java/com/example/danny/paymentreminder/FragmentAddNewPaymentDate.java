package com.example.danny.paymentreminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FragmentAddNewPaymentDate extends Fragment {

    public FragmentAddNewPaymentDate(){

    }
    private View addNewPaymentView;
    Spinner spinnerPaymentType;
    Button btnAdd;
    EditText editTextPaymentDate, editTextPaymentName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        addNewPaymentView = inflater.inflate(R.layout.fragment_for_payment_event,container,false);
        spinnerPaymentType = (Spinner)addNewPaymentView.findViewById(R.id.spinner_payment_type);
        btnAdd = (Button)addNewPaymentView.findViewById(R.id.button_add_new);
        editTextPaymentDate = (EditText)addNewPaymentView.findViewById(R.id.editText_date_of_payment);
        editTextPaymentName = (EditText)addNewPaymentView.findViewById(R.id.editText_payment_name);



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

}
