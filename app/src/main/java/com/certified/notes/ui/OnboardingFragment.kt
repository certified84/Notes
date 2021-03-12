package com.certified.notes.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.certified.notes.R
import com.certified.notes.adapters.ViewPagerAdapter
import com.certified.notes.model.SliderItem
import com.certified.notes.util.PreferenceKeys
import com.google.android.material.button.MaterialButton
import com.rd.PageIndicatorView

class OnboardingFragment : Fragment() {

    private lateinit var preferences: SharedPreferences
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var pageIndicatorView: PageIndicatorView
    private lateinit var sliderItem: ArrayList<SliderItem>
    private lateinit var viewPager2: ViewPager2
    private lateinit var btnGetStarted: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = PreferenceManager.getDefaultSharedPreferences(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)

        pageIndicatorView = view.findViewById(R.id.indicator)
        viewPager2 = view.findViewById(R.id.view_pager)
        btnGetStarted = view.findViewById(R.id.btn_get_started)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSliderItem()
        setUpViewPager()

        btnGetStarted.setOnClickListener {
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putBoolean(PreferenceKeys.FIRST_TIME_LOGIN, false)
            editor.apply()
            startActivity(Intent(context, MainActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun setUpSliderItem() {
        sliderItem = ArrayList()
        sliderItem.add(
            SliderItem(
                R.raw.animation_note, getString(R.string.view_pager_title_notes),
                getString(R.string.view_pager_description_notes)
            )
        )
        sliderItem.add(
            SliderItem(
                R.raw.animation_course, getString(R.string.view_pager_title_course),
                getString(R.string.view_pager_description_courses)
            )
        )
        sliderItem.add(
            SliderItem(
                R.raw.animation_todo, getString(R.string.view_pager_title_todo),
                getString(R.string.view_pager_description_todos)
            )
        )
        sliderItem.add(
            SliderItem(
                R.raw.animation_report, getString(R.string.view_pager_title_report),
                getString(R.string.view_pager_description_report)
            )
        )
    }

    private fun setUpViewPager() {
        viewPagerAdapter = ViewPagerAdapter(sliderItem)
        viewPager2.adapter = viewPagerAdapter
        viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                pageIndicatorView.selection = position
                if (position == sliderItem.size - 1) {
                    pageIndicatorView.count = sliderItem.size
                    pageIndicatorView.selection = position
                }
            }
        })
    }
}