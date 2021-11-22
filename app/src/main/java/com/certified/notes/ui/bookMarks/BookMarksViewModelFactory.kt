package com.certified.notes.ui.bookMarks

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BookMarksViewModelFactory(val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookMarksViewModel::class.java))
            return BookMarksViewModel(application) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}