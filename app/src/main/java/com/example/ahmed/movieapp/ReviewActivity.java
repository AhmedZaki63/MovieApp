package com.example.ahmed.movieapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import com.example.ahmed.movieapp.Adapters.ReviewAdapter;
import com.example.ahmed.movieapp.Models.Review;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ArrayList<Review> reviewList = getIntent().getParcelableArrayListExtra("reviews");
        ReviewAdapter reviewAdapter = new ReviewAdapter(this, reviewList);
        ExpandableListView reviewsList = (ExpandableListView) findViewById(R.id.reviews_view);
        if (reviewsList != null)
            reviewsList.setAdapter(reviewAdapter);
    }
}
