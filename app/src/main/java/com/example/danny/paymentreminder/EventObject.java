package com.example.danny.paymentreminder;

public class EventObject {

    String eventName;
    long eventDate;
    int eventType;

    public EventObject(String eventName, long eventDate, int eventType) {
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

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "EventObject{" +
                "eventName='" + eventName + '\'' +
                ", eventDate=" + eventDate +
                ", eventType=" + eventType +
                '}';
    }
}
