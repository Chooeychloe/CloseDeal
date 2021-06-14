package com.f.closedeal.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.f.closedeal.Models.ModelReview;
import com.f.closedeal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.HolderReview>{

    private final Context context;
    private final ArrayList<ModelReview> reviewArrayList;

    public AdapterReview(Context context, ArrayList<ModelReview> reviewArrayList) {
        this.context = context;
        this.reviewArrayList = reviewArrayList;
    }

    @NonNull
    @Override
    public HolderReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_reviews, parent,false);

        return new HolderReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderReview holder, int position) {

        ModelReview review = reviewArrayList.get(position);

        String uid = review.getUid();
        String ratings = review.getRatings();
        String reviews = review.getReview();
        String timeStamp = review.getTimeStamp();

        loadUserDetail(review, holder);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.ratingBar.setRating(Float.parseFloat(ratings));
        holder.reviewTv.setText(reviews);
        holder.dateTv.setText(dateFormat);

    }

    private void loadUserDetail(ModelReview review, HolderReview holder) {

        String uid = review.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String name = ""+snapshot.child("name").getValue();
                        String image = ""+snapshot.child("image").getValue();

                        holder.nameTv.setText(name);
                        try {
                            Picasso.get().load(image).placeholder(R.drawable.default_photo).into(holder.profileIv);
                        }
                        catch (Exception e){
                            holder.profileIv.setImageResource(R.drawable.default_photo);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }

    class HolderReview extends RecyclerView.ViewHolder {

        private final ImageView profileIv;
        private final TextView nameTv;
        private final TextView dateTv;
        private final TextView reviewTv;
        private final RatingBar ratingBar;

        public HolderReview(@NonNull View itemView) {
            super(itemView);


            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            dateTv = itemView.findViewById(R.id.dateTv);
            reviewTv = itemView.findViewById(R.id.reviewTv);
        }
    }

}
