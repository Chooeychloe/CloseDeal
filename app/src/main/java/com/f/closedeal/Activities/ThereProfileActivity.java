package com.f.closedeal.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f.closedeal.Activities.StartUpActivities.LoginSignUp;
import com.f.closedeal.Adapters.AdapterPosts;
import com.f.closedeal.Models.Post;
import com.f.closedeal.Models.Users;
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
import java.util.List;

public class ThereProfileActivity extends AppCompatActivity {

    List<Users> usersList;

    ImageView profileIv;
    TextView  fullNameProfile, phoneNumberProfile, emailProfile, bdayProfile;

    FirebaseAuth firebaseAuth;

    RecyclerView postsRecyclerview;

    List<Post> postList;
    AdapterPosts adapterPosts;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        profileIv = findViewById(R.id.profileIv);
        Button readReviewBtn = findViewById(R.id.readReviewBtn);
        Button reviewBtn = findViewById(R.id.reviewBtn);

        fullNameProfile = findViewById(R.id.user_fullname);
        phoneNumberProfile = findViewById(R.id.phoneno_txt);
        emailProfile = findViewById(R.id.user_email);
        bdayProfile = findViewById(R.id.user_bday);

        firebaseAuth = FirebaseAuth.getInstance();
        postsRecyclerview = findViewById(R.id.recycler_view_profile);

        Intent intent = getIntent();
        uid = intent.getStringExtra("hisUid");

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone number").getValue();
                    String image = "" + ds.child("image").getValue();
                    String bdate = "" + ds.child("date of birth").getValue();

                    fullNameProfile.setText(name);
                    phoneNumberProfile.setText(phone);
                    emailProfile.setText(email);
                    bdayProfile.setText(bdate);
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


        readReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserReviewsActivity.class);
                intent.putExtra("hisUid", uid);
                startActivity(intent);
                finish();
            }
        });
        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriteReviewActivity.class);
                intent.putExtra("hisUid", uid);
                startActivity(intent);
                finish();
            }
        });

        postList = new ArrayList<>();
        
        checkUserStatus();
        loadHisPosts();

    }

    private void loadHisPosts() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        postsRecyclerview.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Post myPost = ds.getValue(Post.class);
                    postList.add(myPost);
                    adapterPosts = new AdapterPosts(ThereProfileActivity.this, postList);
                    postsRecyclerview.setAdapter(adapterPosts);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ThereProfileActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void searchHisPosts(final String searchQuery) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(ThereProfileActivity.this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        postsRecyclerview.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Post myPost = ds.getValue(Post.class);

                    if (myPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase())
                            || myPost.getpDescription().toLowerCase().contains(searchQuery.toLowerCase())
                    ) {
                        postList.add(myPost);
                    }
                    adapterPosts = new AdapterPosts(ThereProfileActivity.this, postList);

                    postsRecyclerview.setAdapter(adapterPosts);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ThereProfileActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void checkUserStatus() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {


        } else {
            startActivity(new Intent(this, LoginSignUp.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_add_group_participant).setVisible(false);
        menu.findItem(R.id.action_group_info).setVisible(false);
        menu.findItem(R.id.action_create_group).setVisible(false);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    searchHisPosts(s);
                } else {
                    loadHisPosts();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    searchHisPosts(s);
                } else {
                    loadHisPosts();
                }

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout) {

            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "false");
            editor.apply();

            firebaseAuth.signOut();
            checkUserStatus();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}