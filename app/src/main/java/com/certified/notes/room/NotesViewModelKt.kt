package com.certified.notes.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.certified.notes.model.*
import com.certified.notes.util.Repository
import com.certified.notes.util.RepositoryKt

class NotesViewModelKt(application: Application) : AndroidViewModel(application) {
    
    private val mRepository = RepositoryKt(application)

    private val allNotes: LiveData<List<Note>>
    private val allHomeNotes: LiveData<List<Note>>
    private val allCourses: LiveData<List<Course>>
    private val allHomeCourses: LiveData<List<Course>>
    private val allTodos: LiveData<List<Todo>>
    private val allBookMarks: LiveData<List<BookMark>>
    private val allNoteIds: LiveData<List<Int>>
    private val allCourseUnits: LiveData<List<Int>>
    private val allCourseCreditPoints: LiveData<List<Int>>
    private val user: LiveData<User>

    init {
        allNotes = mRepository.allNotes
        allHomeNotes = mRepository.allHomeNotes
        allCourses = mRepository.allCourses
        allHomeCourses = mRepository.allHomeCourses
        allTodos = mRepository.allTodos
        allBookMarks = mRepository.allBookMarks
        allNoteIds = mRepository.allNoteIds
        allCourseUnits = mRepository.allCourseUnits
        allCourseCreditPoints = mRepository.allCourseCreditPoints
        user = mRepository.user
    }

    fun insertNote(note: Note) {
        mRepository.insertNote(note)
    }

    fun insertCourse(course: Course) {
        mRepository.insertCourse(course)
    }

    fun insertTodo(todo: Todo) {
        mRepository.insertTodo(todo)
    }

    fun insertBookMark(bookMark: BookMark) {
        mRepository.insertBookMark(bookMark)
    }

    fun updateNote(note: Note) {
        mRepository.updateNote(note)
    }

    fun updateCourse(course: Course) {
        mRepository.updateCourse(course)
    }

    fun updateTodo(todo: Todo) {
        mRepository.updateTodo(todo)
    }

    fun updateBookMark(bookMark: BookMark) {
        mRepository.updateBookMark(bookMark)
    }

    fun updateUser(user: User) {
        mRepository.updateUser(user)
    }

    fun deleteNote(note: Note) {
        mRepository.deleteNote(note)
    }

    fun deleteCourse(course: Course) {
        mRepository.deleteCourse(course)
    }

    fun deleteTodo(todo: Todo) {
        mRepository.deleteTodo(todo)
    }

    fun deleteBookMark(bookMark: BookMark) {
        mRepository.deleteBookMark(bookMark)
    }

    fun deleteAllNotes() {
        mRepository.deleteAllNotes()
    }

    fun deleteAllCourses() {
        mRepository.deleteAllCourses()
    }

    fun deleteAllTodos() {
        mRepository.deleteAllTodos()
    }

    fun deleteAllBookMarks() {
        mRepository.deleteAllBookMarks()
    }

    fun deleteCompletedTodos() {
        mRepository.deleteCompletedTodos()
    }

    fun getCourseCode(courseTitle: String): String {
        return mRepository.getCourseCode(courseTitle)
    }

    fun getCourseTitle(courseCode: String): String {
        return mRepository.getCourseTitle(courseCode)
    }

    fun deleteBookMarkedNote(noteId: Int) {
        mRepository.deleteBookMarkedNote(noteId)
    }

    fun getBookMarkAt(noteId: Int): LiveData<List<BookMark>>? {
        return mRepository.getBookMarkAt(noteId)
    }

    fun getNotesAt(courseCode: String): LiveData<List<Note>>? {
        return mRepository.getNotesAt(courseCode)
    }

    fun getDeletableNotes(noCourse: String): LiveData<List<Note>>? {
        return mRepository.getDeletableNotes(noCourse)
    }

    fun getDeletableBookmarks(noCourse: String): LiveData<List<BookMark>>? {
        return mRepository.getDeletableBookmarks(noCourse)
    }
}