package com.example.danny.paymentreminder;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import static android.content.Context.LOCATION_SERVICE;

public class CustomLocationManager implements LocationListener {

    LocationManager lm;
    Location location;
    Context ctx;

    public CustomLocationManager(Context ctx){
        this.ctx = ctx;
    }

    public long currentTime(){
        Location l = getLocation();
        if(l !=null){
            Log.i("LocationWorked","location");
            return location.getTime();

        }

        return System.currentTimeMillis();
    }

    private Location getLocation(){
        try{
            lm = (LocationManager)ctx.getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_LOW);
            // String provider = lm.getBestProvider(criteria, true);

            location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            // Toast.makeText(ctx,"LOCATION :"+location,Toast.LENGTH_SHORT).show();

            if (location == null) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            } else {
                onLocationChanged(location);
               return location;
            }

        }catch (SecurityException e){
            Log.d("SECURITY EXCEPTION: ",e.getMessage());
        }
        return  null;
    }


    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}

