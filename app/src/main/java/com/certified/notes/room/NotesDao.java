package com.certified.notes.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.certified.notes.model.BookMark;
import com.certified.notes.model.Course;
import com.certified.notes.model.Note;
import com.certified.notes.model.Todo;
import com.certified.notes.model.User;

import java.util.List;

@Dao
public interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourse(Course course);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTodo(Todo todo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookMark(BookMark bookMark);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Update
    void updateNote(Note note);

    @Update
    void updateCourse(Course course);

    @Update
    void updateTodo(Todo todo);

    @Update
    void updateBookMark(BookMark bookMark);

    @Update
    void updateUser(User user);

    @Delete
    void deleteNote(Note note);

    @Delete
    void deleteCourse(Course course);

    @Delete
    void deleteTodo(Todo todo);

    @Delete
    void deleteBookMark(BookMark bookMark);

    @Query("DELETE FROM note_table ")
    void deleteAllNotes();

    @Query("DELETE FROM course_table ")
    void deleteAllCourses();

    @Query("DELETE FROM todo_table ")
    void deleteAllTodos();

    @Query("DELETE FROM bookmark_table ")
    void deleteAllBookMarks();

    @Query("SELECT * FROM note_table ORDER BY note_title ASC")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM note_table ORDER BY note_title ASC LIMIT 2")
    LiveData<List<Note>> getAllHomeNotes();

    @Query("SELECT * FROM course_table ORDER BY course_code ASC")
    LiveData<List<Course>> getAllCourses();

    @Query("SELECT * FROM course_table ORDER BY course_code ASC LIMIT 2")
    LiveData<List<Course>> getAllHomeCourses();

    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    LiveData<List<Todo>> getAllTodos();

    @Query("SELECT * FROM bookmark_table ORDER BY id ASC")
    LiveData<List<BookMark>> getAllBookMarks();

    @Query("SELECT * FROM user_table")
    LiveData<User> getUser();

    @Query("DELETE FROM bookmark_table WHERE note_id = :noteId")
    void deleteBookMarkedNote(int noteId);

    @Query("DELETE FROM todo_table WHERE done == 1")
    void deleteCompletedTodos();

    @Query("SELECT * FROM bookmark_table WHERE note_id = :noteId")
    LiveData<List<BookMark>> getBookMarkAt(int noteId);

    @Query("SELECT * FROM note_table WHERE course_code = :course_code")
    LiveData<List<Note>> getNotesAt(String course_code);

    @Query("SELECT course_code FROM course_table WHERE course_title = :courseTitle")
    String getCourseCode(String courseTitle);

    @Query("SELECT course_title FROM course_table WHERE course_code = :courseCode")
    String getCourseTitle(String courseCode);

    @Query("SELECT note_id FROM bookmark_table")
    LiveData<List<Integer>> getNoteIds();

    @Query("SELECT course_unit FROM course_table")
    LiveData<List<Integer>> getCourseUnits();

    @Query("SELECT course_credit_point FROM course_table")
    LiveData<List<Integer>> getCourseCreditPoints();

    @Query("SELECT * FROM note_table WHERE course_code != :noCourse")
    LiveData<List<Note>> getDeletableNotes(String noCourse);

    @Query("SELECT * FROM bookmark_table WHERE course_code != :noCourse")
    LiveData<List<BookMark>> getDeletableBookmarks(String noCourse);

    @Query("SELECT * FROM note_table WHERE note_title LIKE :searchQuery OR note_content LIKE :searchQuery OR course_code LIKE :searchQuery ORDER BY note_title ASC")
    LiveData<List<Note>> searchNotes(String searchQuery);

    @Query("SELECT * FROM course_table WHERE course_code LIKE :searchQuery OR course_title LIKE :searchQuery ORDER BY course_code ASC")
    LiveData<List<Course>> searchCourses(String searchQuery);

    @Query("SELECT * FROM bookmark_table WHERE note_title LIKE :searchQuery OR note_content LIKE :searchQuery OR course_code LIKE :searchQuery ORDER BY id ASC")
    LiveData<List<BookMark>> searchBookmarks(String searchQuery);
}