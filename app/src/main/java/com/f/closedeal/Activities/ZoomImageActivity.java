package com.f.closedeal.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.f.closedeal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zolad.zoominimageview.ZoomInImageViewAttacher;

public class ZoomImageActivity extends AppCompatActivity {

    private ImageView pImageIv;
    private String postId, pImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_zoom_image);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        pImageIv = findViewById(R.id.pImageIv);
        ZoomInImageViewAttacher mIvAttacter = new ZoomInImageViewAttacher();
        mIvAttacter.attachImageView(pImageIv);

        mIvAttacter.setZoomable(true);


        loadPostInfo();

    }
    private void loadPostInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {


                    pImage = "" + ds.child("pImage").getValue();
                    try {
                        Picasso.get().load(pImage).into(pImageIv);
                    } catch (Exception e) {

                    }

                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}