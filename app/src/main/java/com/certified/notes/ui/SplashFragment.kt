package com.certified.notes.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.certified.notes.R
import com.certified.notes.util.PreferenceKeys

class SplashFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = PreferenceManager.getDefaultSharedPreferences(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed(this::isFirstLogin, 3000)
    }

    private fun isFirstLogin() {
        val isFirstLogin: Boolean = preferences.getBoolean(PreferenceKeys.FIRST_TIME_LOGIN, true)
        if (isFirstLogin) {
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build()
            navController.navigate(R.id.onboardingFragment, null, navOptions)
        } else {
            val context1 = context
            if (context1 != null) {
                startActivity(Intent(context, MainActivity::class.java))
                requireActivity().finish()
            }
        }
    }
}