package com.certified.notes.view.Notes

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

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application)

    val allNotes: LiveData<List<Note>> = repository.allNotes
    val allCourses: LiveData<List<Course>> = repository.allCourses

    fun insertBookMark(bookMark: BookMark) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertBookMark(bookMark)
        }
    }

    fun insertNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNote(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
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

    fun deleteAllNotes() {
        repository.deleteAllNotes()
    }

    fun deleteAllBookMarks() {
        repository.deleteAllBookMarks()
    }

    fun deleteBookMarkedNote(noteId: Int) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteBookMarkedNote(noteId) }
    }

    fun getCourseCode(courseTitle: String) = repository.getCourseCode(courseTitle)

    fun getCourseTitle(courseCode: String): LiveData<String>? {
        return repository.getCourseTitle(courseCode)
    }

    fun getBookMarkWith(noteId: Int): LiveData<BookMark>? {
        return repository.getBookMarkWith(noteId)
    }

    fun searchNotes(searchQuery: String?): LiveData<List<Note?>?>? {
        return repository.searchNotes(searchQuery)
    }
}