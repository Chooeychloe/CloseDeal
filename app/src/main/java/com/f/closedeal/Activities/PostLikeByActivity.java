package com.f.closedeal.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.f.closedeal.Adapters.UserAdapter;
import com.f.closedeal.Models.Users;
import com.f.closedeal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostLikeByActivity extends AppCompatActivity {

    String postId;

    private RecyclerView recyclerView;

    private List<Users> usersList;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_like_by);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Post Liked By: ");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        usersList = new ArrayList<>();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Likes");
        ref.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                usersList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    String hisUid = ""+ds.getRef().getKey();

                    getUsers(hisUid);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUsers(String hisUid) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){

                            Users users = ds.getValue(Users.class);
                            usersList.add(users);

                        }

                        userAdapter = new UserAdapter(PostLikeByActivity.this, usersList);
                        recyclerView.setAdapter(userAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}