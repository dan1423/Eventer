package com.example.danny.paymentreminder;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/*The purpose of this class is to update data in the database
 by checking and removing old dates, updating weekly, monthly, yearly events
 this class should be instantiated either by event listener(button click) or on application load
  */
public class UpdateDatabase {

    private Date currentDate;
    private CustomLocationManager locationManager;
    private Context context;
    private ArrayList<EventObject> eventObjects;
    DBHandler dbHandler;


    public UpdateDatabase(Context context){
        this.context = context;
        locationManager = new CustomLocationManager(context);
        currentDate = new Date(locationManager.currentTime());
        dbHandler = new DBHandler(context,null,null, StaticVariables.VERSION);
        eventObjects = dbHandler.queryAllEvents();

    }

    public void updateAllEvents(){
        dbHandler.clearTable();
        updateEvents(this.eventObjects);

    }

    private Date updateMonthly(Date date){
        if(date.getMonth()  > 11){
            date.setMonth(1);
            date.setYear(date.getYear() + 1);
        }else{
            date.setMonth(date.getMonth() + 1);
        }

        return date;
    }

    private Date updateWeekly(Date date){
        int numOfDays = 7;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, numOfDays);
        Date nextWeek = calendar.getTime();

        return nextWeek;
    }

    private Date updateYearly(Date date){
        date.setYear(date.getYear() + 1);

        return date;
    }



    private void updateEvents(ArrayList<EventObject> eventObjects){
        Iterator<EventObject> iterator = eventObjects.iterator();
        while(iterator.hasNext()){
            EventObject event = iterator.next();
            Date eventDate = new Date(event.getEventDate());
            String eventType = event.getEventType();

            //the event is past and done, no need to have in database
            if(eventDate.before(currentDate) && eventType.trim().equals("One-time")){
                iterator.remove();
            //set the next date of the event
            }else if(eventDate.before(currentDate)){
               if(eventType.equals("Weekly")){
                   event.setEventDate(updateWeekly(eventDate).getTime());
               }else if(eventType.equals("Monthly")){
                   event.setEventDate(updateMonthly(eventDate).getTime());
               }else if(eventType.equals("Yearly")){
                   event.setEventDate(updateYearly(eventDate).getTime());
               }
            }
        }
        dbHandler.addMultipleEvents(eventObjects);
    }


}
