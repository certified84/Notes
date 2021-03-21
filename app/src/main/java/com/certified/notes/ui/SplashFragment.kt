package com.certified.notes.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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

    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    private lateinit var notifyManager: NotificationManager
    private val NOTES_NOTIFICATION_ID = 0

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
        createNotificationChannel()
        navController = Navigation.findNavController(view)
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed(this::isFirstLogin, 3000)
    }

    private fun createNotificationChannel() {
        notifyManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                getString(R.string.notes_notification),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notes_reminder_notification)
            notifyManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder? {
        val notificationIntent = Intent(requireContext(), MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            requireContext(), NOTES_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val message = getString(R.string.notification_message)
        return NotificationCompat.Builder(requireContext(), PRIMARY_CHANNEL_ID)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.drawable.ic_notes)
            .setContentTitle(getString(R.string.notes_reminder))
            .setColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setTicker("Notes")
            .addAction(R.drawable.ic_add, "Add Note", notificationPendingIntent)
            .setAutoCancel(true)
    }

    private fun sendNotification() {
        val notifyBuilder = getNotificationBuilder()
        notifyManager.notify(NOTES_NOTIFICATION_ID, notifyBuilder!!.build())
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
                sendNotification()
                requireActivity().finish()
            }
        }
    }
}