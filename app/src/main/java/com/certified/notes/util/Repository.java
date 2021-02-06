package com.certified.notes.util;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.certified.notes.model.BookMark;
import com.certified.notes.model.Course;
import com.certified.notes.model.Note;
import com.certified.notes.model.Todo;
import com.certified.notes.model.User;
import com.certified.notes.room.NotesDao;
import com.certified.notes.room.NotesDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    public static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final NotesDao mNotesDao;
    private final LiveData<List<Note>> allNotes;
    private final LiveData<List<Note>> randomNotes;
    private final LiveData<List<Course>> allCourses;
    private final LiveData<List<Course>> randomCourses;
    private final LiveData<List<Todo>> allTodos;
    private final LiveData<List<BookMark>> allBookMarks;
    private final LiveData<List<Integer>> allNoteIds;
    private final LiveData<List<Integer>> allCourseUnits;
    private final LiveData<List<Integer>> allCourseCreditPoints;
    private final LiveData<User> user;

    public Repository(Application application) {
        NotesDatabase database = NotesDatabase.getInstance(application);
        mNotesDao = database.mNotesDao();
        allNotes = mNotesDao.getAllNotes();
        allCourses = mNotesDao.getAllCourses();
        allTodos = mNotesDao.getAllTodos();
        allBookMarks = mNotesDao.getAllBookMarks();
        allNoteIds = mNotesDao.getNoteIds();
        allCourseUnits = mNotesDao.getCourseUnits();
        allCourseCreditPoints = mNotesDao.getCourseCreditPoints();
        user = mNotesDao.getUser();

        final long seed = System.currentTimeMillis();
        randomNotes = Transformations.map(mNotesDao.getAllNotes(), input -> {
            Collections.shuffle(input, new Random(seed));
            List<Note> notes = new ArrayList<>();
            if (input.size() > 0) {
                for (int i = 1; i <= 5; i++) {
                    notes.add(input.get(i));
                }
                if (input.size() >= 5) {
                    return notes;
                } else
                    return input;
            } else
                return null;
        });

        randomCourses = Transformations.map(mNotesDao.getAllCourses(), input -> {
            Collections.shuffle(input, new Random(seed));
            List<Course> courses = new ArrayList<>();
            if (input.size() >= 1) {
                for (int i = 1; i <= 5; i++) {
                    courses.add(input.get(i));
                }
                if (input.size() >= 5) {
                    return courses;
                } else
                    return input;
            } else
                return null;
        });
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

    public void updateUser(User user) {
        executor.execute(() -> mNotesDao.updateUser(user));
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

    public LiveData<List<Note>> getRandomNotes() {
        return randomNotes;
    }

    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }

    public LiveData<List<Course>> getRandomCourses() {
        return randomCourses;
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

    public LiveData<List<Integer>> getAllCourseUnits() {
        return allCourseUnits;
    }

    public LiveData<List<Integer>> getAllCourseCreditPoints() {
        return allCourseCreditPoints;
    }

    public LiveData<User> getUser() {
        return user;
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

    public String getCourseTitle(String courseCode) {
        try {
            return executor.submit(() -> mNotesDao.getCourseTitle(courseCode)).get();
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

    public LiveData<List<BookMark>> getBookMarkAt(int noteId) {
        try {
            return executor.submit(() -> mNotesDao.getBookMarkAt(noteId)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LiveData<List<Note>> getNotesAt(String courseCode) {
        try {
            return executor.submit(() -> mNotesDao.getNotesAt(courseCode)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LiveData<List<Note>> getDeletableNotes(String noCourse) {
        try {
            return executor.submit(() -> mNotesDao.getDeletableNotes(noCourse)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LiveData<List<BookMark>> getDeletableBookmarks(String noCourse) {
        try {
            return executor.submit(() -> mNotesDao.getDeletableBookmarks(noCourse)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LiveData<List<Note>> searchNotes(String searchQuery) {
        try {
            return executor.submit(() -> mNotesDao.searchNotes(searchQuery)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LiveData<List<Course>> searchCourses(String searchQuery) {
        try {
            return executor.submit(() -> mNotesDao.searchCourses(searchQuery)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LiveData<List<BookMark>> searchBookmarks(String searchQuery) {
        try {
            return executor.submit(() -> mNotesDao.searchBookmarks(searchQuery)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}