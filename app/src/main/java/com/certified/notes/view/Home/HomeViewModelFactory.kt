package com.certified.notes.view.Home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.certified.notes.view.Notes.NotesViewModel
import java.lang.IllegalArgumentException

class HomeViewModelFactory(val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(application) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}