package com.example.ahmed.movieapp.Main;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ahmed.movieapp.Adapters.MovieAdapter;
import com.example.ahmed.movieapp.BuildConfig;
import com.example.ahmed.movieapp.DataBase.DataBase;
import com.example.ahmed.movieapp.DataListener;
import com.example.ahmed.movieapp.Models.Movie;
import com.example.ahmed.movieapp.R;
import com.example.ahmed.movieapp.Settings.SettingsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainFragment extends Fragment {

    private MovieAdapter movieAdapter;
    private ArrayList<Movie> moviesList;
    private ProgressBar progressBar;
    private DataListener dataListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                Toast.makeText(getActivity(), "Updating...", Toast.LENGTH_SHORT).show();
                updateMovies();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        moviesList = new ArrayList<>();
        movieAdapter = new MovieAdapter(getContext(), moviesList);

        GridView gridView = (GridView) view.findViewById(R.id.movies_view);
        gridView.setAdapter(movieAdapter);
        //determine the number of columns in the grid view according to available screen
        int orientation = getActivity().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT && getActivity().findViewById(R.id.details_frame_layout) != null)
            gridView.setNumColumns(2);
        else if (orientation == Configuration.ORIENTATION_LANDSCAPE && getActivity().findViewById(R.id.details_frame_layout) == null)
            gridView.setNumColumns(4);
        else
            gridView.setNumColumns(3);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                ImageView imageView = (ImageView) view.findViewById(R.id.list_item_image);
                ObjectAnimator flip = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 360f);
                flip.setDuration(400).start();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String movieId = movieAdapter.getItem(position).getId();
                        String movieTitle = movieAdapter.getItem(position).getTitle();
                        String movieCover = movieAdapter.getItem(position).getCover();
                        String moviePoster = movieAdapter.getItem(position).getPoster();
                        String movieOverview = movieAdapter.getItem(position).getOverview();
                        String movieVote = movieAdapter.getItem(position).getVote();
                        dataListener.setMovieData(movieTitle, movieId, moviePoster, movieCover, movieOverview, movieVote);
                    }
                }, 400);
            }
        });
        return view;
    }

    private void updateMovies() {
        //Creating object from AsyncTask class
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String choice = prefs.getString(
                getString(R.string.prefs_choices_list_key), getString(R.string.pref_default_choice));
        if (choice.equals("favourite")) {
            DataBase dataBase = new DataBase(getActivity());
            movieAdapter.clear();
            movieAdapter.addAll(dataBase.getMoviesFromRealm());
            movieAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        } else {
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute(choice);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private Movie[] getMovieDataFromJson(String movieJsonStr) throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieJsonArray = movieJson.getJSONArray("results");

            String title, id, poster, cover, overview, vote;
            Movie[] moviesArray = new Movie[movieJsonArray.length()];

            for (int i = 0; i < movieJsonArray.length(); i++) {
                JSONObject obj = movieJsonArray.getJSONObject(i);
                title = obj.getString("title");
                id = obj.getString("id");
                poster = obj.getString("poster_path");
                cover = obj.getString("backdrop_path");
                overview = obj.getString("overview");
                vote = obj.getString("vote_average");
                moviesArray[i] = new Movie(title, id, poster, cover, overview, vote);
            }

            for (Movie s : moviesArray) {
                Log.v(LOG_TAG, "Movies Title List: " + s.getTitle());
                Log.v(LOG_TAG, "Movies Poster List: " + s.getPoster());
                Log.v(LOG_TAG, "Movies Cover List: " + s.getCover());
            }

            return moviesArray;
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            if (params.length == 0)
                return null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                // Construct the URL for the Moviedb query
                final String BASE_URL = "http://api.themoviedb.org/3/movie";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter("language", "en")
                        .appendQueryParameter("api_key", BuildConfig.MOVIE_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI : " + url);

                // Open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();
                if (inputStream == null)
                    return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line = reader.readLine();
                builder.append(line);

                if (builder.length() == 0)
                    return null;

                movieJsonStr = builder.toString();
                Log.v(LOG_TAG, "Movie Jason String: " + movieJsonStr);
            } catch (IOException e) {
                try {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    return getMovieDataFromJson(prefs.getString("Backup", "DEFAULT"));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                Log.e(LOG_TAG, "Error", e);
                // If the code didn't successfully get the movies data it will stop
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Backup", movieJsonStr);
                edit.apply();
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.v(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            progressBar.setVisibility(View.GONE);
            if (result != null) {
                moviesList = new ArrayList<>(Arrays.asList(result));
                movieAdapter.clear();
                movieAdapter.addAll(moviesList);
                movieAdapter.notifyDataSetChanged();
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.setTitle("Sorry No Internet Connection");
                dialog.show();
            }
        }
    }
}
