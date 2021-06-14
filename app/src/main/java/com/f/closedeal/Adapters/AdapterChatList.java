package com.f.closedeal.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.f.closedeal.Activities.ChatActivity;
import com.f.closedeal.Models.Users;
import com.f.closedeal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyHolder> {

    final Context context;
    final List<Users> usersList;
    private final HashMap<String, String> lastMessageMap;

    final FirebaseAuth firebaseAuth;
    final String myUid;

    public AdapterChatList(Context context, List<Users> usersList) {
        this.context = context;
        this.usersList = usersList;
        lastMessageMap = new HashMap<>();

        firebaseAuth =FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String hisUid = usersList.get(position).getUid();
        String userImage = usersList.get(position).getImage();
        String username = usersList.get(position).getName();
        String lastMessage = lastMessageMap.get(hisUid);
        

        holder.nameTv.setText(username);
        if (lastMessage == null || lastMessage.equals("default")) {
            holder.lastMessageTv.setVisibility(View.GONE);
        } else {
            holder.lastMessageTv.setVisibility(View.VISIBLE);
            holder.lastMessageTv.setText(lastMessage);
        }
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.default_photo).into(holder.profileIv);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.default_photo).into(holder.profileIv);
        }

        if (usersList.get(position).getOnlineStatus().equals("online")) {
            holder.onlineStatusIv.setImageResource(R.drawable.circle_online);

        } else {
            holder.onlineStatusIv.setImageResource(R.drawable.circle_offline);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               inBlockedOrNot(hisUid);
            }
        });

    }


    private void inBlockedOrNot(String hisUID){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUID).child("BlockedUsers").orderByChild("uid").equalTo(myUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if (ds.exists()){
                                Toast.makeText(context, "Blocked: Cannot message the user", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("hisUid", hisUID);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public void setLastMessageMap(String userId, String lastMessage) {

        lastMessageMap.put(userId, lastMessage);

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        final ImageView profileIv;
        final ImageView onlineStatusIv;
        final TextView nameTv;
        final TextView lastMessageTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            onlineStatusIv = itemView.findViewById(R.id.onlineStatusIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv);
        }
    }

}
