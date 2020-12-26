package com.certified.notes;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.certified.notes.model.Course;
import com.certified.notes.model.Note;
import com.certified.notes.model.Todo;

import java.util.List;

@Dao
public interface NotesDao {

    @Insert()
    void insertNote(Note note);

    @Insert()
    void insertCourse(Course course);

    @Insert()
    void insertTodo(Todo todo);

    @Update
    void updateNote(Note note);

    @Update
    void updateCourse(Course course);

    @Update
    void updateTodo(Todo todo);

    @Delete
    void deleteNote(Note note);

    @Delete
    void deleteCourse(Course course);

    @Delete
    void deleteTodo(Todo todo);

    @Query("DELETE FROM note_table ")
    void deleteAllNotes();

    @Query("DELETE FROM course_table ")
    void deleteAllCourses();

    @Query("DELETE FROM todo_table ")
    void deleteAllTodos();

    @Query("SELECT * FROM note_table ORDER BY note_title ASC")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM course_table ORDER BY course_code ASC")
    LiveData<List<Course>> getAllCourses();

    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    LiveData<List<Todo>> getAllTodos();

    @Query("SELECT course_code FROM course_table WHERE course_title = :courseTitle")
    String getCourseCode(String courseTitle);
}
