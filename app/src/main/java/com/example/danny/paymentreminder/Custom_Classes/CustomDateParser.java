package com.example.danny.paymentreminder.Custom_Classes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/*The purpose of this class is to convert a long object to date string and time*/
public class CustomDateParser {
    private long longDate;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy    ");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    private String dateAsString = "";
    private String timeAsString = "";

    public CustomDateParser(long longDate) {
        this.longDate = longDate;
    }

    public CustomDateParser() {
    }

    public long getLongDate() {
        return longDate;
    }

    public void setLongDate(long longDate) {
        this.longDate = longDate;
    }

    public void setDateAndTime(){
        dateAsString = convertLongToDate();
        timeAsString = convertLongToTime();
    }

    public String getDate(){
        return  this.dateAsString;
    }

    public String getTime(){
        return this.timeAsString;
    }

    private String convertLongToDate(){
        Date d = new Date(this.longDate);

        return  simpleDateFormat.format(d);
    }

    private String convertLongToTime(){
        Date date = new Date(this.longDate);
        return timeFormat.format(date);
    }

}
