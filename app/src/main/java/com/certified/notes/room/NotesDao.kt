package com.certified.notes.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.certified.notes.model.*

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCourse(course: Course)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodo(todo: Todo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookMark(bookMark: BookMark)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateNote(note: Note)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateCourse(course: Course)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateTodo(todo: Todo)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateBookMark(bookMark: BookMark)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteNote(note: Note)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Delete
    suspend fun deleteBookMark(bookMark: BookMark)

    @Query("DELETE FROM note_table ")
    fun deleteAllNotes()

    @Query("DELETE FROM course_table ")
    fun deleteAllCourses()

    @Query("DELETE FROM todo_table ")
    fun deleteAllTodos()

    @Query("DELETE FROM bookmark_table ")
    fun deleteAllBookMarks()

    @Query("SELECT * FROM note_table ORDER BY note_title ASC")
    fun getAllNotes(): LiveData<List<Note>>

//    @Query("SELECT * FROM note_table ORDER BY note_title ASC LIMIT 2")
//    fun getAllHomeNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM course_table ORDER BY course_code ASC")
    fun getAllCourses(): LiveData<List<Course>>

    @Query("SELECT * FROM course_table ORDER BY course_code ASC LIMIT 2")
    fun getAllHomeCourses(): LiveData<List<Course>>

    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    fun getAllTodos(): LiveData<List<Todo>>

    @Query("SELECT * FROM bookmark_table ORDER BY id ASC")
    fun getAllBookMarks(): LiveData<List<BookMark>>

    @Query("SELECT * FROM user_table")
    fun getUser(): LiveData<User>

    @Query("DELETE FROM bookmark_table WHERE note_id = :noteId")
    fun deleteBookMarkedNote(noteId: Int)

    @Query("DELETE FROM todo_table WHERE isDone == 1")
    fun deleteCompletedTodos()

    @Query("SELECT * FROM bookmark_table WHERE note_id = :noteId")
    fun getBookMarkAt(noteId: Int): LiveData<List<BookMark>>

    @Query("SELECT * FROM note_table WHERE course_code = :course_code")
    fun getNotesAt(course_code: String): LiveData<List<Note>>

    @Query("SELECT course_code FROM course_table WHERE course_title = :courseTitle")
    fun getCourseCode(courseTitle: String): String

    @Query("SELECT course_title FROM course_table WHERE course_code = :courseCode")
    fun getCourseTitle(courseCode: String): String

    @Query("SELECT note_id FROM bookmark_table")
    fun getNoteIds(): LiveData<List<Int>>

    @Query("SELECT course_unit FROM course_table")
    fun getCourseUnits(): LiveData<List<Int>>

    @Query("SELECT course_credit_point FROM course_table")
    fun getCourseCreditPoints(): LiveData<List<Int>>

    @Query("SELECT * FROM note_table WHERE course_code != :noCourse")
    fun getDeletableNotes(noCourse: String): LiveData<List<Note>>

    @Query("SELECT * FROM bookmark_table WHERE course_code != :noCourse")
    fun getDeletableBookmarks(noCourse: String): LiveData<List<BookMark>>

    @Query("SELECT * FROM note_table WHERE note_title LIKE :searchQuery OR note_content LIKE :searchQuery OR course_code LIKE :searchQuery ORDER BY note_title ASC")
    fun searchNotes(searchQuery: String?): LiveData<List<Note?>?>?

    @Query("SELECT * FROM course_table WHERE course_code LIKE :searchQuery OR course_title LIKE :searchQuery ORDER BY course_code ASC")
    fun searchCourses(searchQuery: String?): LiveData<List<Course?>?>?

    @Query("SELECT * FROM bookmark_table WHERE note_title LIKE :searchQuery OR note_content LIKE :searchQuery OR course_code LIKE :searchQuery ORDER BY id ASC")
    fun searchBookmarks(searchQuery: String?): LiveData<List<BookMark?>?>?
}