package com.f.closedeal.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.f.closedeal.Models.Review;
import com.f.closedeal.R;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyHolder> {

    final Context context;
    final List<Review> reviewList;
    String imageUri;

    FirebaseUser firebaseUser;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_rate, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String review = reviewList.get(position).getReview();
        String rating = reviewList.get(position).getRating();
        String timeStamp = reviewList.get(position).getTimeStamp();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        holder.reviewEt.setText(review);
        holder.rateCount.setText(rating);
        holder.timeTv.setText(dateTime);

        try {
            Picasso.get().load(imageUri).into(holder.ratingProfileImage);
        } catch (Exception e) {

        }


    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        final ImageView ratingProfileImage;
        final TextView username;
        final TextView rateCount;
        final TextView reviewEt;
        final TextView timeTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            ratingProfileImage = itemView.findViewById(R.id.rating_profile_image);
            username = itemView.findViewById(R.id.nameTv);
            rateCount = itemView.findViewById(R.id.rateCount);
            reviewEt = itemView.findViewById(R.id.reviewEt);
            timeTv = itemView.findViewById(R.id.timeStamp);

        }

    }
}
