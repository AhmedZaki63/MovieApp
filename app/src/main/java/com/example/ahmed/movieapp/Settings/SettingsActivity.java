package com.example.ahmed.movieapp.Settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.example.ahmed.movieapp.DataBase.DataBase;
import com.example.ahmed.movieapp.R;

public class SettingsActivity extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                if (index >= 0) {
                    preference.setSummary(listPreference.getEntries()[index]);
                }

            } else {
                // For all other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
        addPreferencesFromResource(R.xml.pref_general);
        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to their values.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.prefs_choices_list_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.prefs_poster_quality_list_key)));
        Preference preference = findPreference(getString(R.string.prefs_clear_favourite_key));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DataBase dataBase = new DataBase(getApplicationContext());
                dataBase.clearMovies();
                return false;
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
        //This fragment shows general preferences only.
        public static class GeneralPreferenceFragment extends PreferenceFragment {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.pref_general);
                // Bind the summaries of EditText/List/Dialog/Ringtone preferences to their values.
                bindPreferenceSummaryToValue(findPreference(getString(R.string.prefs_choices_list_key)));
                bindPreferenceSummaryToValue(findPreference(getString(R.string.prefs_themes_list_key)));
                bindPreferenceSummaryToValue(findPreference(getString(R.string.prefs_poster_quality_list_key)));
                Preference preference = findPreference(getString(R.string.prefs_clear_favourite_key));
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        DataBase dataBase = new DataBase(getApplicationContext());
                        dataBase.clearMovies();
                        return false;
                    }
                });
            }
        }
    */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }
}
