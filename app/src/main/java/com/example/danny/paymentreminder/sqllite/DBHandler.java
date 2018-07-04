package com.example.danny.paymentreminder.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.danny.paymentreminder.adapter.CustomEventObject;

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
    public long addEvent(CustomEventObject customEventObject){
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENTNAME, customEventObject.getEventName());
        values.put(COLUMN_EVENTTYPE, customEventObject.getEventType()+"");
        values.put(COLUMN_EVENTDATE, customEventObject.getEventDate()+"");
        SQLiteDatabase db = getWritableDatabase();
       long insertedId =  db.insert(TABLE_EVENTS, null, values);
        db.close();

        return insertedId;
    }

    public void addMultipleEvents(ArrayList<CustomEventObject> customEventObjects){
        if(customEventObjects.isEmpty()){
            return;
        }
        String eventName = customEventObjects.get(0).getEventName();
        String eventType = customEventObjects.get(0).getEventType();
        String eventDate = customEventObjects.get(0).getEventDate()+"";
       String queryString =  "INSERT INTO "+ TABLE_EVENTS+"(event_name,event_type,event_date)"+
                            " SELECT '"+eventName+ "' AS "+ COLUMN_EVENTNAME+ ", '"+
                            eventType + "' AS "+ COLUMN_EVENTTYPE+ ", '"+
                            eventDate + "' AS " + COLUMN_EVENTDATE;

        for(int i = 1; i < customEventObjects.size(); i++){
            eventName = customEventObjects.get(i).getEventName();
            eventType = customEventObjects.get(i).getEventType();
            eventDate = customEventObjects.get(i).getEventDate()+"";

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

    //we will get rows from database and convert to CustomEventObject to save to arraylist
    public ArrayList<CustomEventObject> queryAllEvents(){

        ArrayList<CustomEventObject> customEventObjects = new ArrayList<>();
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

                CustomEventObject customEventObject = new CustomEventObject(
                        recordSet.getString(recordSet.getColumnIndex("event_name")),
                        Long.parseLong(recordSet.getString(recordSet.getColumnIndex("event_date"))),//convert string to long
                        recordSet.getString(recordSet.getColumnIndex("event_type")) );

                customEventObject.setEventId(Integer.parseInt(recordSet.getString(recordSet.getColumnIndex("event_id"))));

                customEventObjects.add(customEventObject);
            }
            recordSet.moveToNext();
        }
        db.close();

        return customEventObjects;

    }

    public void updateEvent(CustomEventObject customEventObject){
        String query = "UPDATE "+ TABLE_EVENTS +
        " SET event_name = '" + customEventObject.getEventName() +"', "
                +"event_type = '"+ customEventObject.getEventType()+"', "
                +"event_date = '"+ customEventObject.getEventDate()+
        "' WHERE event_id =  " + customEventObject.getEventId();

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
    }

    public void deleteEvent(CustomEventObject customEventObject){
        String query = "DELETE FROM "+TABLE_EVENTS +" WHERE event_id = "+ customEventObject.getEventId();
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);

    }

    public void clearTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_EVENTS);
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME = '" + TABLE_EVENTS +"'");

    }




}


