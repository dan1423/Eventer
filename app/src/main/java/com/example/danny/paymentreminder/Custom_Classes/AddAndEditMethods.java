package com.example.danny.paymentreminder.Custom_Classes;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.danny.paymentreminder.R;
import com.example.danny.paymentreminder.adapter.CustomEventObject;
import com.example.danny.paymentreminder.sqllite.DBHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*This class holds methods that Adding and Editing Fragments used*/

public class AddAndEditMethods {

    Context context;
    DBHandler dbHandler;
    TextView txtDate, txtTime;
    View view;

    public AddAndEditMethods(Context context, DBHandler dbHandler,View view){
        this.context = context;
        this.dbHandler = dbHandler;
        this.view = view;
        txtDate = (TextView) view.findViewById(R.id.editText_date_of_event);
        txtTime = (TextView) view.findViewById(R.id.editText_time_of_event);
    }

    public boolean doesNameExists(String nameOfPayment){
        dbHandler = new DBHandler(context,null, null, 1);
        String s = dbHandler.databaseToString();
        if(s.equals(nameOfPayment.trim())){
            return true;
        }
        return false;
    }

    public boolean saveEventToDatabase(CustomEventObject customEventObject){
        //  Log.i("event: ", customEventObjectForEditMode.toString());

        dbHandler.addEvent(customEventObject);

        dbHandler.databaseToString();
        return true;
    }

    public Date getDateFromString(String d){

        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");

        try {
            Date date = formatter.parse(d);
            return date;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getTimeFromString(String t){

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        try {
            Date date = formatter.parse(t);
            return date;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void openCalendarDialog(){

        int mYear = 0;
        int mMonth = 0;
        int mDay = 0;

        DatePickerDialog dpd = new DatePickerDialog(context,
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

                       txtDate.setText(sdf.format(myCalendar.getTime()));
                       }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMinDate(System.currentTimeMillis());
        dpd.show();

    }

    public void openTimeDialog(){

        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);


        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalender.set(Calendar.MINUTE, minute);

                    String myFormat = "hh:mm a";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                  txtTime.setText(sdf.format(myCalender.getTime()));

                }
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, false);
        timePickerDialog.setTitle("Time of event");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    public Date mergeDateAndTime(Date date, Date time){
        date.setHours(time.getHours());
        date.setMinutes(time.getMinutes());

        return date;
    }

    //set noti
    public void setupNotification(){

    }

}
