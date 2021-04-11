package com.certified.notes.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.certified.notes.model.*
import com.certified.notes.util.Repository

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(application)

    val allNotes: LiveData<List<Note>> = repository.allNotes
    val randomNotes: LiveData<List<Note>> = repository.randomNotes
    val allCourses: LiveData<List<Course>> = repository.allCourses
    val randomCourses: LiveData<List<Course>> = repository.randomCourses
    val allTodos: LiveData<List<Todo>> = repository.allTodos
    val allBookMarks: LiveData<List<BookMark>> = repository.allBookMarks
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

    fun insertBookMark(bookMark: BookMark) {
        repository.insertBookMark(bookMark)
    }

    fun updateNote(note: Note) {
        repository.updateNote(note)
    }

    fun updateCourse(course: Course) {
        repository.updateCourse(course)
    }

    fun updateTodo(todo: Todo) {
        repository.updateTodo(todo)
    }

    fun updateBookMark(bookMark: BookMark) {
        repository.updateBookMark(bookMark)
    }

    fun updateUser(user: User) {
        repository.updateUser(user)
    }

    fun deleteNote(note: Note) {
        repository.deleteNote(note)
    }

    fun deleteCourse(course: Course) {
        repository.deleteCourse(course)
    }

    fun deleteTodo(todo: Todo) {
        repository.deleteTodo(todo)
    }

    fun deleteBookMark(bookMark: BookMark) {
        repository.deleteBookMark(bookMark)
    }

    fun deleteAllNotes() {
        repository.deleteAllNotes()
    }

    fun deleteAllCourses() {
        repository.deleteAllCourses()
    }

    fun deleteAllTodos() {
        repository.deleteAllTodos()
    }

    fun deleteAllBookMarks() {
        repository.deleteAllBookMarks()
    }

    fun deleteCompletedTodos() {
        repository.deleteCompletedTodos()
    }

    fun getCourseCode(courseTitle: String): String {
        return repository.getCourseCode(courseTitle)
    }

    fun getCourseTitle(courseCode: String): String {
        return repository.getCourseTitle(courseCode)
    }

    fun deleteBookMarkedNote(noteId: Int) {
        repository.deleteBookMarkedNote(noteId)
    }

    fun getBookMarkAt(noteId: Int): LiveData<List<BookMark>>? {
        return repository.getBookMarkAt(noteId)
    }

    fun getNotesAt(courseCode: String): LiveData<List<Note>>? {
        return repository.getNotesAt(courseCode)
    }

    fun getDeletableNotes(noCourse: String): LiveData<List<Note>>? {
        return repository.getDeletableNotes(noCourse)
    }

    fun getDeletableBookmarks(noCourse: String): LiveData<List<BookMark>>? {
        return repository.getDeletableBookmarks(noCourse)
    }


    fun searchNotes(searchQuery: String?): LiveData<List<Note?>?>? {
        return repository.searchNotes(searchQuery)
    }

    fun searchCourses(searchQuery: String?): LiveData<List<Course?>?>? {
        return repository.searchCourses(searchQuery)
    }

    fun searchBookmarks(searchQuery: String?): LiveData<List<BookMark?>?>? {
        return repository.searchBookmarks(searchQuery)
    }
}