package com.tondz.googlemapservice.TheoDoi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DBContext implements LocationListener {
    Context context;
    public DatabaseReference reference;
    public FirebaseDatabase database;
    private FusedLocationProviderClient locationProviderClient;
    private Geocoder geocoder;
    private List<Address> addresses;
    Activity activity;

    public DBContext(Context context) {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Location");
        this.context = context;
        locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        geocoder = new Geocoder(context, Locale.getDefault());
        random = new Random();
        this.activity = activity;
    }

    double latitude, longitude;
    Random random;

    public void updateLocation() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(addresses.size()>0){
                String addressLine1 = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String fullAddress = addressLine1 + ",  " + city + ",  " + state + ",  ";
                String latLng = "Lat: " + latitude + "\nLong: " + longitude;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    reference.child("LC"+random.nextInt()).setValue(dtf.format(now)+"\n"+latLng + "\n" + fullAddress);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected LocationManager locationManager;
    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
}
