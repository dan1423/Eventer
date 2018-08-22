package com.project.danielo.eventer.Custom_Classes;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

//custom regular expressions to validate each item in an event string from imported file
public class CustomRegex {

    public CustomRegex(){

    }

    public boolean validateEventName(String eventName){
        if(eventName.isEmpty() || !eventName.matches("[a-zA-Z]+")){
            return false;

        }
        return true;
    }

    public boolean validateEvenDate(String dateString){
        if(dateString.isEmpty()){
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(dateString);

        }
        catch (ParseException e) {
            return false;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }



    public boolean validateHour(String hour){
        int number = -1;
       try{
           number = Integer.parseInt(hour);
       }catch (NumberFormatException e){
           return false;
       }
       if(number < 1 || number > 12){
           return false;
       }

       return true;
    }

    public boolean validateMinute(String minute){

        int number = -1;
        try{
            number = Integer.parseInt(minute);
        }catch (NumberFormatException e){
            return false;
        }
        if(number < 0 || number > 59){
            return false;
        }

        return true;

    }

    public boolean validatePeriod(String period){
        if(period.isEmpty() || period.length() > 2){
            return false;
        }
        period = period.toUpperCase();
        if(period.equals("AM")){
            return true;
        }
        if(period.equals("PM")){
            return true;
        }

        return false;
    }

    public boolean validateEventType(String eventType){

        if (eventType.isEmpty()){
            return false;
        }

        eventType = eventType.toLowerCase().trim();

        if(eventType.equals("one-time") ||
            eventType.equals("daily")||
            eventType.equals("weekly")||
            eventType.equals("monthly")||
            eventType.equals("yearly")){
            return true;
        }
        return false;
    }
}
