package com.certified.notes;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.certified.notes.model.Course;
import com.certified.notes.model.Note;
import com.certified.notes.model.Todo;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    public static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private NotesDao mNotesDao;
    private LiveData<List<Note>> allNotes;
    private LiveData<List<Course>> allCourses;
    private LiveData<List<Todo>> allTodos;

    public Repository(Application application) {
        NotesDatabase database = NotesDatabase.getInstance(application);
        mNotesDao = database.mNotesDao();
        allNotes = mNotesDao.getAllNotes();
        allCourses = mNotesDao.getAllCourses();
        allTodos = mNotesDao.getAllTodos();
    }

    public void insertNote(Note note) {
        executor.execute(() -> mNotesDao.insertNote(note));
    }

    public void insertCourse(Course course) {
        executor.execute(() -> mNotesDao.insertCourse(course));
    }

    public void insertTodo(Todo todo) {
        executor.execute(() -> mNotesDao.insertTodo(todo));
    }

    public void updateNote(Note note) {
        executor.execute(() -> mNotesDao.updateNote(note));
    }

    public void updateCourse(Course course) {
        executor.execute(() -> mNotesDao.updateCourse(course));
    }

    public void updateTodo(Todo todo) {
        executor.execute(() -> mNotesDao.updateTodo(todo));
    }

    public void deleteNote(Note note) {
        executor.execute(() -> mNotesDao.deleteNote(note));
    }

    public void deleteCourse(Course course) {
        executor.execute(() -> mNotesDao.deleteCourse(course));
    }

    public void deleteTodo(Todo todo) {
        executor.execute(() -> mNotesDao.deleteTodo(todo));
    }

    public void deleteAllNotes() {
        NotesDatabase.databaseWriteExecutor.execute(() -> mNotesDao.deleteAllNotes());
    }

    public void deleteAllCourses() {
        NotesDatabase.databaseWriteExecutor.execute(() -> mNotesDao.deleteAllCourses());
    }

    public void deleteAllTodos() {
        NotesDatabase.databaseWriteExecutor.execute(() -> mNotesDao.deleteAllTodos());
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }

    public LiveData<List<Todo>> getAllTodos() {
        return allTodos;
    }

    public String getCourseCode(String courseTitle) {
        try {
            return executor.submit(() -> mNotesDao.getCourseCode(courseTitle)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return "";
        }
    }
}