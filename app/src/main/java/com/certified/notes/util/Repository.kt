package com.certified.notes.util

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.certified.notes.model.*
import com.certified.notes.room.NotesDao
import com.certified.notes.room.NotesDatabase
import com.certified.notes.room.NotesDatabasekt
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.random.Random

class Repository(application: Application) {

    private val noteDao: NotesDao
    val allNotes: LiveData<List<Note>>
    val randomNotes: LiveData<List<Note>>
    val allCourses: LiveData<List<Course>>
    val randomCourses: LiveData<List<Course>>
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
        val database = NotesDatabasekt.getInstance(application)
        noteDao = database!!.mNotesDao()
        allNotes = noteDao.getAllNotes()
        allCourses = noteDao.getAllCourses()
        allTodos = noteDao.getAllTodos()
        allBookMarks = noteDao.getAllBookMarks()
        allNoteIds = noteDao.getNoteIds()
        allCourseUnits = noteDao.getCourseUnits()
        allCourseCreditPoints = noteDao.getCourseCreditPoints()
        user = noteDao.getUser()

        val seed = System.currentTimeMillis()
        randomNotes = Transformations.map(allNotes) {
            it.shuffled(Random(seed))
            val notes: MutableList<Note> = ArrayList()
            return@map if (it.isNotEmpty()) {
                if (it.size >= 5) {
                    for (i in 1..5) {
                        notes.add(it[i])
                    }
                    notes
                } else it
            } else null
        }

        randomCourses = Transformations.map(allCourses) {
            it.shuffled(Random(seed))
            val courses: MutableList<Course> = ArrayList()
            return@map if (it.isNotEmpty()) {
                if (it.size >= 5) {
                    for (i in 1..5) {
                        courses.add(it[i])
                    }
                    courses
                } else it
            } else null
        }
    }

    fun insertNote(note: Note) {
        executor.execute { noteDao.insertNote(note) }
    }

    fun insertCourse(course: Course) {
        executor.execute { noteDao.insertCourse(course) }
    }

    fun insertTodo(todo: Todo) {
        executor.execute { noteDao.insertTodo(todo) }
    }

    fun insertBookMark(bookMark: BookMark) {
        executor.execute { noteDao.insertBookMark(bookMark) }
    }

    fun updateNote(note: Note) {
        executor.execute { noteDao.updateNote(note) }
    }

    fun updateCourse(course: Course) {
        executor.execute { noteDao.updateCourse(course) }
    }

    fun updateTodo(todo: Todo) {
        executor.execute { noteDao.updateTodo(todo) }
    }

    fun updateBookMark(bookMark: BookMark) {
        executor.execute { noteDao.updateBookMark(bookMark) }
    }

    fun updateUser(user: User) {
        executor.execute { noteDao.updateUser(user) }
    }

    fun deleteNote(note: Note) {
        executor.execute { noteDao.deleteNote(note) }
    }

    fun deleteCourse(course: Course) {
        executor.execute { noteDao.deleteCourse(course) }
    }

    fun deleteTodo(todo: Todo) {
        executor.execute { noteDao.deleteTodo(todo) }
    }

    fun deleteBookMark(bookMark: BookMark) {
        executor.execute { noteDao.deleteBookMark(bookMark) }
    }

    fun deleteAllNotes() {
        NotesDatabase.databaseWriteExecutor.execute { noteDao.deleteAllNotes() }
    }

    fun deleteAllCourses() {
        NotesDatabase.databaseWriteExecutor.execute { noteDao.deleteAllCourses() }
    }

    fun deleteAllTodos() {
        NotesDatabase.databaseWriteExecutor.execute { noteDao.deleteAllTodos() }
    }

    fun deleteAllBookMarks() {
        NotesDatabase.databaseWriteExecutor.execute { noteDao.deleteAllBookMarks() }
    }

    fun deleteCompletedTodos() {
        executor.execute { noteDao.deleteCompletedTodos() }
    }

    fun getCourseCode(courseTitle: String): String {
        return try {
            executor.submit<String> { noteDao.getCourseCode(courseTitle) }.get()
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
            executor.submit<String> { noteDao.getCourseTitle(courseCode) }.get()
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
            executor.submit { noteDao.deleteBookMarkedNote(noteId) }.get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun getBookMarkAt(noteId: Int): LiveData<List<BookMark>>? {
        return try {
            executor.submit(Callable { noteDao.getBookMarkAt(noteId) }).get()
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
            executor.submit(Callable { noteDao.getNotesAt(courseCode) }).get()
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
            executor.submit(Callable { noteDao.getDeletableNotes(noCourse) }).get()
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
            executor.submit(Callable { noteDao.getDeletableBookmarks(noCourse) }).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
            null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun searchNotes(searchQuery: String?): LiveData<List<Note?>?>? {
        return try {
            executor.submit(Callable {
                noteDao.searchNotes(
                    searchQuery!!
                )
            }).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
            null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun searchCourses(searchQuery: String?): LiveData<List<Course?>?>? {
        return try {
            executor.submit(Callable {
                noteDao.searchCourses(
                    searchQuery
                )
            }).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
            null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun searchBookmarks(searchQuery: String?): LiveData<List<BookMark?>?>? {
        return try {
            executor.submit(Callable {
                noteDao.searchBookmarks(
                    searchQuery
                )
            }).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
            null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }
}