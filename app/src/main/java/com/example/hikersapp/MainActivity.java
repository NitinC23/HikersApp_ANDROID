package com.example.hikersapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }
    public void startListening(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        }
    }
    public void updateLocationInfo(Location location){
        TextView latitudeTextView = (TextView)findViewById(R.id.latitudeTextView);
        TextView longitudeTextView = (TextView)findViewById(R.id.longitudeTextView);
        TextView accuracyTextView = (TextView)findViewById(R.id.accuracyTextView);
        TextView altitudeTextView = (TextView)findViewById(R.id.altutudeTextView);
        latitudeTextView.setText("Latitude : "+Math.round(location.getLatitude() * 100.0) / 100.0);
        longitudeTextView.setText("Longitude : "+Math.round(location.getLongitude() * 100.0) / 100.0);
        altitudeTextView.setText("Altitude : "+location.getAltitude());
        accuracyTextView.setText("Accuracy : "+location.getAccuracy());
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            String address = "Could Not Find The Address !";
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if(listAddresses != null && listAddresses.size() > 0){
                address = "Address : ";
               if(listAddresses.get(0).getSubThoroughfare() != null){
                   address = address + listAddresses.get(0).getSubThoroughfare()+" ";
               }
               if(listAddresses.get(0).getThoroughfare() != null){
                   address = address + listAddresses.get(0).getThoroughfare()+"\n";
               }
               if(listAddresses.get(0).getLocality() != null){
                    address = address + listAddresses.get(0).getLocality()+"\n";
               }
               if(listAddresses.get(0).getPostalCode() != null){
                    address = address + listAddresses.get(0).getPostalCode()+"\n";
               }
               if(listAddresses.get(0).getCountryName() != null){
                    address = address + listAddresses.get(0).getCountryName()+"\n";
               }
            }
            TextView addressTextView = (TextView)findViewById(R.id.adressTextView);
            addressTextView.setText(address);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
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
        };
        if(Build.VERSION.SDK_INT < 23){
            startListening();
        }else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastKnownLocation != null){
                    updateLocationInfo(lastKnownLocation);
                }
            }
        }
    }
}
