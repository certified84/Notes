package com.certified.notes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.certified.notes.model.Course;
import com.certified.notes.model.Note;
import com.certified.notes.model.Todo;

import java.util.List;

public class NotesViewModel extends AndroidViewModel {

    private LiveData<List<Note>> allNotes;
    private LiveData<List<Course>> allCourses;
    private LiveData<List<Todo>> allTodos;
    Repository mRepository;

    public NotesViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        allNotes = mRepository.getAllNotes();
        allCourses = mRepository.getAllCourses();
        allTodos = mRepository.getAllTodos();
    }

    public void insertNote(Note note) {
        mRepository.insertNote(note);
    }

    public void insertCourse(Course course) {
        mRepository.insertCourse(course);
    }

    public void insertTodo(Todo todo) {
        mRepository.insertTodo(todo);
    }

    public void updateNote(Note note) {
        mRepository.updateNote(note);
    }

    public void updateCourse(Course course) {
        mRepository.updateCourse(course);
    }

    public void updateTodo(Todo todo) {
        mRepository.updateTodo(todo);
    }

    public void deleteNote(Note note) {
        mRepository.deleteNote(note);
    }

    public void deleteCourse(Course course) {
        mRepository.deleteCourse(course);
    }

    public void deleteTodo(Todo todo) {
        mRepository.deleteTodo(todo);
    }

    public void deleteAllNotes() {
        mRepository.deleteAllNotes();
    }

    public void deleteAllCourses() {
        mRepository.deleteAllCourses();
    }

    public void deleteAllTodos() {
        mRepository.deleteAllTodos();
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
        return mRepository.getCourseCode(courseTitle);
    }
}
