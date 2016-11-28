package com.example.ahmed.movieapp.DataBase;

import android.content.Context;

import com.example.ahmed.movieapp.Models.Movie;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class DataBase {
    private Realm realm;

    public DataBase(Context context) {
        //Initialize Realm
        Realm.init(context);
        realm = Realm.getDefaultInstance();
    }

    public void addMovieToRealm(Movie movie) {
        realm.beginTransaction();
        SavedMovies savedMovies = realm.createObject(SavedMovies.class, movie.getId());
        savedMovies.setTitle(movie.getTitle());
        savedMovies.setPoster(movie.getPoster());
        savedMovies.setCover(movie.getCover());
        savedMovies.setOverview(movie.getOverview());
        savedMovies.setVote(movie.getVote());
        realm.commitTransaction();
    }

    public boolean isFavourite(String id) {
        SavedMovies result = realm.where(SavedMovies.class).equalTo("id", id).findFirst();
        return (result != null);
    }

    public ArrayList<Movie> getMoviesFromRealm() {
        Movie movie;
        ArrayList<Movie> moviesList = new ArrayList<>();
        RealmResults<SavedMovies> results = realm.where(SavedMovies.class).findAll();
        for (SavedMovies m : results) {
            movie = new Movie(m.getTitle(), m.getID(), m.getPoster(), m.getCover(), m.getOverview(), m.getVote());
            moviesList.add(movie);
        }
        return moviesList;
    }

    public void removeMovieFromRealm(String id) {
        SavedMovies result = realm.where(SavedMovies.class).equalTo("id", id).findFirst();
        realm.beginTransaction();
        result.deleteFromRealm();
        realm.commitTransaction();
    }

    public void clearMovies() {
        RealmResults<SavedMovies> results = realm.where(SavedMovies.class).findAll();
        realm.beginTransaction();
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }
}
