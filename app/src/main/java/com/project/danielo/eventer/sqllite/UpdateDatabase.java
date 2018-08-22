package com.project.danielo.eventer.sqllite;

import android.content.Context;

import com.project.danielo.eventer.Custom_Classes.CustomLocationManager;
import com.project.danielo.eventer.adapter.CustomEventObject;
import com.project.danielo.eventer.StaticVariables;

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
    private ArrayList<CustomEventObject> customEventObjects;
    DBHandler dbHandler;


    public UpdateDatabase(Context context){
        this.context = context;
        locationManager = new CustomLocationManager(context);
        currentDate = new Date(locationManager.currentTime());
        dbHandler = new DBHandler(context,null,null, StaticVariables.DATABASE_VERSION);
        customEventObjects = dbHandler.queryAllEvents();

    }

    public void updateAllEvents(){
        dbHandler.clearTable();
        updateEvents(this.customEventObjects);

    }

    public Date updateDaily(Date date){
        int plusOne = 1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, plusOne);
        Date tommorrow = calendar.getTime();

        return tommorrow;

    }

    private Date updateWeekly(Date date){
        int numOfDays = 7;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, numOfDays);
        Date nextWeek = calendar.getTime();

        return nextWeek;
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



    private Date updateYearly(Date date){
        date.setYear(date.getYear() + 1);

        return date;
    }



    private void updateEvents(ArrayList<CustomEventObject> customEventObjects){
        Iterator<CustomEventObject> iterator = customEventObjects.iterator();
        while(iterator.hasNext()){
            CustomEventObject event = iterator.next();
            Date eventDate = new Date(event.getEventDate());
            String eventType = event.getEventType();

            //the event is past and done, no need to have in database
            if(!eventDate.after(currentDate)  && !isSameDay(currentDate, eventDate) && eventType.trim().equals("One-time")){
                 iterator.remove();
            //set the next date of the event
            }else if(eventDate.before(currentDate)){
               if(eventType.equals("Weekly")){
                   event.setEventDate(updateWeekly(eventDate).getTime());
               }else if(eventType.equals("Monthly")){
                   event.setEventDate(updateMonthly(eventDate).getTime());
               }else if(eventType.equals("Yearly")){
                   event.setEventDate(updateYearly(eventDate).getTime());
               }else if(eventType.equals("Daily")){
                   event.setEventDate(updateDaily(eventDate).getTime());
               }
            }
        }
        dbHandler.addMultipleEvents(customEventObjects);
    }

    private boolean isSameDay(Date d1, Date d2){
        if(d1.getDay() != d2.getDay() || d1.getMonth() != d2.getMonth() ||d1.getYear() != d2.getYear()){
            return false;
        }
        return true;
    }


}
