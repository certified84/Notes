package com.certified.notes.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.certified.notes.R;
import com.certified.notes.util.PreferenceKeys;

public class SplashFragment extends Fragment {

    private Handler mHandler;
    private NavController mNavController;
    private SharedPreferences mPreferences;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
        mHandler = new Handler();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        isFirstLogin();
        isDarkModeEnabled();
    }

    private void isDarkModeEnabled() {
        boolean isDarkModeEnabled = mPreferences.getBoolean(PreferenceKeys.DARK_MODE, false);
    }

    public void isFirstLogin() {
        boolean isFirstLogin = mPreferences.getBoolean(PreferenceKeys.FIRST_TIME_LOGIN, true);

        if (isFirstLogin) {
            mHandler.postDelayed(() -> {
                NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build();
                mNavController.navigate(R.id.onboardingFragment, null, navOptions);
            }, 3000);
        } else {
            mHandler.postDelayed(() -> {
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }, 3000);
        }
    }
}