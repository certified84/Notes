package com.certified.notes.ui.Home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.certified.notes.model.Course
import com.certified.notes.model.Note
import com.certified.notes.model.Todo
import com.certified.notes.util.Repository

class HomeViewModel(application: Application): AndroidViewModel(application) {

    private val repository = Repository(application)

    val randomNotes: LiveData<List<Note>> = repository.randomNotes
    val randomCourses: LiveData<List<Course>> = repository.randomCourses
    val allTodos: LiveData<List<Todo>> = repository.allTodos

    fun updateTodo(todo: Todo) {
        repository.updateTodo(todo)
    }

    fun deleteTodo(todo: Todo) {
        repository.deleteTodo(todo)
    }

    fun deleteAllTodos() {
        repository.deleteAllTodos()
    }

    fun deleteCompletedTodos() {
        repository.deleteCompletedTodos()
    }
}