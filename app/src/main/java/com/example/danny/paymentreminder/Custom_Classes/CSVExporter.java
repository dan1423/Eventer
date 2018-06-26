package com.example.danny.paymentreminder.Custom_Classes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/*The purpose of this class is to send email with csv attachment, generated from all events

 */
public class CSVExporter {
    private String csvDataString;
    private String email;
    Context context;

    //prepared CSV data is initialized in constructor
    public CSVExporter(Context context, String csvDataString, String email){
        this.context = context;
        this.csvDataString = csvDataString;
        this.email = email;
    }

    public boolean exportToEmail(){
        File f = writeStringCSVToFile(csvDataString);
        if(f !=null){
            sendEmail(f);
            return true;
        }
        return false;
    }
    //to save csv to phone storage
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
        context.startActivity(Intent.createChooser(emailIntent , "Exporting to email"));


    }

}
