package com.project.danielo.eventer.Custom_Classes;

import com.project.danielo.eventer.adapter.CustomEventObject;

import java.util.ArrayList;

public class EventsToCsvString {
    /*The purpose of this class is to convert current events in the database to csv string format
     */

    ArrayList<CustomEventObject> eventObjects;
    public EventsToCsvString(ArrayList<CustomEventObject>eventObjects){
        this.eventObjects = eventObjects;
    }

    public String getEventsAsCsvString(){
        if(this.eventObjects == null){
            return "";
        }

        return convertEventsToCsv();
    }

    private String convertEventsToCsv(){
        StringBuilder builder = new StringBuilder("Event,Date,Time,Event-Type\n");
        CustomDateParser parser;

        for(int i = 0; i < eventObjects.size(); i++){
            String eventName = eventObjects.get(i).getEventName();

            parser = new  CustomDateParser(eventObjects.get(i).getEventDate());
            parser.setDateAndTime();

            String eventDate = parser.getDate();

            String eventTime = parser.getTime();

            String eventType = eventObjects.get(i).getEventType();

            builder.append(eventName +","+eventDate+","+eventTime+","+eventType+"\n");

        }

        return  builder.toString();

    }
}
