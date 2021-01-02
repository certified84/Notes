package com.certified.notes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.certified.notes.model.BookMark;
import com.certified.notes.model.Course;
import com.certified.notes.model.Note;
import com.certified.notes.model.Todo;

import java.util.List;

public class NotesViewModel extends AndroidViewModel {

    private Repository mRepository;

    private LiveData<List<Note>> allNotes;
    private LiveData<List<Note>> allHomeNotes;
    private LiveData<List<Course>> allCourses;
    private LiveData<List<Course>> allHomeCourses;
    private LiveData<List<Todo>> allTodos;
    private LiveData<List<BookMark>> allBookMarks;
    private LiveData<List<Integer>> allNoteIds;

    public NotesViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        allNotes = mRepository.getAllNotes();
        allHomeNotes = mRepository.getAllHomeNotes();
        allCourses = mRepository.getAllCourses();
        allHomeCourses = mRepository.getAllHomeCourses();
        allTodos = mRepository.getAllTodos();
        allBookMarks = mRepository.getAllBookMarks();
        allNoteIds = mRepository.getAllNoteIds();
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

    public void insertBookMark(BookMark bookMark) {
        mRepository.insertBookMark(bookMark);
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

    public void updateBookMark(BookMark bookMark) {
        mRepository.updateBookMark(bookMark);
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

    public void deleteBookMark(BookMark bookMark) {
        mRepository.deleteBookMark(bookMark);
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

    public void deleteAllBookMarks() {
        mRepository.deleteAllBookMarks();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<List<Note>> getAllHomeNotes() {
        return allHomeNotes;
    }

    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }

    public LiveData<List<Course>> getAllHomeCourses() {
        return allHomeCourses;
    }

    public LiveData<List<Todo>> getAllTodos() {
        return allTodos;
    }

    public LiveData<List<BookMark>> getAllBookMarks() {
        return allBookMarks;
    }

    public LiveData<List<Integer>> getAllNoteIds() {
        return allNoteIds;
    }

    public void deleteCompletedTodos() {
        mRepository.deleteCompletedTodos();
    }

    public String getCourseCode(String courseTitle) {
        return mRepository.getCourseCode(courseTitle);
    }

    public void deleteBookMarkedNote(int noteId) {
        mRepository.deleteBookMarkedNote(noteId);
    }

    public LiveData<List<BookMark>> getBookMarkAt(int noteId) {
        return mRepository.getBookMarkAt(noteId);
    }
}