package com.certified.notes.ui.Courses

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.certified.notes.model.BookMark
import com.certified.notes.model.Course
import com.certified.notes.model.Note
import com.certified.notes.util.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoursesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(application)

    val allCourses: LiveData<List<Course>> = repository.allCourses

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

    fun updateBookMark(bookMark: BookMark) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateBookMark(bookMark)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCourse(course)
        }
    }

    fun deleteBookMark(bookMark: BookMark) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBookMark(bookMark)
        }
    }

    fun deleteAllCourses() {
        repository.deleteAllCourses()
    }

    fun getNotesAt(courseCode: String): LiveData<List<Note>>? {
        return repository.getNotesAt(courseCode)
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

    fun getDeletableNotes(noCourse: String): LiveData<List<Note>>? {
        return repository.getDeletableNotes(noCourse)
    }

    fun getDeletableBookmarks(noCourse: String): LiveData<List<BookMark>>? {
        return repository.getDeletableBookmarks(noCourse)
    }

    fun searchCourses(searchQuery: String?): LiveData<List<Course?>?>? {
        return repository.searchCourses(searchQuery)
    }
}