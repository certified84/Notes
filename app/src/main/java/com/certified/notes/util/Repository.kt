package com.certified.notes.util

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.certified.notes.model.*
import com.certified.notes.room.NotesDao
import com.certified.notes.room.NotesDatabase
import java.util.*
import java.util.concurrent.ExecutionException
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

    init {
        val database = NotesDatabase.getInstance(application)
        noteDao = database.noteDao()
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

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun insertCourse(course: Course) {
        noteDao.insertCourse(course)
    }

    suspend fun insertTodo(todo: Todo) {
        noteDao.insertTodo(todo)
    }

    suspend fun insertBookMark(bookMark: BookMark) {
        noteDao.insertBookMark(bookMark)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    suspend fun updateCourse(course: Course) {
        noteDao.updateCourse(course)
    }

    suspend fun updateTodo(todo: Todo) {
        noteDao.updateTodo(todo)
    }

    suspend fun updateBookMark(bookMark: BookMark) {
        noteDao.updateBookMark(bookMark)
    }

    suspend fun updateUser(user: User) {
        noteDao.updateUser(user)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun deleteCourse(course: Course) {
        noteDao.deleteCourse(course)
    }

    suspend fun deleteTodo(todo: Todo) {
        noteDao.deleteTodo(todo)
    }

    suspend fun deleteBookMark(bookMark: BookMark) {
        noteDao.deleteBookMark(bookMark)
    }

    fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }

    fun deleteAllCourses() {
        noteDao.deleteAllCourses()
    }

    fun deleteAllTodos() {
        noteDao.deleteAllTodos()
    }

    fun deleteAllBookMarks() {
        noteDao.deleteAllBookMarks()
    }

    fun deleteCompletedTodos() {
        noteDao.deleteCompletedTodos()
    }

    fun deleteBookMarkedNote(noteId: Int) {
        try {
            noteDao.deleteBookMarkedNote(noteId)
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun getCourseCode(courseTitle: String): LiveData<String>? {
        return try {
            noteDao.getCourseCode(courseTitle)
        } catch (e: ExecutionException) {
            e.printStackTrace()
            null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

        fun getCourseTitle(courseCode: String): LiveData<String>? {
            return try {
                noteDao.getCourseTitle(courseCode)
            } catch (e: ExecutionException) {
                e.printStackTrace()
                null
            } catch (e: InterruptedException) {
                e.printStackTrace()
                null
            }
        }

        fun getBookMarkWith(noteId: Int): LiveData<BookMark>? {
            return try {
                noteDao.getBookMarkWith(noteId)
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
                noteDao.getNotesAt(courseCode)
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
                noteDao.getDeletableNotes(noCourse)
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
                noteDao.getDeletableBookmarks(noCourse)
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
                noteDao.searchNotes(searchQuery)
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
                noteDao.searchCourses(searchQuery)
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
                noteDao.searchBookmarks(searchQuery)
            } catch (e: ExecutionException) {
                e.printStackTrace()
                null
            } catch (e: InterruptedException) {
                e.printStackTrace()
                null
            }
        }
    }