package com.certified.notes.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.certified.notes.R;
import com.certified.notes.adapters.ViewPagerAdapter;
import com.certified.notes.model.SliderItem;
import com.certified.notes.util.PreferenceKeys;
import com.google.android.material.button.MaterialButton;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class OnboardingFragment extends Fragment {

    private ViewPagerAdapter mViewPagerAdapter;
    private PageIndicatorView mIndicator;
    private List<SliderItem> mSliderItems;
    private ViewPager2 mViewPager;
    private MaterialButton btnGetStarted;

    public OnboardingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);

        mIndicator = view.findViewById(R.id.indicator);
        mViewPager = view.findViewById(R.id.view_pager);
        btnGetStarted = view.findViewById(R.id.btn_get_started);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpSliderItem();
        setUpViewPager();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        btnGetStarted.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(PreferenceKeys.FIRST_TIME_LOGIN, false);
            editor.apply();
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        });
    }

    private void setUpSliderItem() {
        mSliderItems = new ArrayList<>();
        mSliderItems.add(new SliderItem(R.raw.animation_todo, getString(R.string.view_pager_title),
                getString(R.string.view_pager_description)));
        mSliderItems.add(new SliderItem(R.raw.animation_note, getString(R.string.view_pager_title_1),
                getString(R.string.view_pager_title_description_1)));
        mSliderItems.add(new SliderItem(R.raw.animation_report, getString(R.string.view_pager_title_2),
                getString(R.string.view_pager_description_2)));
    }

    private void setUpViewPager() {
        mViewPagerAdapter = new ViewPagerAdapter(mSliderItems, mViewPager);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator.setSelection(position);
                if (position == mSliderItems.size() - 1) {
                    mIndicator.setCount(mSliderItems.size());
                    mIndicator.setSelection(position);
                }
            }
        });
    }
}