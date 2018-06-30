package com.example.danny.paymentreminder.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.danny.paymentreminder.adapter.EventObject;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION =  1;
    private static final String DATABASE_NAME = "events.db";
    private static final String TABLE_EVENTS = "events";
    private static final String COLUMN_ID = "event_id";
    private static final String COLUMN_EVENTNAME = "event_name";
    private static final String COLUMN_EVENTTYPE = "event_type";
    private static final String COLUMN_EVENTDATE = "event_date";




    //We need to pass database information along to superclass
    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_EVENTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EVENTNAME + " TEXT, " + COLUMN_EVENTTYPE +" TEXT, "+
                COLUMN_EVENTDATE+" TEXT "+
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    //Add a new row to the database
    public void addEvent(EventObject eventObject){
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENTNAME, eventObject.getEventName());
        values.put(COLUMN_EVENTTYPE, eventObject.getEventType()+"");
        values.put(COLUMN_EVENTDATE, eventObject.getEventDate()+"");
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_EVENTS, null, values);
        db.close();
    }

    public void addMultipleEvents(ArrayList<EventObject> eventObjects){
        if(eventObjects.isEmpty()){
            return;
        }
        String eventName = eventObjects.get(0).getEventName();
        String eventType = eventObjects.get(0).getEventType();
        String eventDate = eventObjects.get(0).getEventDate()+"";
       String queryString =  "INSERT INTO "+ TABLE_EVENTS+"(event_name,event_type,event_date)"+
                            " SELECT '"+eventName+ "' AS "+ COLUMN_EVENTNAME+ ", '"+
                            eventType + "' AS "+ COLUMN_EVENTTYPE+ ", '"+
                            eventDate + "' AS " + COLUMN_EVENTDATE;

        for(int i = 1; i < eventObjects.size(); i++){
            eventName = eventObjects.get(i).getEventName();
            eventType = eventObjects.get(i).getEventType();
            eventDate = eventObjects.get(i).getEventDate()+"";

           queryString +=   " UNION ALL SELECT '"+ eventName +"','"+eventType+"','"+eventDate +"'";


        }
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(queryString);
    }

    //Delete a product from the database
    public void deleteEvent(String eventName){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_EVENTS + " WHERE " + COLUMN_EVENTNAME + "=\"" + eventName + "\";");
    }

    // this is goint in record_TextView in the Main activity.
    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTS ;// why not leave out the WHERE  clause?

        //Cursor points to a location in your results
        Cursor recordSet = db.rawQuery(query, null);
        //Move to the first row in your results
        recordSet.moveToFirst();

        //Position after the last row means the end of the results
        while (!recordSet.isAfterLast()) {
            // null could happen if we used our empty constructor
            if (recordSet.getString(recordSet.getColumnIndex("event_name")) != null) {
                dbString += recordSet.getString(recordSet.getColumnIndex("event_name"));
                dbString += "\n";
            }
            recordSet.moveToNext();
        }
        db.close();
        return dbString;
    }

    //we will get rows from database and convert to EventObject to save to arraylist
    public ArrayList<EventObject> queryAllEvents(){

        ArrayList<EventObject> eventObjects = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTS ;// why not leave out the WHERE  clause?

        //Cursor points to a location in your results
        Cursor recordSet = db.rawQuery(query, null);
        //Move to the first row in your results
        recordSet.moveToFirst();

        //Position after the last row means the end of the results
        while (!recordSet.isAfterLast()) {
            // null could happen if we used our empty constructor
            if (recordSet.getString(recordSet.getColumnIndex("event_name")) != null) {

                EventObject eventObject = new EventObject(
                        recordSet.getString(recordSet.getColumnIndex("event_name")),
                        Long.parseLong(recordSet.getString(recordSet.getColumnIndex("event_date"))),//convert string to long
                        recordSet.getString(recordSet.getColumnIndex("event_type")) );

                eventObject.setEventId(Integer.parseInt(recordSet.getString(recordSet.getColumnIndex("event_id"))));

                eventObjects.add(eventObject);
            }
            recordSet.moveToNext();
        }
        db.close();

        return eventObjects;

    }

    public void updateEvent(EventObject eventObject){
        String query = "UPDATE "+ TABLE_EVENTS +
        " SET event_name = '" +eventObject.getEventName() +"', "
                +"event_type = '"+eventObject.getEventType()+"', "
                +"event_date = '"+eventObject.getEventDate()+
        "' WHERE event_id =  " + eventObject.getEventId();

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
    }

    public void deleteEvent(EventObject eventObject){
        String query = "DELETE FROM "+TABLE_EVENTS +" WHERE event_id = "+eventObject.getEventId();
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);

    }

    public void clearTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_EVENTS);
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME = '" + TABLE_EVENTS +"'");

    }


}

