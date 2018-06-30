package com.example.danny.paymentreminder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.danny.paymentreminder.fragment_activities.EventListFragment;
import com.example.danny.paymentreminder.fragment_activities.FAQFragment;
import com.example.danny.paymentreminder.fragment_activities.SettingsFragments;
import com.example.danny.paymentreminder.fragment_activities.UpcomingEventsFragment;
import com.example.danny.paymentreminder.sqllite.DBHandler;
import com.example.danny.paymentreminder.sqllite.UpdateDatabase;

public class MainActivity extends AppCompatActivity {

    DBHandler dbHandler;
    UpdateDatabase updateDatabase;
    private int STORAGE_PERMISSION_CODE = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {



        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            Fragment fragment = null;
            String title = "";

            if (id == R.id.events) {
                fragment = new EventListFragment();
                title = "Events";
            }else if (id == R.id.upcoming) {
                fragment = new UpcomingEventsFragment();
                title = "Upcoming Events";

            } else if (id == R.id.settings) {
                fragment = new SettingsFragments();
                title = "Settings";
            } else if (id == R.id.faqs) {
                fragment = new FAQFragment();
                title = "Frequently Asked Questions";
            }

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_left,R.anim.blank);
            ft.replace(R.id.main_layout_screen_bottom_nav,fragment);
            ft.commit();
            return true;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resolvePermission();
        dbHandler = new DBHandler(getApplicationContext(),null,null, StaticVariables.VERSION);
        updateDatabase = new UpdateDatabase(getApplicationContext());
        updateDatabase.updateAllEvents();


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_layout_screen_bottom_nav, new EventListFragment()).commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void resolvePermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to access csv files")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE );
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE )  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
