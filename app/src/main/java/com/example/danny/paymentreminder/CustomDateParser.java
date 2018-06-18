package com.example.danny.paymentreminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateParser {
    private long longDate;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");

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

    public String convertLongToDate(){
        Date d = new Date(this.longDate);
        Date date = new Date();


        return  simpleDateFormat.format(d);
    }

}
