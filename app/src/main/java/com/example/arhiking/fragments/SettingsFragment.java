package com.example.arhiking.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.arhiking.R;
import com.example.arhiking.databinding.FragmentHomeBinding;
import com.example.arhiking.databinding.FragmentSettingsBinding;
import com.example.arhiking.viewmodels.SettingsViewModel;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

import org.intellij.lang.annotations.Language;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private FragmentSettingsBinding binding;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load preferences from an XML resource
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        SwitchPreferenceCompat themePreference = findPreference("theme_settings");

        // Navigate to user statistics fragment after 'user_data_pref' preference click
        // TODO fix preference
        Preference userDataPreference = findPreference("user_data_pref");
        if(userDataPreference != null){
            userDataPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    Navigation.findNavController(getView()).navigate(R.id.action_navigation_settings_to_userDataFragment);
                    return true;
                }
            });
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key){
            case "language_settings":
                String chosenLanguage = sharedPreferences.getString(key, "");
                if (chosenLanguage.equals("norwegian")){
                    Locale locale = new Locale("no");

                    Locale.setDefault(locale);
                    Resources resources = getResources();
                    Configuration config = resources.getConfiguration();
                    config.setLocale(locale);

                        resources.updateConfiguration(config, resources.getDisplayMetrics());

                } else if (chosenLanguage.equals("english")){
                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Resources resources = getResources();
                    Configuration config = resources.getConfiguration();
                    config.setLocale(locale);

                    getContext().createConfigurationContext(config);
                    resources.updateConfiguration(config, resources.getDisplayMetrics());
                }

        }
        // Settings to change theme
        SwitchPreferenceCompat themePreference = findPreference("theme_settings");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isChecked = sharedPreferences.getBoolean("theme_settings", false);
        if(isChecked){
            themePreference.setSummaryOn(R.string.dark_theme);
             AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            themePreference.setSummaryOff(R.string.light_theme);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

       /* ListPreference languagePreference = findPreference("language_settings");
        if(languagePreference.getValue().equals("english")){
            Resources res = getContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.setLocale(new Locale("en")); // API 17+ only.
            // Use conf.locale = new Locale(...) if targeting lower versions
            res.updateConfiguration(conf, dm);
        }
        else if(languagePreference.getValue().equals("norwegian")){
            Resources res = getContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.setLocale(new Locale("no")); // API 17+ only.
            // Use conf.locale = new Locale(...) if targeting lower versions
            res.updateConfiguration(conf, dm);
        }*/
        /*switch(languagePreference){
            case "English":

        }*/
    }

    public static Context setLocale(Context context, String language) {
        persist(context, language);

        // updating the language for devices above android nougat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }
        // for devices having lower version of android os
        return updateResourcesLegacy(context, language);
    }

    private static void persist(Context context, String language) {
        SharedPreferences preferences =                       PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Locale.Helper.Selected.Language", language);
        editor.apply();
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }

    @Override
    public void onResume() {
        super.onResume();
        SwitchPreferenceCompat themePreference = findPreference("theme_settings");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isChecked = sharedPreferences.getBoolean("theme_settings", false);
        if(isChecked){
            themePreference.setSummaryOn(R.string.dark_theme);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            themePreference.setSummaryOff(R.string.light_theme);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        String chosenLanguage = sharedPreferences.getString("language_settings", "");
        if (chosenLanguage.equals("norwegian")){
            Locale locale = new Locale("no");
            Locale.setDefault(locale);
            Resources resources = getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        } else if (chosenLanguage.equals("english")){
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

      /*  ListPreference languagePreference = findPreference("language_settings");
        if(languagePreference.getValue().equals("english")){

            Resources res = getContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.setLocale(new Locale("en")); // API 17+ only.
            // Use conf.locale = new Locale(...) if targeting lower versions
            res.updateConfiguration(conf, dm);
        }
        else if(languagePreference.getValue().equals("norwegian")){
            Resources res = getContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.setLocale(new Locale("no")); // API 17+ only.
            // Use conf.locale = new Locale(...) if targeting lower versions
            res.updateConfiguration(conf, dm);
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

  /*  public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }*/

}