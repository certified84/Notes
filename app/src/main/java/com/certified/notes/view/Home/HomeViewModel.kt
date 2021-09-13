package com.certified.notes.view.Home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.certified.notes.model.Course
import com.certified.notes.model.Note
import com.certified.notes.model.Todo
import com.certified.notes.util.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(application)

    val randomNotes: LiveData<List<Note>> = repository.randomNotes
    val randomCourses: LiveData<List<Course>> = repository.randomCourses
    val allTodos: LiveData<List<Todo>> = repository.allTodos

    fun updateTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTodo(todo)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTodo(todo)
        }
    }

    fun deleteAllTodos() {
        repository.deleteAllTodos()
    }

    fun deleteCompletedTodos() {
        repository.deleteCompletedTodos()
    }
}