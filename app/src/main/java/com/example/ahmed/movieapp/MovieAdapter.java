package com.example.ahmed.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends BaseAdapter {
    ArrayList<Movie> myMovies;
    Context myContext;

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        myMovies = movies;
        myContext = context;
    }

    @Override
    public int getCount() {
        return myMovies.size();
    }

    @Override
    public Movie getItem(int position) {
        return myMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addAll(ArrayList<Movie> movieList) {
        myMovies.addAll(movieList);
    }

    public void clear() {
        myMovies.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            //LayoutInflater inflater = LayoutInflater.from(myContext);
            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.list_item_text);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_image);
        textView.setText(getItem(position).getTitle());
        if (getItem(position).getPoster().equals("null"))
            Picasso.with(myContext)
                    .load("http://vignette2.wikia.nocookie.net/powerlisting/images/4/49/NoImage.gif/revision/latest?cb=20140424063554").into(imageView);
        else
            Picasso.with(myContext)
                    .load("https://image.tmdb.org/t/p/w500" + getItem(position).getPoster()).into(imageView);
        return convertView;
    }
}
