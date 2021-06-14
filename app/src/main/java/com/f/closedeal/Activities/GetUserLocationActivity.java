package com.f.closedeal.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.f.closedeal.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

public class GetUserLocationActivity extends AppCompatActivity{

    private ImageView profileIv;
    private TextView fullNameProfile, usersLocation;

    private String myUid, uid, name;

    MarkerOptions place1, place2;
    LatLng one, two;

    private double myLat1, myLongt1, hisLat1, hisLongt1;

    private TextView country1, locality1, address1;
    private String country, locality, address, hisName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_location);

        profileIv = findViewById(R.id.profileIv);

        fullNameProfile = findViewById(R.id.user_fullname);
        usersLocation = findViewById(R.id.userslocation);
        country1 = findViewById(R.id.country);
        locality1 = findViewById(R.id.locality);
        address1 = findViewById(R.id.addressLine);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = firebaseAuth.getCurrentUser();
        myUid = fUser.getUid();

        Intent intent = getIntent();
        uid = intent.getStringExtra("hisUid");

        DatabaseReference q = FirebaseDatabase.getInstance().getReference("Location");

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(myUid)){

                    Query myQuery = FirebaseDatabase.getInstance().getReference("Location").orderByChild("uid").equalTo(myUid);

                    myQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {

                                myLat1 = (double) ds.child("latitude").getValue();
                                myLongt1 = (double) ds.child("longitude").getValue();
                                one = new LatLng(myLat1, myLongt1);

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {

                                name = "" + ds.child("name").getValue();
                                String image = "" + ds.child("image").getValue();


                                fullNameProfile.setText(name);
                                try {
                                    Picasso.get().load(image).placeholder(R.drawable.default_photo).into(profileIv);
                                } catch (Exception e) {
                                    profileIv.setImageResource(R.drawable.default_photo);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Location");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(uid)) {

                                Query query2 = FirebaseDatabase.getInstance().getReference("Location").orderByChild("uid").equalTo(uid);

                                query2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {

                                            hisLat1 = (double) ds.child("latitude").getValue();
                                            hisLongt1 = (double) ds.child("longitude").getValue();
                                            hisName = "" + ds.child("name").getValue();
                                            country = (String) ds.child("country").getValue();
                                            locality = (String) ds.child("locality").getValue();
                                            address = (String) ds.child("address").getValue();

                                            country1.setText("Country: " + country);
                                            locality1.setText("Locality: " + locality);
                                            address1.setText("Address: " + address);

                                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrag);
                                            mapFragment.getMapAsync((GoogleMap googleMap) -> {

                                                two = new LatLng(hisLat1, hisLongt1);
                                                place2 = new MarkerOptions().position(two).title(hisName+"'s location");
                                                place1 = new MarkerOptions().position(one).title("My location");
                                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(two, 14));
                                                googleMap.addMarker(place2).showInfoWindow();
                                                googleMap.addMarker(place1).showInfoWindow();

                                                double distance = SphericalUtil.computeDistanceBetween(one, two)/1000;



                                                usersLocation.setText("Your distance to "+hisName +" is "+String.format("%.2f", distance)+" km.");

                                            });

                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else {

                                usersLocation.setText("Ask " + name + " to update location details.");

                                country1.setVisibility(View.GONE);
                                locality1.setVisibility(View.GONE);
                                address1.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {

                    Toast.makeText(getApplicationContext(), "Update Location", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }



}