package com.example.ahmed.movieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.ahmed.movieapp.Models.Review;
import com.example.ahmed.movieapp.R;

import java.util.ArrayList;

public class ReviewAdapter extends BaseExpandableListAdapter {
    ArrayList<Review> reviews;
    Context context;
    TextView authorText, contentText;

    public ReviewAdapter(Context context, ArrayList<Review> reviews) {
        this.reviews = reviews;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return reviews.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return reviews.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return reviews.get(groupPosition).getContent();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.review_item_parent, parent, false);
        }
        authorText = (TextView) convertView.findViewById(R.id.author);
        authorText.setText(reviews.get(groupPosition).getAuthorName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.review_item_child, parent, false);
        }
        contentText = (TextView) convertView.findViewById(R.id.review);
        contentText.setText(reviews.get(groupPosition).getContent());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
