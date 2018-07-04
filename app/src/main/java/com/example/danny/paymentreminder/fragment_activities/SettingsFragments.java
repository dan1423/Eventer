package com.example.danny.paymentreminder.fragment_activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.example.danny.paymentreminder.Custom_Classes.CSVExporter;
import com.example.danny.paymentreminder.Custom_Classes.CustomDateParser;
import com.example.danny.paymentreminder.adapter.CustomEventObject;
import com.example.danny.paymentreminder.sqllite.DBHandler;
import com.example.danny.paymentreminder.R;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class SettingsFragments extends Fragment {

    public SettingsFragments(){

    }

    private View settingsView;
    Button btnDeleteAllEvents, btnExportEvents ;
   // private static boolean isThereAnEmailSuggestion = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        settingsView = inflater.inflate(R.layout.layout_for_settings_fragment, null, false);
        btnDeleteAllEvents = (Button)settingsView.findViewById(R.id.btn_notification_settings);
        btnExportEvents  = (Button)settingsView.findViewById(R.id.btn_export_events);

        btnExportEvents.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showExportEmailDialog();
            }
        });

        btnDeleteAllEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDeleteConfirmationDialog();
            }
        });
        return settingsView;
    }

    private void showExportEmailDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_for_export_events, null);
       builder.setView(dialogView);
        final AlertDialog d = builder.show();
        d.setTitle("Enter email to export csv to");

        //set widgets in custom dialog
        final AutoCompleteTextView email = (AutoCompleteTextView) dialogView.findViewById(R.id.txt_email_address);
        Button btnDialogExport = (Button)dialogView.findViewById(R.id.btn_dialog_export);
        Button btnDialogCancel = (Button)dialogView.findViewById(R.id.btn_dialog_cancel);
        final TextView errorMessage = (TextView)dialogView.findViewById(R.id.txt_dialog_error_message);

        //get email suggestion

      /*  String emailSuggestionFromSharedPreferences = getRecentEmail();
        String suggestions[] = new String[1];


        if(!emailSuggestionFromSharedPreferences.trim().isEmpty()){
            isThereAnEmailSuggestion = true;
            suggestions[0] = emailSuggestionFromSharedPreferences;

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_dropdown_item_1line, suggestions);

            email.setAdapter(adapter);


        }

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isThereAnEmailSuggestion)
                     email.showDropDown();
            }
        });*/

        //user clicks export button
        btnDialogExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmailValid(email.getText().toString())){
                    errorMessage.setVisibility(View.VISIBLE);
                }else {
                    errorMessage.setVisibility(View.INVISIBLE);
                   // setEmailSuggestion(email.getText().toString());//set email to sharedpreferences
                    startExport(email.getText().toString());//export csv to email
                    d.dismiss();//dismiss dialog
                }
            }
        });

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
         builder.create();

    }

    private boolean isEmailValid(String email){
        if(email.trim().isEmpty()){
            return false;
        }
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);
        return matcher.matches();

    }

    private void startExport(String email){
        DBHandler dbHandler = new DBHandler(getContext(),null, null, 1);
        ArrayList<CustomEventObject> customEventObjects = dbHandler.queryAllEvents();
        String csv = buildCSV(customEventObjects);
        CSVExporter csvExporter = new CSVExporter(getContext(), csv,email);
        csvExporter.exportToEmail();
    }

    private String buildCSV(ArrayList<CustomEventObject> customEventObjects){
       StringBuilder builder = new StringBuilder("Event,Date,Time,Event-Type\n");
       CustomDateParser parser;

       for(int i = 0; i < customEventObjects.size(); i++){
           String eventName = customEventObjects.get(i).getEventName();

            parser = new  CustomDateParser(customEventObjects.get(i).getEventDate());
            parser.setDateAndTime();

            String eventDate = parser.getDate();

            String eventTime = parser.getTime();

            String eventType = customEventObjects.get(i).getEventType();

            builder.append(eventName +","+eventDate+","+eventTime+","+eventType+"\n");

       }

       return  builder.toString();

    }

    /*private void showDeleteConfirmationDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_delete_events_confirmation, null);
        builder.setView(dialogView);
        final AlertDialog d = builder.show();
        d.setTitle("DELETE ALL EVENTS?");


        Button btnYes = (Button)dialogView.findViewById(R.id.btnYes);
        Button btnNo = (Button)dialogView.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllEvents();
                d.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        builder.create();
    }

    private void deleteAllEvents(){
        DBHandler dbHandler = new DBHandler(getContext(), null,null,1);
        dbHandler.clearTable();
    }*/

   /* private String getRecentEmail(){
        SharedPreferences prefs = getContext().getSharedPreferences("emailSuggestion", MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);
        if (restoredText != null) {
            String email = prefs.getString("email", "");//"No name defined" is the default value.
           return email;
        }
        return "";
    }
    private void setEmailSuggestion(String email){
        SharedPreferences.Editor editor = getContext().getSharedPreferences("emailSuggestion", MODE_PRIVATE).edit();
        editor.putString("email", email);
        editor.apply();
    }*/
}
