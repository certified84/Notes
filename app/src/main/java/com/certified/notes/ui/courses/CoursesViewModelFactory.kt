package com.certified.notes.ui.courses

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CoursesViewModelFactory(val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoursesViewModel::class.java))
            return CoursesViewModel(application) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}