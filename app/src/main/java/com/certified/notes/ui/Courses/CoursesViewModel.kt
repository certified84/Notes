package com.certified.notes.ui.Courses

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.certified.notes.model.*
import com.certified.notes.util.Repository

class CoursesViewModel(application: Application): AndroidViewModel(application) {

    private val repository = Repository(application)

    val allCourses: LiveData<List<Course>> = repository.allCourses

    fun updateNote(note: Note) {
        repository.updateNote(note)
    }

    fun updateCourse(course: Course) {
        repository.updateCourse(course)
    }

    fun updateBookMark(bookMark: BookMark) {
        repository.updateBookMark(bookMark)
    }

    fun deleteNote(note: Note) {
        repository.deleteNote(note)
    }

    fun deleteCourse(course: Course) {
        repository.deleteCourse(course)
    }

    fun deleteBookMark(bookMark: BookMark) {
        repository.deleteBookMark(bookMark)
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