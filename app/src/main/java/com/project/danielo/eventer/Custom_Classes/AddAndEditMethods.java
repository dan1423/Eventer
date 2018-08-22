package com.project.danielo.eventer.Custom_Classes;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.project.danielo.eventer.R;
import com.project.danielo.eventer.StaticVariables;
import com.project.danielo.eventer.adapter.CustomEventObject;
import com.project.danielo.eventer.notification_package.NotificationsSetter;
import com.project.danielo.eventer.sqllite.DBHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*This class holds methods that are used to create  or edit event objects*/

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



   public AddAndEditMethods(Context context, DBHandler handler){
       this.context = context;
       this.dbHandler = handler;
   }

    public AddAndEditMethods(){

    }

    //extract date from string and return date object
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

    //extract time from string and return time as date object
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

    /*when user is adding or editing an event object,
    /* this dialog opens when user wants to add or edit date
     */
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
    /*when user is adding or editing an event object,
       /* this dialog opens when user wants to add or edit time
        */
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


    /*if user attempts to add an event without typing in a name, this dialog pops up to prompt the user*/
    public  void showEnterNameDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("!");
        alertDialog.setMessage("Please enter event name");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    //this methods combines both time and date(both date objects) as one date object
    public Date mergeDateAndTime(Date date, Date time){
        date.setHours(time.getHours());
        date.setMinutes(time.getMinutes());

        return date;
    }


    public long addNewEventToDatabase(CustomEventObject eventObject){
       ArrayList<CustomEventObject> eventObjects = dbHandler.queryAllEvents();
       String name = eventObject.getEventName().trim() ;
       int num = 1;
       for(int i = 0; i < eventObjects.size(); i++){

           if (name.equals(eventObjects.get(i).getEventName())){
               name = eventObject.getEventName() +"("+num+")";
               num++;

           }
       }
        eventObject.setEventName(name);
        long id = dbHandler.addEvent(eventObject);
        return id;

    }

    /*this method is responsible for setting up alarm notification of an event object*/
    public void setupNotification(CustomEventObject e, long id){
        NotificationsSetter notificationsSetter = new NotificationsSetter(context);
        notificationsSetter.setUpInitialNotification(e, id);
        notificationsSetter.setUpMinutesBeforeNotification(e,id);
        notificationsSetter.setUpFinalNotification(e,id);

    }
}
