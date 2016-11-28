package com.example.ahmed.movieapp.Details;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmed.movieapp.Adapters.TrailerAdapter;
import com.example.ahmed.movieapp.BuildConfig;
import com.example.ahmed.movieapp.DataBase.DataBase;
import com.example.ahmed.movieapp.Models.Movie;
import com.example.ahmed.movieapp.Models.Review;
import com.example.ahmed.movieapp.R;
import com.example.ahmed.movieapp.ReviewActivity;
import com.squareup.picasso.Picasso;

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

public class DetailsFragment extends Fragment {

    private boolean isFavourite;
    private String id, title, poster, cover, overview, rate;
    private String choice;
    private ArrayList<String> trailersList;
    private TrailerAdapter trailerAdapter;
    private ArrayList<Review> reviewsList;
    private RelativeLayout videosPlaceholder;
    private DataBase dataBase;
    private ImageButton favouriteButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        videosPlaceholder = (RelativeLayout) view.findViewById(R.id.videos_placeholder);

        //get Data From MainActivity
        Bundle bundle = getArguments();
        id = bundle.getString("Id");
        title = bundle.getString("Title");
        cover = bundle.getString("Cover");
        poster = bundle.getString("Poster");
        overview = bundle.getString("Overview");
        rate = bundle.getString("Vote");
        setDataToView(view);

        //Trailers & reviews ArrayLists and Adapters
        reviewsList = new ArrayList<>();
        trailersList = new ArrayList<>();
        trailerAdapter = new TrailerAdapter(getContext(), trailersList);
        RecyclerView video_view = (RecyclerView) view.findViewById(R.id.videos_view);
        video_view.setAdapter(trailerAdapter);
        video_view.setLayoutManager(new LinearLayoutManager(getActivity(), 0, false));

        //Get user's view choice from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        choice = prefs.getString(
                getString(R.string.prefs_choices_list_key), getString(R.string.pref_default_choice));
        dataBase = new DataBase(getActivity());

        //Find if selected movie exist in database or not
        isFavourite = dataBase.isFavourite(id);

        favouriteButton = (ImageButton) view.findViewById(R.id.favourite_button);
        if (isFavourite)
            favouriteButton.setBackgroundResource(R.drawable.favourite);
        else
            favouriteButton.setBackgroundResource(R.drawable.not_favourite);
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavourite) {
                    dataBase.removeMovieFromRealm(id);
                    if (choice.equals("favourite")) {
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                    }
                    favouriteButton.setBackgroundResource(R.drawable.not_favourite);
                    isFavourite = false;
                } else {
                    Movie movie = new Movie(title, id, poster, cover, overview, rate);
                    dataBase.addMovieToRealm(movie);
                    favouriteButton.setBackgroundResource(R.drawable.favourite);
                    isFavourite = true;
                }
            }
        });

        Button reviewsButton = (Button) view.findViewById(R.id.review_button);
        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reviewsList.isEmpty()) {
                    Toast.makeText(getActivity(), "No Reviews Available Yet", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getActivity(), ReviewActivity.class);
                    intent.putParcelableArrayListExtra("reviews", reviewsList);
                    startActivity(intent);
                }
            }
        });
        //Call updateData method
        updateData();
        return view;
    }

    public void setDataToView(View view) {
        //Setting movie poster and cover to an image view
        ImageView movieImageView = (ImageView) view.findViewById(R.id.details_image);
        //determine the image according to available screen
        int orientation = getActivity().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE && getActivity().findViewById(R.id.main_frame_layout) == null)
            Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/w320" + poster).into(movieImageView);
        else
            Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/w500" + cover).into(movieImageView);
        //Setting movie title to a text view
        TextView titleText = (TextView) view.findViewById(R.id.details_title);
        titleText.setText(title);
        //Setting movie rate to a text view
        TextView rateText = (TextView) view.findViewById(R.id.details_rate);
        if (rate == null || rate.equals("0"))
            rateText.setText(getText(R.string.no_rate_text));
        else
            rateText.setText("Rate : " + rate + "/10");
        //Setting movie overview text to a text view
        TextView overviewText = (TextView) view.findViewById(R.id.details_overview);
        overviewText.setText(overview);
    }

    public void updateData() {
        //Creating objects from AsyncTask class
        FetchVideoTask videoTask = new FetchVideoTask();
        videoTask.execute(id, "videos");
        FetchVideoTask reviewTask = new FetchVideoTask();
        reviewTask.execute(id, "reviews");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(getActivity());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchVideoTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchVideoTask.class.getSimpleName();

        private Void getVideoDataFromJson(String videoJsonStr) throws JSONException {

            JSONObject trailerJason = new JSONObject(videoJsonStr);
            JSONArray trailerJsonArray = trailerJason.getJSONArray("results");
            String[] trailersLink = new String[trailerJsonArray.length()];
            for (int i = 0; i < trailerJsonArray.length(); i++) {
                JSONObject obj = trailerJsonArray.getJSONObject(i);
                String videoKey = obj.getString("key");
                trailersLink[i] = videoKey;
                Log.v(LOG_TAG, "Video Link: " + videoKey);
            }
            trailersList = new ArrayList<>(Arrays.asList(trailersLink));
            trailerAdapter.clear();
            trailerAdapter.addAll(trailersList);
            return null;
        }

        private Void getReviewDataFromJson(String reviewJsonStr) throws JSONException {

            JSONObject reviewJason = new JSONObject(reviewJsonStr);
            JSONArray reviewJsonArray = reviewJason.getJSONArray("results");
            Review[] reviews = new Review[reviewJsonArray.length()];
            String content, author;
            for (int i = 0; i < reviewJsonArray.length(); i++) {
                JSONObject obj = reviewJsonArray.getJSONObject(i);
                author = obj.getString("author");
                content = obj.getString("content");
                reviews[i] = new Review(author, content);
            }
            reviewsList = new ArrayList<>(Arrays.asList(reviews));
            return null;
        }

        @Override
        protected Void doInBackground(String... params) {

            if (params.length == 0)
                return null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String videoJsonStr = null;

            try {
                // Construct the URL for the Moviedb videos query
                final String BASE_URL = "http://api.themoviedb.org/3/movie";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendPath(params[1])
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

                videoJsonStr = builder.toString();
                Log.v(LOG_TAG, "Video Jason String: " + videoJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the videos data it will stop
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
                if (params[1].equals("videos"))
                    return getVideoDataFromJson(videoJsonStr);
                else if (params[1].equals("reviews"))
                    return getReviewDataFromJson(videoJsonStr);
            } catch (JSONException e) {
                Log.v(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            trailerAdapter.notifyDataSetChanged();
            if (!trailersList.isEmpty())
                videosPlaceholder.setVisibility(View.GONE);
        }
    }
}
