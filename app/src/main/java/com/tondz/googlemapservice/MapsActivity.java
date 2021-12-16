package com.tondz.googlemapservice;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initView();
        setSpinner();
        setOnClick();
    }








    private Geocoder geocoder;
    private List<Address> addresses;
    private void setDialog(){
        geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        Dialog dialog = new Dialog(MapsActivity.this);
        dialog.setContentView(R.layout.dialog_goto);
        EditText edt_lat = dialog.findViewById(R.id.edt_lat);
        EditText edt_long = dialog.findViewById(R.id.edt_long);
        Button btn_goto = dialog.findViewById(R.id.btn_goto);
        btn_goto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double lat = Double.parseDouble(edt_lat.getText().toString());
                double lng = Double.parseDouble(edt_long.getText().toString());
                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1);
                    String addressLine1 = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String pinCode = addresses.get(0).getPostalCode();
                    String fullAddress = addressLine1 + ",  " + city + ",  " + state + ",  " + pinCode;
                    LatLng latLng = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)//vị trí
                            .title(fullAddress)//tiêu đề
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.push_location)));//icon
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));//di map view đế vị trí, zoom 16
                    dialog.dismiss();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        dialog.show();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(20.930580, 106.008171);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Nhà tôi"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,16));
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap != null) {
            mMap.clear();
        }
    }



    private void setOnClick() {
        SearchView searchView = findViewById(R.id.edt_searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equalsIgnoreCase("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    //di chuyển đến vị trí
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)//vị trí
                            .title(address.getFeatureName())//tiêu đề
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.push_location)));//icon
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));//di map view đế vị trí, zoom 16
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        findViewById(R.id.btn_custom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng latLng = new LatLng(20.942030, 106.059859);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)//vị trí
                        .title("Trường ĐHSPKT Hưng Yên")//tiêu đề
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.push_location)));//icon
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));//di map view đế vị trí, zoom 16

            }
        });
        findViewById(R.id.btn_gotomap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog();
            }
        });
    }
    Spinner spinner;

    private void initView() {
        spinner = findViewById(R.id.sp_typemap);
    }
    private void setSpinner() {
        String[] arr = new String[]{"MAP_TYPE_NORMAL", "MAP_TYPE_SATELLITE", "MAP_TYPE_HYBRID", "MAP_TYPE_TERRAIN", "MAP_TYPE_NONE"};
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arr);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case 1:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case 2:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case 3:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    case 4:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
    }
}