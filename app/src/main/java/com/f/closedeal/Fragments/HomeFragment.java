package com.f.closedeal.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f.closedeal.Activities.SettingsActivity;
import com.f.closedeal.Adapters.AdapterPosts;
import com.f.closedeal.Models.Post;
import com.f.closedeal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private List<Post> postList;
    private AdapterPosts adapterPosts;

    private TextView verifyMsg;
    private Button verifyBtn;
    private FirebaseUser fUser;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        firebaseAuth = FirebaseAuth.getInstance();
        fUser = firebaseAuth.getCurrentUser();
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        verifyBtn = root.findViewById(R.id.verifyBtn);
        verifyMsg = root.findViewById(R.id.verifyMsg);

        recyclerView = root.findViewById(R.id.posts_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();

        if (fUser.isEmailVerified()){
            verifyMsg.setVisibility(View.GONE);
            verifyBtn.setVisibility(View.GONE);
        }


            loadPosts();
        
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String verificationStatus = (String) ds.child("verificationStatus").getValue();

                    if (verificationStatus == "notVerified") {
                        verifyMsg.setVisibility(View.VISIBLE);
                        verifyBtn.setVisibility(View.VISIBLE);
                    } else {
                        verifyBtn.setOnClickListener(v -> fUser.sendEmailVerification().addOnSuccessListener(aVoid -> {

                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("verificationStatus", "verified");

                            dbRef.updateChildren(hashMap);

                            Toast.makeText(getActivity(), "Please logout and login again.", Toast.LENGTH_LONG).show();

                        }));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return root;
    }

    private void loadPosts() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);

                    postList.add(post);

                    adapterPosts = new AdapterPosts(getActivity(), postList);
                    recyclerView.setAdapter(adapterPosts);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void searchPosts(String searchQuery) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);

                    if (post.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            post.getpDescription().toLowerCase().contains(searchQuery.toLowerCase())
                            || post.getpStatus().toLowerCase().contains(searchQuery.toLowerCase())) {
                        postList.add(post);
                    }

                    adapterPosts = new AdapterPosts(getActivity(), postList);
                    recyclerView.setAdapter(adapterPosts);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_create_group).setVisible(false);
        menu.findItem(R.id.action_add_group_participant).setVisible(false);
        menu.findItem(R.id.action_group_info).setVisible(false);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    searchPosts(s);
                } else {
                    loadPosts();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    searchPosts(s);
                } else {
                    loadPosts();
                }

                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout) {


            SharedPreferences preferences = this.getActivity().getSharedPreferences("checkbox", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "false");
            editor.apply();

            firebaseAuth.signOut();

        } else if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

}