package com.certified.notes.util

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.certified.notes.model.*
import com.certified.notes.room.NotesDaoKt
import com.certified.notes.room.NotesDatabaseKt
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.random.Random

class RepositoryKt(application: Application) {

    private val mNotesDao: NotesDaoKt
    val allNotes: LiveData<List<Note>>
    val allHomeNotes: LiveData<List<Note>>
    val allCourses: LiveData<List<Course>>
    val allHomeCourses: LiveData<List<Course>>
    val allTodos: LiveData<List<Todo>>
    val allBookMarks: LiveData<List<BookMark>>
    val allNoteIds: LiveData<List<Int>>
    val allCourseUnits: LiveData<List<Int>>
    val allCourseCreditPoints: LiveData<List<Int>>
    val user: LiveData<User>

    companion object {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
    }

    init {
        val database = NotesDatabaseKt.getInstance(application)
        mNotesDao = database!!.mNotesDao()
        allNotes = mNotesDao.getAllNotes()
        allHomeNotes = mNotesDao.getAllHomeNotes()
        allCourses = mNotesDao.getAllCourses()
        allHomeCourses = mNotesDao.getAllHomeCourses()
        allTodos = mNotesDao.getAllTodos()
        allBookMarks = mNotesDao.getAllBookMarks()
        allNoteIds = mNotesDao.getNoteIds()
        allCourseUnits = mNotesDao.getCourseUnits()
        allCourseCreditPoints = mNotesDao.getCourseCreditPoints()
        user = mNotesDao.getUser()
    }

    fun insertNote(note: Note) {
        executor.execute { mNotesDao.insertNote(note) }
    }

    fun insertCourse(course: Course) {
        executor.execute { mNotesDao.insertCourse(course) }
    }

    fun insertTodo(todo: Todo) {
        executor.execute { mNotesDao.insertTodo(todo) }
    }

    fun insertBookMark(bookMark: BookMark) {
        executor.execute { mNotesDao.insertBookMark(bookMark) }
    }

    fun updateNote(note: Note) {
        executor.execute { mNotesDao.updateNote(note) }
    }

    fun updateCourse(course: Course) {
        executor.execute { mNotesDao.updateCourse(course) }
    }

    fun updateTodo(todo: Todo) {
        executor.execute { mNotesDao.updateTodo(todo) }
    }

    fun updateBookMark(bookMark: BookMark) {
        executor.execute { mNotesDao.updateBookMark(bookMark) }
    }

    fun updateUser(user: User) {
        executor.execute { mNotesDao.updateUser(user) }
    }

    fun deleteNote(note: Note) {
        executor.execute { mNotesDao.deleteNote(note) }
    }

    fun deleteCourse(course: Course) {
        executor.execute { mNotesDao.deleteCourse(course) }
    }

    fun deleteTodo(todo: Todo) {
        executor.execute { mNotesDao.deleteTodo(todo) }
    }

    fun deleteBookMark(bookMark: BookMark) {
        executor.execute { mNotesDao.deleteBookMark(bookMark) }
    }

    fun deleteAllNotes() {
        NotesDatabaseKt.databaseWriteExecutor.execute { mNotesDao.deleteAllNotes() }
    }

    fun deleteAllCourses() {
        NotesDatabaseKt.databaseWriteExecutor.execute { mNotesDao.deleteAllCourses() }
    }

    fun deleteAllTodos() {
        NotesDatabaseKt.databaseWriteExecutor.execute { mNotesDao.deleteAllTodos() }
    }

    fun deleteAllBookMarks() {
        NotesDatabaseKt.databaseWriteExecutor.execute { mNotesDao.deleteAllBookMarks() }
    }

    fun deleteCompletedTodos() {
        executor.execute { mNotesDao.deleteCompletedTodos() }
    }

    fun getCourseCode(courseTitle: String): String {
        return try {
            executor.submit<String> { mNotesDao.getCourseCode(courseTitle) }.get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
            ""
        } catch (e: InterruptedException) {
            e.printStackTrace()
            ""
        }
    }

    fun getCourseTitle(courseCode: String): String {
        return try {
            executor.submit<String> { mNotesDao.getCourseTitle(courseCode) }.get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
            ""
        } catch (e: InterruptedException) {
            e.printStackTrace()
            ""
        }
    }

    fun deleteBookMarkedNote(noteId: Int) {
        try {
            executor.submit { mNotesDao.deleteBookMarkedNote(noteId) }.get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun getBookMarkAt(noteId: Int): LiveData<List<BookMark>>? {
        return try {
            executor.submit(Callable { mNotesDao.getBookMarkAt(noteId) }).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
            null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getNotesAt(courseCode: String): LiveData<List<Note>>? {
        return try {
            executor.submit(Callable { mNotesDao.getNotesAt(courseCode) }).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
            null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getDeletableNotes(noCourse: String): LiveData<List<Note>>? {
        return try {
            executor.submit(Callable { mNotesDao.getDeletableNotes(noCourse) }).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
            null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getDeletableBookmarks(noCourse: String): LiveData<List<BookMark>>? {
        return try {
            executor.submit(Callable { mNotesDao.getDeletableBookmarks(noCourse) }).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
            null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    private val seed = System.currentTimeMillis()
    val randomNotes: LiveData<List<Note>> = Transformations.map(mNotesDao.getAllHomeNotes()) {
        it.shuffled(Random(seed))
    }
}