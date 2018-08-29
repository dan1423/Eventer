package com.project.danielo.eventer.dialog_fragments;

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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
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
import com.project.danielo.eventer.Custom_Classes.CustomRegex;
import com.project.danielo.eventer.Custom_Classes.EventsToCsvString;
import com.project.danielo.eventer.R;
import com.project.danielo.eventer.StaticVariables;
import com.project.danielo.eventer.adapter.CustomEventObject;
import com.project.danielo.eventer.sqllite.DBHandler;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


import static android.app.Activity.RESULT_OK;

public class GoogleDriveFragment extends Fragment{

    private View googleDriveFragmentView;
    private Button  btnChooseAccount,btnImportEvents,btnExportEvents,btnGetEventTemplates;
    ImageView imgArrowBackoSettings;
    ProgressBar progressBar;

    private static final String TAG = "Google drive activity";
    private static  final int  REQUEST_CODE_OPENER = 15;
    private static final  int REQUEST_CODE_SIGN_IN = 16;

    private DriveId driveId;
    private DriveClient driveClient;
    private OpenFileActivityOptions openFileActivityOptions;
    private DriveResourceClient resourceClient;
    private TaskCompletionSource<DriveId> mOpenItemTaskSource;
    private DriveContents driveContents;
    private Metadata metadata;
    private String gmailOfUser = "";


    public GoogleDriveFragment(){

    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signIn();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        googleDriveFragmentView = inflater.inflate(R.layout.layout_for_google_drive_settings,null,false);
        btnChooseAccount = (Button)googleDriveFragmentView.findViewById(R.id.btn_choose_account);
        btnImportEvents = (Button)googleDriveFragmentView.findViewById(R.id.btn_import_events);
        btnExportEvents = (Button)googleDriveFragmentView.findViewById(R.id.btn_export_events);
        btnGetEventTemplates = (Button)googleDriveFragmentView.findViewById(R.id.btn_get_event_template);
        progressBar = (ProgressBar)googleDriveFragmentView.findViewById(R.id.progressBar_google_drive_settings);
        imgArrowBackoSettings = (ImageView)googleDriveFragmentView.findViewById(R.id.img_back_to_Settings);

        initializeButtonEvents();

        return googleDriveFragmentView;
    }

    private void initializeButtonEvents(){

       btnChooseAccount.setOnClickListener(new View.OnClickListener(){
           public void onClick(View v){

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

        btnImportEvents.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

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

        btnExportEvents.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                if (!isConnectedToTheInternet()) {
                    openPleaseConnectToInternet();

                }else if(getEventsFromDatabase().isEmpty()){
                    openNothingToExportDialog();
                }

                else{
                    if(!isUserSignedInToGoogleDriveAccount()){
                        openSignInGoogleDriveAccountDialog();
                    }else{
                        String s = convertEventsToCsvString(getEventsFromDatabase());
                        uploadFile(s);
                    }

                }

            }
        });

        btnGetEventTemplates.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                sendEventerTemplateToEmail();
            }
        });

        imgArrowBackoSettings.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                backToSettings();
            }
        });
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
                            .requestEmail()
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .build();
            GoogleSignInClient signInClient = GoogleSignIn.getClient(getContext(), signInOptions);
            startActivityForResult(signInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);

        }
    }

    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        gmailOfUser = signInAccount.getEmail();
        driveClient = Drive.getDriveClient(getContext(), signInAccount);
        Log.i("GoogleDriveAPI",signInAccount.toJson());

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

    //upload events to Google Drive Account
    private void uploadFile(String eventsAsString){
        progressBar.setVisibility(View.VISIBLE);

        final Task<DriveFolder> rootFolderTask = resourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = resourceClient.createContents();
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = rootFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();
                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                        writer.write(eventsAsString);
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("Events from Eventer")
                            .setMimeType("text/csv")
                            .setStarred(true)
                            .build();

                    return resourceClient.createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(getActivity(),
                        driveFile -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(),"Successfuly imported events as CSV to Drive account", Toast.LENGTH_LONG).show();
                        })
                .addOnFailureListener(getActivity(), e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(),"Failed to export events", Toast.LENGTH_LONG).show();
                });
    }





    //converting InputStream to string
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
        Log.i("Array of Event string", Arrays.toString(lines));
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
        DBHandler handler = new DBHandler(getContext(),null,null, StaticVariables.DATABASE_VERSION);
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

        CustomEventObject eventObject = new CustomEventObject(eventName,eventTime.getTime(),type,"");

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

    private void backToSettings(){
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
    }

    private ArrayList<CustomEventObject> getEventsFromDatabase(){
        DBHandler dbHandler = new DBHandler(getContext(),null,null,StaticVariables.DATABASE_VERSION);
        ArrayList<CustomEventObject> eventObjects = dbHandler.queryAllEvents();
        return eventObjects;
    }

    private String convertEventsToCsvString(ArrayList<CustomEventObject> eventObjects){
        EventsToCsvString eventsToCsvString = new EventsToCsvString(eventObjects);
        return eventsToCsvString.getEventsAsCsvString();
    }

    private  void sendEventerTemplateToEmail(){
            if(!isUserSignedInToGoogleDriveAccount()){
                openSignInGoogleDriveAccountDialog();
                return;
            }

            if (!isConnectedToTheInternet()){
                openPleaseConnectToInternet();
                return;
            }
            sendEmail(gmailOfUser);

    }

   private void sendEmail(String gmailOfUser){
       BackgroundMail.newBuilder(getContext())
               .withUsername("eventer1423@gmail.com")
               .withPassword("retneve123")
               .withMailto(gmailOfUser)
               .withType(BackgroundMail.TYPE_PLAIN)
               .withSubject("Eventer template link")
               .withBody("https://danieloluwadare.com/eventer/")
               .withSendingMessage("Sending download link")
               .withSendingMessageError("Unsuccessful, please try again later")
               .withSendingMessageSuccess("Eventer download Link successfully sent to email")


               .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                   @Override
                   public void onSuccess() {

                   }
               })
               .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                   @Override
                   public void onFail() {
                   }
               })
               .send();

    }





}
