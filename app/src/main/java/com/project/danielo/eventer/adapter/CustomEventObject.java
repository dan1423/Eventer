package com.project.danielo.eventer.adapter;

import java.io.Serializable;
import java.util.Date;

/*This calss is used to populate recyclerview
* it is used as a model for the sqlite database
* it implements serilizable so we can pass it from one fragment(oractivity) to another
* */
public class CustomEventObject implements Serializable{

    String eventName;
    long eventDate;
    long eventTime;
   String eventType;
   String eventNote;
   private int eventId;//eventId is used to uniquely identify event and to set request code of event

    public CustomEventObject(String eventName, long eventDate, String eventType, String eventNote) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventType = eventType;
        this.eventNote = eventNote;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventNote() {
        return eventNote;
    }

    public void setEventNote(String eventNote) {
        this.eventNote = eventNote;
    }

    @Override
    public String toString() {
        return "CustomEventObject{" +
                "eventName='" + eventName + '\'' +
                ", eventDate=" + eventDate +
                ", eventTime=" + eventTime +
                ", eventType='" + eventType + '\'' +
                ", eventNote='" + eventNote + '\'' +
                ", eventId=" + eventId +
                '}';
    }
}
