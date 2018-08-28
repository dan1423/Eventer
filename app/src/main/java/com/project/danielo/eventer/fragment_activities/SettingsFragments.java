package com.project.danielo.eventer.fragment_activities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;

import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.project.danielo.eventer.Custom_Classes.AddAndEditMethods;
import com.project.danielo.eventer.Custom_Classes.CSVExporter;
import com.project.danielo.eventer.Custom_Classes.CustomDateParser;
import com.project.danielo.eventer.Custom_Classes.CustomRegex;
import com.project.danielo.eventer.Custom_Classes.EventsToCsvString;
import com.project.danielo.eventer.StaticVariables;
import com.project.danielo.eventer.adapter.CustomEventObject;
import com.project.danielo.eventer.dialog_fragments.GoogleDriveFragment;
import com.project.danielo.eventer.dialog_fragments.NotificationSettings;
import com.project.danielo.eventer.notification_package.CustomNotification;
import com.project.danielo.eventer.sqllite.DBHandler;
import com.project.danielo.eventer.R;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import static android.app.Activity.RESULT_OK;


public class SettingsFragments extends Fragment {

    public SettingsFragments(){

    }

    private View settingsView;
    Button btnShowNotificationSettings, btnExportEvents ,
            btnImportEvents, btnDeleteAllEvents, btnDriveSettings;
    ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        settingsView = inflater.inflate(R.layout.layout_for_settings_fragment, null, false);
        btnShowNotificationSettings = (Button)settingsView.findViewById(R.id.btn_notification_settings);
        btnExportEvents  = (Button)settingsView.findViewById(R.id.btn_export_events);
        btnImportEvents = (Button)settingsView.findViewById(R.id.btn_import_events);
        btnDeleteAllEvents = (Button)settingsView.findViewById(R.id.btn_delete_all_events);
        btnDriveSettings = (Button)settingsView.findViewById(R.id.btn_google_drive_settings);
        progressBar = (ProgressBar)settingsView.findViewById(R.id.settings_progress_bar);

        final DBHandler handler = new DBHandler(getContext(),null,null, StaticVariables.DATABASE_VERSION);
        final ArrayList<CustomEventObject> eventObjects = handler.queryAllEvents();

        btnExportEvents.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(eventObjects.isEmpty()){
                    openNothingToExportDialog();
                }else {
                    showExportEmailDialog();
                }
            }
        });



        btnDeleteAllEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        btnShowNotificationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openNotificationSettings();
            }
        });

        btnDriveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGoogleDriveSettings();
            }
        });
        return settingsView;
    }





    private void openNothingToExportDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Note");
        alertDialog.setMessage("There are no events to export");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }



/***********************END OF IMPORT EVENTS METHODS**************************/
    private void showExportEmailDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.layout_for_export_events_dialog, null);
       builder.setView(dialogView);
        final AlertDialog d = builder.show();
        d.setTitle("Enter email to export csv to");

        //set widgets in custom dialog
        final AutoCompleteTextView email = (AutoCompleteTextView) dialogView.findViewById(R.id.txt_email_address);
        Button btnDialogExport = (Button)dialogView.findViewById(R.id.btn_dialog_export);
        Button btnDialogCancel = (Button)dialogView.findViewById(R.id.btn_dialog_cancel);
        final TextView errorMessage = (TextView)dialogView.findViewById(R.id.txt_dialog_error_message);


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
        EventsToCsvString eventsToCsvString = new EventsToCsvString(customEventObjects);
        return eventsToCsvString.getEventsAsCsvString();

    }

    private void showDeleteConfirmationDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.layout_for_delete_confirmation_dialog, null);
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
        progressBar.setVisibility(View.VISIBLE);
        DBHandler dbHandler = new DBHandler(getContext(), null,null,1);

        ArrayList<CustomEventObject>objects = dbHandler.queryAllEvents();
        for(int i = 0; i < objects.size(); i++){
            CustomEventObject eventObject = objects.get(i);
            int id = eventObject.getEventId();
            dbHandler.deleteEvent(eventObject);

            deleteEventNotification(id);
        }
        dbHandler.clearTable();
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void deleteEventNotification(int id) {
        CustomNotification notification = new CustomNotification(getContext(), id);
        notification.removeNotification();

        int negativeId = id * -1;
        CustomNotification notification2 = new CustomNotification(getContext(), negativeId);
        notification2.removeNotification();
    }

    private void openNotificationSettings(){
       Fragment fragment = new NotificationSettings();
       FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
       FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
       fragmentTransaction .setCustomAnimations(R.anim.grow_from_center, R.anim.blank,
               R.anim.blank, R.anim.shrink_to_center);
       fragmentTransaction.replace(R.id.main_nav,fragment);
       fragmentTransaction.addToBackStack(null);


       fragmentTransaction.commit();
   }

   private void openGoogleDriveSettings(){
       Fragment fragment = new GoogleDriveFragment();
       FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
       FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
       fragmentTransaction .setCustomAnimations(R.anim.slide_in_left, R.anim.blank,
               R.anim.blank, R.anim.slide_out_right);
       fragmentTransaction.replace(R.id.main_nav,fragment);
       fragmentTransaction.addToBackStack(null);


       fragmentTransaction.commit();
   }


}
