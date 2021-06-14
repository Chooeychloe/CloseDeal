package com.f.closedeal.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.f.closedeal.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class SettingsActivity extends AppCompatActivity {

    private TextView country1, locality1, address1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    public static final int REQUEST_CHECK_SETTING = 1001;
    private String myName, uid;

    MarkerOptions place1, place2;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    private static final String TOPIC_POST_NOTIFICATION = "POST";


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = firebaseAuth.getCurrentUser();
        uid = fUser.getUid();
        SwitchCompat postSwitch = findViewById(R.id.postSwitch);
        Button find = findViewById(R.id.find);
        Button turnOnGPS = findViewById(R.id.turnOnGPS);
        country1 = findViewById(R.id.country);
        locality1= findViewById(R.id.locality);
        address1 = findViewById(R.id.addressLine);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        sp = getSharedPreferences("Notification_SP", MODE_PRIVATE);
        boolean isPostEnabled = sp.getBoolean("" + TOPIC_POST_NOTIFICATION, false);

        Query myRef = FirebaseDatabase.getInstance().getReference("Users");
        myRef.orderByChild("uid").equalTo(firebaseAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            myName = "" + ds.child("name").getValue();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        postSwitch.setChecked(isPostEnabled);

        postSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                editor = sp.edit();
                editor.putBoolean("" + TOPIC_POST_NOTIFICATION, isChecked);
                editor.apply();

                if (isChecked) {
                    subscribePostNotification();
                } else {
                    unsubscribePostNotification();
                }
            }
        });

        turnOnGPS.setOnClickListener(v -> {
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(2000);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                    .checkLocationSettings(builder.build());

            result.addOnCompleteListener(task -> {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(SettingsActivity.this, "GPS is already On", Toast.LENGTH_SHORT).show();
                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(SettingsActivity.this, REQUEST_CHECK_SETTING);
                            } catch (IntentSender.SendIntentException sendIntentException) {
                                break;
                            }
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }

                }

            });

        });


        find.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    fusedLocationProviderClient.getLastLocation().
                            addOnSuccessListener(location -> {

                                if (location != null) {

                                    try {
                                        Geocoder geocoder = new Geocoder(SettingsActivity.this);

                                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                                                location.getLongitude(), 1);


                                        Double latitude = addresses.get(0).getLatitude();
                                        Double longitude = addresses.get(0).getLongitude();
                                        String country = addresses.get(0).getCountryName();
                                        String locality = addresses.get(0).getLocality();
                                        String address = addresses.get(0).getAddressLine(0);


                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("latitude", latitude);
                                        hashMap.put("longitude", longitude);
                                        hashMap.put("country", country);
                                        hashMap.put("locality", locality);
                                        hashMap.put("address", address);
                                        hashMap.put("name", myName);
                                        hashMap.put("uid", uid);

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference reference = database.getReference("Location");
                                        reference.child(uid).setValue(hashMap);

                                        Toast.makeText(getApplicationContext(), "Location has been updated", Toast.LENGTH_SHORT).show();

                                        country1.setText("Country: " + country);
                                        locality1.setText("Locality: " + locality);
                                        address1.setText("Address: " + address);

                                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrag);
                                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(GoogleMap googleMap) {

                                                LatLng latLng = new LatLng(latitude, longitude);
                                                place2 = new MarkerOptions().position(latLng).title("My location");

                                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                                                googleMap.addMarker(place2).showInfoWindow();


                                            }
                                        });


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "Update your location", Toast.LENGTH_SHORT).show();

                                }
                            });

                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK_SETTING);

                }

            }
        });

    }


    private void unsubscribePostNotification() {

        FirebaseMessaging.getInstance().unsubscribeFromTopic("" + TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will not receive post notifications";
                        if (!task.isSuccessful()) {
                            msg = "UnSubscription failed";
                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void subscribePostNotification() {

        FirebaseMessaging.getInstance().subscribeToTopic("" + TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will receive post notifications";
                        if (!task.isSuccessful()) {
                            msg = "Subscription failed";
                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == REQUEST_CHECK_SETTING) {
            switch (resultCode) {

                case Activity.RESULT_OK:

                    Toast.makeText(this, "GPS is Turned On", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "GPS is required to be turned on", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}