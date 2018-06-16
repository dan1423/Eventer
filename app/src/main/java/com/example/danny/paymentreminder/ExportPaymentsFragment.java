package com.example.danny.paymentreminder;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;


public class ExportPaymentsFragment extends Fragment {

    public ExportPaymentsFragment(){

    }
    Button btnExport;
    EditText txtEmailAddress;
    TextView txtErrorMessage;

    private View exportView;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        exportView = inflater.inflate(R.layout.layout_for_export_events,null,false);
        btnExport = (Button)exportView.findViewById(R.id.btn_export_data);
        txtEmailAddress = (EditText)exportView.findViewById(R.id.txt_email_address);
        txtErrorMessage = (TextView) exportView.findViewById(R.id.txt_error_message_export);

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(!isValidEmail(txtEmailAddress.getText().toString())){
                    txtErrorMessage.setVisibility(View.VISIBLE);
                   beginProecdure();
               }else{
                    txtErrorMessage.setVisibility(View.INVISIBLE);
                    beginProecdure();
               }
            }
        });

        return exportView;
    }

    private void beginProecdure(){
        StringBuilder builder = getAllPaymentsAsCSV();
       File file =  writeStringCSVToFile(builder.toString());
       if(file !=null){
           sendEmail(file);
       }

    }

    private StringBuilder getAllPaymentsAsCSV(){
        DBHandler dbHandler = new DBHandler(getContext(),null,null,1);
        ArrayList<EventObject>eventObjects = dbHandler.queryAllEvents();

       return convertArrayList(eventObjects);

    }

    private StringBuilder convertArrayList (ArrayList<EventObject> eventObjects){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < eventObjects.size(); i++){
            EventObject e = eventObjects.get(i);
            String s = e.getEventName() +","+e.getEventDate()+","+e.getEventType()+"\n";
            builder.append(s);
        }

        return builder;
    }

    private boolean isValidEmail(String email){
        if(email.trim().isEmpty()){
            return false;
        }
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);
        return matcher.matches();

    }

    //to convert string to csv format
    private File writeStringCSVToFile(String str){
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(dir, "event_dates.csv");

        try{

            FileUtils.writeStringToFile(file,str);
            return file;
        }catch(IOException e){
            e.printStackTrace();
        }
       return null;
    }


    //to send email
    private void sendEmail(File file){

            Uri path = Uri.fromFile(file);

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            // set the type to 'email'
           emailIntent.setType("text/html");
            String to[] = {"oluwadare.daniel21@gmail.com"};
            emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
            // the attachment
            emailIntent .putExtra(Intent.EXTRA_STREAM, path);
            // the mail subject
            emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Event dates");
            startActivity(Intent.createChooser(emailIntent , "Send email..."));


    }



}
