package com.certified.notes;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.certified.notes.model.BookMark;
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
    private LiveData<List<Note>> allHomeNotes;
    private LiveData<List<Course>> allCourses;
    private LiveData<List<Course>> allHomeCourses;
    private LiveData<List<Todo>> allTodos;
    private LiveData<List<BookMark>> allBookMarks;
    private LiveData<List<Integer>> allNoteIds;

    public Repository(Application application) {
        NotesDatabase database = NotesDatabase.getInstance(application);
        mNotesDao = database.mNotesDao();
        allNotes = mNotesDao.getAllNotes();
        allHomeNotes = mNotesDao.getAllHomeNotes();
        allCourses = mNotesDao.getAllCourses();
        allHomeCourses = mNotesDao.getAllHomeCourses();
        allTodos = mNotesDao.getAllTodos();
        allBookMarks = mNotesDao.getAllBookMarks();
        allNoteIds = mNotesDao.getNoteIds();
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

    public void insertBookMark(BookMark bookMark) {
        executor.execute(() -> mNotesDao.insertBookMark(bookMark));
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

    public void updateBookMark(BookMark bookMark) {
        executor.execute(() -> mNotesDao.updateBookMark(bookMark));
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

    public void deleteBookMark(BookMark bookMark) {
        executor.execute(() -> mNotesDao.deleteBookMark(bookMark));
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

    public void deleteAllBookMarks() {
        NotesDatabase.databaseWriteExecutor.execute(() -> mNotesDao.deleteAllBookMarks());
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
        executor.execute(() -> mNotesDao.deleteCompletedTodos());
    }

    public String getCourseCode(String courseTitle) {
        try {
            return executor.submit(() -> mNotesDao.getCourseCode(courseTitle)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void deleteBookMarkedNote(int noteId) {
        try {
            executor.submit(() -> mNotesDao.deleteBookMarkedNote(noteId)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public LiveData<List<BookMark>> getBookMarkAt(int noteId){
        try {
            return executor.submit(() -> mNotesDao.getBookMarkAt(noteId)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

//    private long seed = System.currentTimeMillis();
//    val mItemList: LiveData<MutableList<Note>> = Transformations.map(mNotesDao.getAllHomeNotes()) {
//        it.shuffled(Random(seed))
//    }
//    public LiveData<List<Note>> randomisedNotes() {
//        Transformations.map(mNotesDao.getAllHomeNotes());
//        return allHomeNotes.shu
//    }
}