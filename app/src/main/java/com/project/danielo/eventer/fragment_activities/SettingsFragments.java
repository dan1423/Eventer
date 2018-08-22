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
import com.project.danielo.eventer.StaticVariables;
import com.project.danielo.eventer.adapter.CustomEventObject;
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
    GoogleApiClient apiClient;
    private static final String TAG = "Google drive activity";
    private static  final int  REQUEST_CODE_OPENER = 15;
    private static final  int REQUEST_CODE_SIGN_IN = 16;
    private  static final int REQUEST_CODE_OPEN_ITEM = 1;


    private DriveId driveId;
    private DriveClient driveClient;
    private OpenFileActivityOptions openFileActivityOptions;
    private DriveResourceClient resourceClient;
    private TaskCompletionSource<DriveId> mOpenItemTaskSource;
    private DriveContents driveContents;
    private Metadata metadata;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signIn();
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

        btnImportEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnectedToTheInternet()) {
                    openPleaseConnectToInternet();

                }else{
                   if(!isUserSignedInToGoogleDriveAccount()){
                       openSignInGoogleDriveAccountDialog();
                   }else{

                       openFileChooser();

                   }

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
                if (!isConnectedToTheInternet()) {
                    openPleaseConnectToInternet();

                }else{
                    progressBar.setVisibility(View.VISIBLE);

                        /*
                        /user is signed in, so we must initialize sign in client and sign out to reopen Google Drive Account chooser
                         */
                            GoogleSignInOptions signInOptions =
                                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .requestScopes(Drive.SCOPE_FILE)
                                            .requestScopes(Drive.SCOPE_APPFOLDER)
                                            .build();
                    GoogleSignInClient signInClient = GoogleSignIn.getClient(getContext(),signInOptions);
                            signInClient.signOut();
                            signIn();

                }

            }
        });
        return settingsView;
    }

    /***********************START OF IMPORT EVENTS METHODS**************************/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        progressBar.setVisibility(View.INVISIBLE);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != getActivity().RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
                    // required and is fatal. For apps where sign-in is optional, handle
                    // appropriately
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());
                } else {

                }
                break;
            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                   driveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                   loadCurrentFile();
                }

                break;


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Google drive sign in
    private void signIn(){

        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());

       if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        } else {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .build();
           GoogleSignInClient  signInClient = GoogleSignIn.getClient(getContext(), signInOptions);
            startActivityForResult(signInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);

        }
    }
    //list files in drive
    private void openFileChooser(){
        progressBar.setVisibility(View.VISIBLE);
        OpenFileActivityOptions openOptions =
                new OpenFileActivityOptions.Builder()
                       .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "text/csv"))
                      //  .setMimeType(mimeTypes)
                        .setActivityTitle("Choose a CSV file")
                        .build();

        driveClient.newOpenFileActivityIntentSender(openOptions)
                .addOnSuccessListener(new OnSuccessListener<IntentSender>() {
                    @Override
                    public void onSuccess(IntentSender intentSender) {

                        try {
                            startIntentSenderForResult(
                                    intentSender,
                                    REQUEST_CODE_OPENER,
                                    /* fillInIntent= */ null,
                                    /* flagsMask= */ 0,
                                    /* flagsValues= */ 0,
                                    /* extraFlags= */ 0,
                                    null);
                            ;
                        } catch (IntentSender.SendIntentException e) {
                            Log.w(TAG, "Unable to send intent.", e);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Unable to create OpenFileActivityIntent.", e);
            }
        });

    }


    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        //Log.i("DEBUG MODE","in initialize client");
        driveClient = Drive.getDriveClient(getContext(), signInAccount);
        resourceClient = Drive.getDriveResourceClient(getContext(), signInAccount);
        if(progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Retrieves the currently selected Drive file's metadata and contents.
     */
    private void loadCurrentFile() {
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Retrieving...");
        final DriveFile file = driveId.asDriveFile();

        // Retrieve and store the file metadata and contents.
        resourceClient.getMetadata(file)
                .continueWithTask(new Continuation<Metadata, Task<DriveContents>>() {
                    @Override
                    public Task<DriveContents> then(@NonNull Task<Metadata> task) {
                        if (task.isSuccessful()) {
                            metadata = task.getResult();
                            return resourceClient.openFile(file, DriveFile.MODE_READ_ONLY);
                        } else {
                            return Tasks.forException(task.getException());
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<DriveContents>() {
            @Override
            public void onSuccess(DriveContents contents) {
                driveContents = contents;
                refreshUiFromCurrentFile();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Unable to retrieve file metadata and contents.", e);
            }
        });
    }


    //converting inputstream to string
    private void refreshUiFromCurrentFile() {
        Log.d(TAG, "Refreshing...");
        String contents = "";

        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(driveContents.getInputStream(), writer);
             contents = writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(contents.trim().isEmpty()){
            return;
        }

        ArrayList<String> eventList = stringToArray(contents);
        ArrayList<CustomEventObject> objects = arrayListOfEventStringsToArrayListOfEventObjects(eventList);
        addEventsToApplication(objects);
    }

    //convert string contents to array of event strings
    private ArrayList<String> stringToArray(String eventsasCSVString){
       eventsasCSVString =  eventsasCSVString.replace(",","|");
        String[] lines = eventsasCSVString.split("\\r?\\n");
        Log.i("Array of Event string",Arrays.toString(lines));
        ArrayList<String> eventList = new ArrayList<>();
       for(int i = 0; i < lines.length; i++){
           String s = lines[i].replace("|",",");
            if(validateEventString(s)){

                eventList.add(s);
            }
        }
        return eventList;
    }


    private boolean validateEventString(String eventString){
        String[] lines = eventString.split(",");
        if(lines.length < 6){
            return false;
        }
        CustomRegex customRegex = new CustomRegex();


        if(!customRegex.validateEventName(lines[0])||
            !customRegex.validateEvenDate(lines[1])||
            !customRegex.validateHour(lines[2])||
            !customRegex.validateMinute(lines[3])||
            !customRegex.validatePeriod(lines[4])||
            !customRegex.validateEventType(lines[5])){
            return false;
        }
        return true;

    }

    private ArrayList<CustomEventObject>
            arrayListOfEventStringsToArrayListOfEventObjects(ArrayList<String>eventList){

        ArrayList<CustomEventObject> objects = new ArrayList<>();

        for(int i = 0; i < eventList.size(); i++){
            String[] eventArray = eventList.get(i).split(",");
            objects.add(getEventObjectFromStringArray(eventArray));
        }

        return objects;

    }

    private void addEventsToApplication(ArrayList<CustomEventObject> eventObjects){
        DBHandler handler = new DBHandler(getContext(),null,null,StaticVariables.DATABASE_VERSION);
        AddAndEditMethods methods = new AddAndEditMethods(getContext(),handler);

        for(int i = 0; i < eventObjects.size(); i++){
            long id = methods.addNewEventToDatabase(eventObjects.get(i));
            if(isNotificationOn()){
                methods.setupNotification(eventObjects.get(i),id);
            }
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    private boolean isNotificationOn(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isOn = preferences.getBoolean("notifications", true);
        return isOn;
    }


    private CustomEventObject getEventObjectFromStringArray(String[] eventStringArray){

         AddAndEditMethods methods = new AddAndEditMethods();

        String eventName = eventStringArray[0];

        //format date
        String eventDateString = eventStringArray[1];
        eventDateString = convertDateFormat(eventDateString);
        Date eventDate =  methods.getDateFromString(eventDateString);

        String timeString = eventStringArray[2].trim() + ":"+eventStringArray[3].trim()+" "+ eventStringArray[4].trim();
        Date time = methods.getTimeFromString(timeString);

        Date eventTime = methods.mergeDateAndTime(eventDate,time);


        String type = eventStringArray[5];

        CustomEventObject eventObject = new CustomEventObject(eventName,eventTime.getTime(),type);

        return eventObject;
    }

    private String convertDateFormat(String date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String newDateFormat = "";
        try {
            Date dateParser =sdf.parse(date);
            sdf = new SimpleDateFormat("MM-dd-yyyy");
           newDateFormat =   sdf.format(dateParser);

        }catch (ParseException e){
            e.printStackTrace();
        }

        return newDateFormat;
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



   private boolean isConnectedToTheInternet(){
       ConnectivityManager cm =
               (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
       boolean isConnected = false;
       try{
           NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                   activeNetwork.isConnectedOrConnecting();
       }catch (SecurityException  e){
           e.printStackTrace();
       }
        return isConnected;

   }


    private boolean isUserSignedInToGoogleDriveAccount(){
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        if(signInAccount == null){
            return false;
        }
        return true;

    }

    private void openSignInGoogleDriveAccountDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("No google account selected");
        alertDialog.setMessage("Please sign in to Google Drive Account by pressing Google Drive Settings button");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void openPleaseConnectToInternet(){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("!Internet Connection needed");
        alertDialog.setMessage("Please Connect to the internet");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
