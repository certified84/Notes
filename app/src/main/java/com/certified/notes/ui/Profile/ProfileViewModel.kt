package com.certified.notes.ui.Profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.certified.notes.model.User
import com.certified.notes.util.Repository

class ProfileViewModel(application: Application): AndroidViewModel(application) {

    private val repository = Repository(application)

    val user: LiveData<User> = repository.user

    fun updateUser(user: User) {
        repository.updateUser(user)
    }
}