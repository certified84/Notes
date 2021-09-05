package com.certified.notes.ui.Notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.certified.notes.model.BookMark
import com.certified.notes.model.Course
import com.certified.notes.model.Note
import com.certified.notes.model.Todo
import com.certified.notes.util.Repository

class NotesViewModel(application: Application): AndroidViewModel(application) {
    private val repository = Repository(application)

    val allNotes: LiveData<List<Note>> = repository.allNotes
    val allCourses: LiveData<List<Course>> = repository.allCourses

    fun insertBookMark(bookMark: BookMark) {
        repository.insertBookMark(bookMark)
    }

    fun updateNote(note: Note) {
        repository.updateNote(note)
    }

    fun updateBookMark(bookMark: BookMark) {
        repository.updateBookMark(bookMark)
    }

    fun deleteNote(note: Note) {
        repository.deleteNote(note)
    }

    fun deleteAllNotes() {
        repository.deleteAllNotes()
    }

    fun deleteAllBookMarks() {
        repository.deleteAllBookMarks()
    }

    fun deleteBookMarkedNote(noteId: Int) {
        repository.deleteBookMarkedNote(noteId)
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

    fun searchNotes(searchQuery: String?): LiveData<List<Note?>?>? {
        return repository.searchNotes(searchQuery)
    }
}