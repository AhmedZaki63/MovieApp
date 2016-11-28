package com.example.ahmed.movieapp.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.ahmed.movieapp.Models.Movie;
import com.example.ahmed.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends BaseAdapter {
    ArrayList<Movie> movies;
    Context context;
    ImageView moviePoster;

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        this.movies = movies;
        this.context = context;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.movie_item, null);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String choice = prefs.getString(
                context.getResources().getString(R.string.prefs_poster_quality_list_key),
                context.getResources().getString(R.string.pref_default_poster_quality));

        moviePoster = (ImageView) convertView.findViewById(R.id.list_item_image);
        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w" + choice + getItem(position).getPoster())
                .placeholder(R.drawable.placeholder).into(moviePoster);
        return convertView;
    }

    public void addAll(ArrayList<Movie> movieList) {
        movies.addAll(movieList);
    }

    public void clear() {
        movies.clear();
    }
}
