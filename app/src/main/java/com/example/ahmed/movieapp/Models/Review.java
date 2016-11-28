package com.example.ahmed.movieapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
    private String authorName, content;

    public Review(String authorName, String content) {
        this.authorName = authorName;
        this.content = content;
    }

    protected Review(Parcel in) {
        authorName = in.readString();
        content = in.readString();
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authorName);
        dest.writeString(content);
    }
}
