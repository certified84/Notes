package com.certified.notes.ui.bookMarks

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

class BookMarksViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(application)

    private val _allBookMarks: LiveData<List<BookMark>> = repository.allBookMarks
    val allBookMarks: LiveData<List<BookMark>>
        get() = _allBookMarks

    val allCourses: LiveData<List<Course>> = repository.allCourses

    fun updateBookMark(bookMark: BookMark) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateBookMark(bookMark) }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
        }
    }

    fun deleteBookMark(bookMark: BookMark) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBookMark(bookMark)
        }
    }

    fun deleteAllBookMarks() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllBookMarks()
        }
    }

    fun getBookMarkWith(noteId: Int): LiveData<BookMark>? {
        return repository.getBookMarkWith(noteId)
    }

    fun getCourseCode(courseTitle: String) = repository.getCourseCode(courseTitle)
//        val courseCode = MutableLiveData<String>()
//        viewModelScope.launch(Dispatchers.IO) {
//            courseCode.postValue(repository.getCourseCode(courseTitle))
//        }
//        return courseCode.value ?: " "
//    }

    fun getCourseTitle(courseCode: String): LiveData<String>? {
        return repository.getCourseTitle(courseCode)
    }

    fun searchBookmarks(searchQuery: String?): LiveData<List<BookMark?>?>? {
        return repository.searchBookmarks(searchQuery)
    }
}