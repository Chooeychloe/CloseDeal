package com.f.closedeal.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.f.closedeal.Activities.CreateGroup;
import com.f.closedeal.Activities.HomeActivity;
import com.f.closedeal.Activities.SettingsActivity;
import com.f.closedeal.Adapters.AdapterChatList;
import com.f.closedeal.Models.ModelChatList;
import com.f.closedeal.Models.ModelChat;
import com.f.closedeal.Models.Users;
import com.f.closedeal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatListFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelChatList> modelChatListList;
    List<Users> usersList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    AdapterChatList adapterChatList;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        modelChatListList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelChatListList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelChatList modelChatList = ds.getValue(ModelChatList.class);
                    modelChatListList.add(modelChatList);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;

    }


    private void loadChats() {

        usersList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Users users = ds.getValue(Users.class);
                    for (ModelChatList modelChatList : modelChatListList) {
                        if (users.getUid() != null && users.getUid().equals(modelChatList.getId())) {
                            usersList.add(users);
                            break;
                        }
                    }

                    adapterChatList = new AdapterChatList(getContext(), usersList);
                    recyclerView.setAdapter(adapterChatList);
                    for (int i = 0; i < usersList.size(); i++) {
                        lastMessage(usersList.get(i).getUid());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void lastMessage(String userId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat == null) {
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null) {
                        continue;
                    }
                    if (chat.getReceiver().equals(currentUser.getUid())
                            && chat.getSender().equals(userId)
                            || chat.getReceiver().equals(userId)
                            && chat.getSender().equals(currentUser.getUid())) {

                        if (chat.getType().equals("image")){
                            theLastMessage = "Sent a photo";
                        }
                        else {
                            theLastMessage = chat.getMessage();
                        }
                    }
                }
                adapterChatList.setLastMessageMap(userId, theLastMessage);
                adapterChatList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkUserStatus() {

        if (currentUser != null) {

        } else {
            startActivity(new Intent(getActivity(), HomeActivity.class));

        }

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_add_group_participant).setVisible(false);
        menu.findItem(R.id.action_group_info).setVisible(false);
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
            checkUserStatus();
        }
        else if (id ==R.id.action_settings){
            startActivity(new Intent(getActivity(), SettingsActivity.class));

        }
        else if (id == R.id.action_create_group){
            startActivity(new Intent(getActivity(), CreateGroup.class));
        }

        return super.onOptionsItemSelected(item);
    }

}