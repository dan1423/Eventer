package com.example.danny.paymentreminder;

import java.io.Serializable;
import java.util.Date;

public class EventObject implements Serializable{

    String eventName;
    long eventDate;
   String eventType;
   private int eventId;

    public EventObject(String eventName, long eventDate, String eventType) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventType = eventType;
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

    @Override
    public String toString() {
        return "EventObject{" +
                "eventName='" + eventName + '\'' +
                ", eventDate=" + eventDate +
                ", eventType='" + eventType + '\'' +
                ", eventId=" + eventId +
                '}';
    }
}
