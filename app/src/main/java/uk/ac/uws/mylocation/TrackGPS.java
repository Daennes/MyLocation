package uk.ac.uws.mylocation;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by dennisalt on 07/11/2017.
 */

public class TrackGPS extends Service implements LocationListener {

    private final Context ctxt;
    boolean checkGPS = false;
    boolean checkNetwork = false;
    boolean canGetLocation = false;

    Location loc;
    protected LocationManager locationManager;
    double longitude;
    double latitude;

    private static final long MINDISTANCE = 10;
    private static final long MINDELAY = 1000 * 6;

    public TrackGPS(Context ctxt){
        this.ctxt = ctxt;
        getLocation();
    }

    public double getLatitude(){
        if (loc != null)
            return loc.getLatitude();
        return latitude;
    }

    public double getLongitude(){
        if (loc != null)
            return loc.getLongitude();
        return longitude;
    }

    public boolean canGetLocation(){
        return this.canGetLocation;
    }

    private Location getLocation(){
        try {
            locationManager = (LocationManager) ctxt.getSystemService(LOCATION_SERVICE);
            checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!checkNetwork && !checkGPS){
                Toast.makeText(ctxt, "No Service provider available", Toast.LENGTH_SHORT).show();
            }
            else{
                canGetLocation = true;

                String provider = checkNetwork ? LocationManager.NETWORK_PROVIDER:LocationManager.GPS_PROVIDER;

                try {
                    locationManager.requestLocationUpdates(
                            provider,
                            MINDELAY,
                            MINDISTANCE,
                            this
                    );

                    if (locationManager != null){
                        loc = locationManager.getLastKnownLocation(provider);
                        if (loc != null){
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                        }
                    }
                }
                catch (SecurityException e){
                    Toast.makeText(ctxt, "No permission to access provider", Toast.LENGTH_SHORT).show();
                }

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return loc;

    }

    public void showAlert(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctxt);
        dialog.setTitle("GPS not enabled");
        dialog.setMessage("Do you want to turn on GPS?");


        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                ctxt.startActivity(intent);
            }
        });

        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog.show();

    }


    public void stopGPS(){
        if (locationManager != null){
            try {
                locationManager.removeUpdates(TrackGPS.this);
            }
            catch (SecurityException e){
                Toast.makeText(ctxt, "No permission to access GPS", Toast.LENGTH_LONG).show();
            }
        }
    }






    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
