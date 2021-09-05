package com.certified.notes.ui.Main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.certified.notes.model.*
import com.certified.notes.util.Repository

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(application)

    val allNotes: LiveData<List<Note>> = repository.allNotes
    val allCourses: LiveData<List<Course>> = repository.allCourses
    val allNoteIds: LiveData<List<Int>> = repository.allNoteIds
    val allCourseUnits: LiveData<List<Int>> = repository.allCourseUnits
    val allCourseCreditPoints: LiveData<List<Int>> = repository.allCourseCreditPoints
    val user: LiveData<User> = repository.user

    fun insertNote(note: Note) {
        repository.insertNote(note)
    }

    fun insertCourse(course: Course) {
        repository.insertCourse(course)
    }

    fun insertTodo(todo: Todo) {
        repository.insertTodo(todo)
    }

    fun updateNote(note: Note) {
        repository.updateNote(note)
    }

    fun updateCourse(course: Course) {
        repository.updateCourse(course)
    }

    fun updateUser(user: User) {
        repository.updateUser(user)
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