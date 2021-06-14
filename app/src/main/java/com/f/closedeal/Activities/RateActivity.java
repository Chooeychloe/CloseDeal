package com.f.closedeal.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f.closedeal.Adapters.ReviewAdapter;
import com.f.closedeal.Models.Review;
import com.f.closedeal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RateActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView profileTv;
    TextView nameTv, rateCount;
    EditText reviewEt;
    Button sendReview;
    RatingBar ratingBar;
    float rateValue;
    String temp ,review;
    RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;

    String hisUid;
    String myUid;
    String hisImage;

    List<Review> reviewList;
    ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        profileTv = findViewById(R.id.rating_profile_image);
        nameTv = findViewById(R.id.nameTv);
        reviewEt = findViewById(R.id.reviewEt);
        sendReview = findViewById(R.id.sendReview);
        ratingBar = findViewById(R.id.ratingBar);
        rateCount = findViewById(R.id.rateCount);

        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        myUid = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");

        recyclerView = findViewById(R.id.rating_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        Query userQuery = usersDbRef.orderByChild("uid").equalTo(hisUid);

        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {

                    String name = "" + ds.child("name").getValue();
                    hisImage = "" + ds.child("image").getValue();

                    nameTv.setText(name);

                    try {
                        Picasso.get().load(hisImage).placeholder(R.drawable.default_photo).into(profileTv);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.default_photo).into(profileTv);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {

            rateValue = ratingBar.getRating();
            if (rateValue<=1 && rateValue>0){
                rateCount.setText("Bad "+rateValue+"/5");
            }
            else if (rateValue<=2 && rateValue>1){
                rateCount.setText("OK "+rateValue+"/5");
            }
            else if (rateValue<=3 && rateValue>2){
                rateCount.setText("Good "+rateValue+"/5");
            }
            else if (rateValue<=4 && rateValue>3){
                rateCount.setText("Very Good "+rateValue+"/5");
            }
            else if (rateValue<=5 && rateValue>4){
                rateCount.setText("Best "+rateValue+"/5");
            }

        });
        sendReview.setOnClickListener(v -> {
            review = reviewEt.getText().toString().trim();
            temp = rateCount.getText().toString();
            ratingBar.setRating(0);
            rateCount.setText("");
            reviewEt.setText("");
            submitReview(temp,review );
        });
     readReviews();

    }

    private void submitReview(String temp, String review) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timeStamp = String.valueOf(System.currentTimeMillis());


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("rater", myUid);
        hashMap.put("ratedUser", hisUid);
        hashMap.put("rating", temp);
        hashMap.put("review", review);
        hashMap.put("timeStamp", timeStamp);

        databaseReference.child("Reviews").push().setValue(hashMap);


    }

    private void readReviews() {

        reviewList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Reviews");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Review review = ds.getValue(Review.class);
                    if (review.getRatedUser().equals(hisUid)) {

                        reviewList.add(review);

                    }
                    reviewAdapter = new ReviewAdapter(RateActivity.this, reviewList);
                    reviewAdapter.notifyDataSetChanged();

                    recyclerView.setAdapter(reviewAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}