package com.example.ahmed.movieapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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

public class DetailsFragment extends Fragment {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(getActivity());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            String id = intent.getStringExtra("Id");
            String title = intent.getStringExtra("Title");
            String cover = intent.getStringExtra("Cover");
            String poster = intent.getStringExtra("Poster");
            String overview = intent.getStringExtra("Overview");
            String vote = intent.getStringExtra("Vote");
            //Setting movie overview text to a text view
            TextView textView = (TextView) view.findViewById(R.id.details_text);
            textView.setText(overview);
            //Setting movie poster and cover to an image view
            ImageView imageView = (ImageView) view.findViewById(R.id.details_image);
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/w500" + cover).into(imageView);
            else
                Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/w500" + poster).into(imageView);
            //Setting movie rate to a rating bar
            RatingBar ratingBar = (RatingBar) view.findViewById(R.id.details_rate);
            ratingBar.setRating(Float.parseFloat(vote) / 2);
            //Creating object from AsyncTask class
            FetchVideoTask videoTask = new FetchVideoTask();
            videoTask.execute(id);
        }
        return view;
    }

    public class FetchVideoTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = FetchVideoTask.class.getSimpleName();

        private String getVideoDataFromJson(String videoJsonStr) throws JSONException {

            JSONObject movieJson = new JSONObject(videoJsonStr);
            JSONArray movieJsonArray = movieJson.getJSONArray("results");
            try {
                JSONObject obj = movieJsonArray.getJSONObject(0);
                String videoKey = obj.getString("key");
                String videoLink = "https://www.youtube.com/watch?v=" + videoKey;
                Log.v(LOG_TAG, "Video Link: " + videoLink);
                return videoLink;
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }

        @Override
        protected String doInBackground(String... params) {

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
                        .appendPath("videos")
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
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line + "\n");

                if (buffer.length() == 0) {
                    // Nothing to do.
                    return null;
                }
                videoJsonStr = buffer.toString();
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
                return getVideoDataFromJson(videoJsonStr);
            } catch (JSONException e) {
                Log.v(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            if (result != null) {
                ImageButton imageButton = (ImageButton) getActivity().findViewById(R.id.play_button);
                imageButton.setVisibility(View.VISIBLE);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(result)));
                    }
                });
            }
        }
    }
}
