package com.example.ahmed.movieapp.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ahmed.movieapp.DataListener;
import com.example.ahmed.movieapp.Details.DetailsActivity;
import com.example.ahmed.movieapp.Details.DetailsFragment;
import com.example.ahmed.movieapp.R;

public class MainActivity extends AppCompatActivity implements DataListener {

    public boolean isTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainFragment mainFragment = new MainFragment();
        mainFragment.setDataListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, mainFragment, "").commit();
        if (findViewById(R.id.details_frame_layout) != null)
            isTwoPane = true;
    }

    @Override
    public void setMovieData(String title, String id, String poster, String cover, String overview, String vote) {
        Bundle b = new Bundle();
        b.putString("Id", id);
        b.putString("Title", title);
        b.putString("Cover", cover);
        b.putString("Poster", poster);
        b.putString("Overview", overview);
        b.putString("Vote", vote);
        if (!isTwoPane) {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtras(b);
            startActivity(intent);
        } else {
            DetailsFragment detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.details_frame_layout, detailsFragment, "").commit();
        }
    }
}
