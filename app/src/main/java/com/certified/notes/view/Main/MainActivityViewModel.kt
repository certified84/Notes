package com.certified.notes.view.Main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.certified.notes.model.*
import com.certified.notes.util.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(application)

    val allCourses: LiveData<List<Course>> = repository.allCourses
    val allNoteIds: LiveData<List<Int>> = repository.allNoteIds
    val allCourseUnits: LiveData<List<Int>> = repository.allCourseUnits
    val allCourseCreditPoints: LiveData<List<Int>> = repository.allCourseCreditPoints
    val user: LiveData<User> = repository.user

    fun insertNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNote(note)
        }
    }

    fun insertCourse(course: Course) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCourse(course)
        }
    }

    fun insertTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTodo(todo)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCourse(course)
        }
    }

    fun deleteAllCourses() {
        repository.deleteAllCourses()
    }

    fun getCourseCode(courseTitle: String): String {
        return repository.getCourseCode(courseTitle)
    }

    fun getCourseTitle(courseCode: String): String {
        return repository.getCourseTitle(courseCode)
    }

    fun getBookMarkAt(noteId: Int): LiveData<List<BookMark>>? {
        return repository.getBookMarkAt(noteId)
    }
}