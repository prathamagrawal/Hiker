package com.example.hiker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }

    public void startListening()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }


    @SuppressLint("SetTextI18n")
    public void updateLocationInfo(Location location)
    {
        Log.i("Location Info: ",location.toString());
        TextView latitude = (TextView)findViewById(R.id.Latitude);
        TextView longitude = (TextView)findViewById(R.id.Longitude);
        TextView Altitude = (TextView)findViewById(R.id.Altitude);
        TextView Accuracy = (TextView)findViewById(R.id.Accuracy);

        latitude.setText("Latitude: "+location.getLatitude());
        longitude.setText("Longitude: "+location.getLongitude());
        Altitude.setText("Altitude: "+location.getAltitude());
        Accuracy.setText("Accuracy: "+location.getAccuracy());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            String Address = "Could Not Find Address";
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (listAddresses != null && listAddresses.size() > 0) {
                Log.i("Place Info", listAddresses.get(0).toString());
                Address = "";
                if (listAddresses.get(0).getSubThoroughfare() != null) {
                    Address += listAddresses.get(0).getSubThoroughfare() + " ";
                }
                if (listAddresses.get(0).getThoroughfare() != null) {
                    Address += listAddresses.get(0).getThoroughfare() + "\n";
                }
                if (listAddresses.get(0).getLocality() != null) {
                    Address += listAddresses.get(0).getLocality() + "\n";
                }
                if (listAddresses.get(0).getPostalCode() != null) {
                    Address += listAddresses.get(0).getPostalCode() + "\n";
                }
                if (listAddresses.get(0).getCountryName() != null) {
                    Address += listAddresses.get(0).getCountryName() + "\n";
                }
            }
            TextView addressTextView = (TextView) findViewById(R.id.Address);
            addressTextView.setText("Address: \n" + Address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager =(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener= new LocationListener(){
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }
        };
        if (Build.VERSION.SDK_INT<23)
        {
            startListening();
        }else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateLocationInfo(location);
            }
        }
    }
}