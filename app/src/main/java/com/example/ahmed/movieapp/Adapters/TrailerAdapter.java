package com.example.ahmed.movieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ahmed.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> videos;

    public TrailerAdapter(Context context, ArrayList<String> videos) {
        this.videos = videos;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Picasso.with(context)
                .load("https://img.youtube.com/vi/" + videos.get(position) + "/mqdefault.jpg")
                .placeholder(R.drawable.video_placeholder).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoLink = "https://www.youtube.com/watch?v=" + videos.get(holder.getAdapterPosition());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoLink));
                intent.putExtra("force_fullscreen", true);
                context.startActivity(intent);
            }
        });
    }

    public void addAll(ArrayList<String> movieList) {
        videos.addAll(movieList);
    }

    public void clear() {
        videos.clear();
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.list_item_video);
        }
    }
}
