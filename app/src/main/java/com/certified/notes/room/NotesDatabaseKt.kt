package com.certified.notes.room

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.certified.notes.model.*
import com.certified.notes.util.Converters
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(
    entities = [Note::class, Course::class, Todo::class, BookMark::class, User::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NotesDatabaseKt : RoomDatabase() {
    abstract fun mNotesDao(): NotesDaoKt

    companion object {
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)
        private var instance: NotesDatabaseKt? = null
        private val roomCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                val note1 = Note(
                    "MTS 315",
                    "Dynamic intent resolution",
                    "Wow, intents allow components to be resolved at runtime"
                )
                val note2 =
                    Note("CPE 301", "Parameters", "Leverage variable-length parameter lists?")
                val note3 = Note(
                    "EEE 301",
                    "Delegating intents",
                    "PendingIntents are powerful; they delegate much more than just a component invocation"
                )
                val note4 = Note(
                    "AGE 301",
                    "Compiler options",
                    "The -jar option isn't compatible with with the -cp option"
                )
                val note5 = Note(
                    "EEE 305",
                    "Serialization",
                    "Remember to include SerialVersionUID to assure version compatibility"
                )
                val note6 = Note(
                    "PMT 301",
                    "Service default threads",
                    "Did you know that by default an Android Service will tie up the UI thread?"
                )
                val note7 = Note(
                    "EEE 311",
                    "Anonymous classes",
                    "Anonymous classes simplify implementing one-use types"
                )
                val note8 = Note(
                    "EEE 307",
                    "Long running operations",
                    "Foreground Services can be tied to a notification icon"
                )

                val course1 = Course("AGE 301", "Engineering Statistics", 2, 0, "F", 0)
                val course2 = Course("CPE 301", "Digital System Design with VHDL", 3, 0, "F", 0)
                val course3 = Course("EEE 301", "Measurement and Instrumentation", 2, 0, "F", 0)
                val course4 = Course("EEE 303", "Electronics Engineering I", 3, 0, "F", 0)
                val course5 = Course("EEE 305", "Electromagnetic Field Theory", 2, 0, "F", 0)
                val course6 = Course("EEE 307", "Electric Machines I", 3, 0, "F", 0)
                val course7 = Course("EEE 311", "Electric Circuit Theory I", 3, 0, "F", 0)
                val course8 = Course("EEE 313", "Electrical/Electronic Laboratory I", 1, 0, "F", 0)
                val course9 = Course("MTS 315", "Engineering Mathematics I", 3, 0, "F", 0)
                val course10 = Course("PMT 301", "Introduction to Entrepreneurship", 2, 0, "F", 0)

                val todo1 = Todo("Make sure to complete the To-dos below", false)
                val todo2 = Todo(
                    "Learn LiveData, Room, ViewModel, WorkManager, Notification, Paging library",
                    false
                )
                val todo3 = Todo(
                    "The above should be learnt completely in order to pass the AAD exam",
                    false
                )
                val todo4 = Todo("Stop all projects that aren't related to the AAD exam", false)
                val todo5 =
                    Todo("Visit the code labs and study guide for the AAD exam frequently", false)
                val todo6 = Todo(
                    "Practice, Practice and keep practicing. Obtaining the AAD certificate should be the only priority for now",
                    false
                )
                val todo7 = Todo("Make sure you pass the exam at the first try!!!", false)
                val todo8 = Todo("Make sure to complete the To-dos above", false)

                val user =
                    User("Enter name", "Enter school", "Enter department", "Select level", null)

                databaseWriteExecutor.execute {
                    val notesDao = instance!!.mNotesDao()

//                    notesDao.deleteAllNotes()
                    notesDao.insertNote(note1)
                    notesDao.insertNote(note2)
                    notesDao.insertNote(note3)
                    notesDao.insertNote(note4)
                    notesDao.insertNote(note5)
                    notesDao.insertNote(note6)
                    notesDao.insertNote(note7)
                    notesDao.insertNote(note8)

//                    notesDao.deleteAllCourses()
                    notesDao.insertCourse(course1)
                    notesDao.insertCourse(course2)
                    notesDao.insertCourse(course3)
                    notesDao.insertCourse(course4)
                    notesDao.insertCourse(course5)
                    notesDao.insertCourse(course6)
                    notesDao.insertCourse(course7)
                    notesDao.insertCourse(course8)
                    notesDao.insertCourse(course9)
                    notesDao.insertCourse(course10)

//                    notesDao.deleteAllTodos()
                    notesDao.insertTodo(todo1)
                    notesDao.insertTodo(todo2)
                    notesDao.insertTodo(todo3)
                    notesDao.insertTodo(todo4)
                    notesDao.insertTodo(todo5)
                    notesDao.insertTodo(todo6)
                    notesDao.insertTodo(todo7)
                    notesDao.insertTodo(todo8)

//                    notesDao.insertUser(user)
                }
            }
        }

        @Synchronized
        fun getInstance(context: Context): NotesDatabaseKt? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabaseKt::class.java,
                    "notes_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()
            }
            return instance
        }
    }
}