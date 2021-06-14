package com.f.closedeal.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.f.closedeal.Activities.EditPostActivity;
import com.f.closedeal.Activities.PostDetailActivity;
import com.f.closedeal.Activities.PostLikeByActivity;
import com.f.closedeal.Activities.ThereProfileActivity;
import com.f.closedeal.Activities.ZoomImageActivity;
import com.f.closedeal.Models.Post;
import com.f.closedeal.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    final Context context;
    final List<Post> postList;

    final String myUid;

    final String shared = "SHARED";
    final String traded = "TRADED";


    private final DatabaseReference postRef;

    boolean mProcessLike = false;

    public AdapterPosts(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        final Post post = postList.get(position);

        String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescription = postList.get(position).getpDescription();
        String pImage = postList.get(position).getpImage();
        String pTimeStamp = postList.get(position).getpTime();
        String pStatus = postList.get(position).getpStatus();
        String pLikes = postList.get(position).getpLikes();
        String pComments = postList.get(position).getpComments();
        String transactionStatus = postList.get(position).getTransactionStatus();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(pDescription);
        holder.pStatus.setText(pStatus);
        holder.pLikesTv.setText(pLikes + " Likes");
        holder.pCommentsTv.setText(pComments + " Comments");
        holder.transactedTv.setText(transactionStatus);


        setTransactionStatus(holder, transactionStatus, pId);

        setLikes(holder, pId);

        if (uid.equals(myUid)) {

            holder.transactedTv.setVisibility(View.GONE);

        }

        try {
            Picasso.get().load(uDp).placeholder(R.drawable.default_photo).into(holder.uPictureIv);
        } catch (Exception e) {

        }

        if (pImage.equals("noImage")) {
            holder.pImageIv.setVisibility(View.GONE);
        } else {

            holder.pImageIv.setVisibility(View.VISIBLE);

            try {
                Picasso.get().load(pImage).into(holder.pImageIv);
            } catch (Exception e) {

            }

            holder.pImageIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ZoomImageActivity.class);
                    intent.putExtra("postId", pId);
                    context.startActivity(intent);
                }
            });

        }

        holder.moreBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);

            if (uid.equals(myUid)) {

                popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
                popupMenu.getMenu().add(Menu.NONE, 1, 0, "Transacted?");
                popupMenu.getMenu().add(Menu.NONE, 3, 0, "Edit?");

            }
            popupMenu.getMenu().add(Menu.NONE, 2, 0, "View Detail");


            popupMenu.setOnMenuItemClickListener(item -> {

                int id = item.getItemId();
                if (id == 0) {
                    beginDelete(pId, pImage);
                } else if (id == 3) {
                    Intent intent = new Intent(context, EditPostActivity.class);
                    intent.putExtra("editPostId", pId);
                    context.startActivity(intent);
                } else if (id == 1) {


                    String[] options = {
                            "It has been traded", "It has been shared"
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Post has been transacted?");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {


                                DatabaseReference pref = FirebaseDatabase.getInstance().getReference("Posts").child(pId);
                                pref.child("transactionStatus").setValue(traded);
                                Toast.makeText(context, "It has been traded", Toast.LENGTH_SHORT).show();
                                holder.transactedTv.setText(transactionStatus);

                            } else if (which == 1) {

                                DatabaseReference pref = FirebaseDatabase.getInstance().getReference("Posts").child(pId);
                                pref.child("transactionStatus").setValue(shared);
                                Toast.makeText(context, "It has been shared", Toast.LENGTH_SHORT).show();
                                holder.transactedTv.setText(transactionStatus);


                            }
                        }
                    });
                    builder.create().show();
                } else if (id == 2) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postId", pId);
                    context.startActivity(intent);
                }

                return false;
            });

            popupMenu.show();
        });
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int pLikes = Integer.parseInt(postList.get(position).getpLikes());
                mProcessLike = true;
                final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("Posts").child(pId).child("Likes");

                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mProcessLike) {
                            if (snapshot.child(pId).hasChild(myUid)) {
                                postRef.child(pId).child("pLikes").setValue("" + (pLikes - 1));
                                likesRef.child(pId).child(myUid).removeValue();
                                mProcessLike = false;
                            } else {
                                postRef.child(pId).child("pLikes").setValue("" + (pLikes + 1));
                                likesRef.child(pId).child(myUid).setValue("Liked");
                                mProcessLike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });
        holder.pCommentsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });
        holder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                if (uid.equals(myUid)) {

                    intent = new Intent(context, ThereProfileActivity.class);
                    intent.putExtra("hisUid", myUid);
                    context.startActivity(intent);
                } else {
                    intent = new Intent(context, ThereProfileActivity.class);
                    intent.putExtra("hisUid", uid);
                    context.startActivity(intent);
                }

            }
        });
        holder.pLikesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostLikeByActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });

    }

    private void setTransactionStatus(final MyHolder holder, final String transactionStatus, String pId) {
        DatabaseReference transactedRef = FirebaseDatabase.getInstance().getReference("Posts").child(pId).child(transactionStatus);
        transactedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                holder.transactedTv.setText(transactionStatus);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setLikes(final MyHolder holder, final String postKey) {
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("Posts").child("postId").child("Likes");
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(postKey).hasChild(myUid)) {

                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0, 0);
                    holder.likeBtn.setText("LIKED");
                } else {


                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
                    holder.likeBtn.setText("LIKE");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void beginDelete(String pId, String pImage) {

        if (pImage.equals("noImage")) {
            deleteWithoutImage(pId, pImage);
        } else {
            deleteWithImage(pId, pImage);
        }

    }

    private void deleteWithoutImage(String pId, String pImage) {

        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting");
        DatabaseReference dbNode = FirebaseDatabase.getInstance().getReference("Posts").child(pId);
        dbNode.removeValue();
        Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();


    }

    private void deleteWithImage(String pId, String pImage) {

        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting");

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference dbNode = FirebaseDatabase.getInstance().getReference("Posts").child(pId);
                dbNode.removeValue();
                Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        final ImageView uPictureIv;
        final ImageView pImageIv;
        final TextView uNameTv;
        final TextView pTimeTv;
        final TextView pTitleTv;
        final TextView pDescriptionTv;
        final TextView transactedTv;
        final TextView pLikesTv;
        final TextView pCommentsTv;
        final TextView pStatus;
        final ImageButton moreBtn;
        final Button likeBtn;
        final Button commentBtn;
        final LinearLayout profileLayout;


        public MyHolder(@NonNull View itemView) {
            super(itemView);


            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv = itemView.findViewById(R.id.pImageIv);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            transactedTv = itemView.findViewById(R.id.transactedTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            pCommentsTv = itemView.findViewById(R.id.pCommentsTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            profileLayout = itemView.findViewById(R.id.profileLayout);
            pStatus = itemView.findViewById(R.id.pStatusTv);

        }
    }


}
