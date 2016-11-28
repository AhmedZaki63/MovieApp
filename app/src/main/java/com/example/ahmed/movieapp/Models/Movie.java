package com.example.ahmed.movieapp.Models;

public class Movie {
    private String title;
    private String id;
    private String poster;
    private String cover;
    private String overview;
    private String vote;

    public Movie(String title, String id, String poster, String cover, String overview, String vote) {
        this.title = title;
        this.id = id;
        this.poster = poster;
        this.cover = cover;
        this.overview = overview;
        this.vote = vote;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getPoster() {
        return poster;
    }

    public String getCover() {
        return cover;
    }

    public String getOverview() {
        return overview;
    }

    public String getVote() {
        return vote;
    }
}
