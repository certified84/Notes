package com.certified.notes.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.certified.notes.model.BookMark;
import com.certified.notes.model.Course;
import com.certified.notes.model.Note;
import com.certified.notes.model.Todo;
import com.certified.notes.model.User;
import com.certified.notes.util.Converters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class, Course.class, Todo.class, BookMark.class, User.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class NotesDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static NotesDatabase instance;
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            Note note1 = new Note("MTS 315", "Dynamic intent resolution", "Wow, intents allow components to be resolved at runtime");
            Note note2 = new Note("CPE 301", "Parameters", "Leverage variable-length parameter lists?");
            Note note3 = new Note("EEE 301", "Delegating intents", "PendingIntents are powerful; they delegate much more than just a component invocation");
            Note note4 = new Note("AGE 301", "Compiler options", "The -jar option isn't compatible with with the -cp option");
            Note note5 = new Note("EEE 305", "Serialization", "Remember to include SerialVersionUID to assure version compatibility");
            Note note6 = new Note("PMT 301", "Service default threads", "Did you know that by default an Android Service will tie up the UI thread?");
            Note note7 = new Note("EEE 311", "Anonymous classes", "Anonymous classes simplify implementing one-use types");
            Note note8 = new Note("EEE 307", "Long running operations", "Foreground Services can be tied to a notification icon");

            Course course1 = new Course("AGE 301", "Engineering Statistics", 2, 0, "F", 0);
            Course course2 = new Course("CPE 301", "Digital System Design with VHDL", 3, 0, "F", 0);
            Course course3 = new Course("EEE 301", "Measurement and Instrumentation", 2, 0, "F", 0);
            Course course4 = new Course("EEE 303", "Electronics Engineering I", 3, 0, "F", 0);
            Course course5 = new Course("EEE 305", "Electromagnetic Field Theory", 2, 0, "F", 0);
            Course course6 = new Course("EEE 307", "Electric Machines I", 3, 0, "F", 0);
            Course course7 = new Course("EEE 311", "Electric Circuit Theory I", 3, 0, "F", 0);
            Course course8 = new Course("EEE 313", "Electrical/Electronic Laboratory I", 1, 0, "F", 0);
            Course course9 = new Course("MTS 315", "Engineering Mathematics I", 3, 0, "F", 0);
            Course course10 = new Course("PMT 301", "Introduction to Entrepreneurship", 2, 0, "F", 0);

            Todo todo1 = new Todo("Make sure to complete the To-dos below", false);
            Todo todo2 = new Todo("Learn LiveData, Room, ViewModel, WorkManager, Notification, Paging library", false);
            Todo todo3 = new Todo("The above should be learnt completely in order to pass the AAD exam", false);
            Todo todo4 = new Todo("Stop all projects that aren't related to the AAD exam", false);
            Todo todo5 = new Todo("Visit the code labs and study guide for the AAD exam frequently", false);
            Todo todo6 = new Todo("Practice, Practice and keep practicing. Obtaining the AAD certificate should be the only priority for now", false);
            Todo todo7 = new Todo("Make sure you pass the exam at the first try!!!", false);
            Todo todo8 = new Todo("Make sure to complete the To-dos above", false);

            User user = new User("Enter name", "Enter school", "Enter department", "Select level", null);

            databaseWriteExecutor.execute(() -> {
                NotesDao notesDao = instance.mNotesDao();

////                notesDao.deleteAllNotes();
//                notesDao.insertNote(note1);
//                notesDao.insertNote(note2);
//                notesDao.insertNote(note3);
//                notesDao.insertNote(note4);
//                notesDao.insertNote(note5);
//                notesDao.insertNote(note6);
//                notesDao.insertNote(note7);
//                notesDao.insertNote(note8);
//
////                notesDao.deleteAllCourses();
//                notesDao.insertCourse(course1);
//                notesDao.insertCourse(course2);
//                notesDao.insertCourse(course3);
//                notesDao.insertCourse(course4);
//                notesDao.insertCourse(course5);
//                notesDao.insertCourse(course6);
//                notesDao.insertCourse(course7);
//                notesDao.insertCourse(course8);
//                notesDao.insertCourse(course9);
//                notesDao.insertCourse(course10);
//
////                notesDao.deleteAllTodos();
//                notesDao.insertTodo(todo1);
//                notesDao.insertTodo(todo2);
//                notesDao.insertTodo(todo3);
//                notesDao.insertTodo(todo4);
//                notesDao.insertTodo(todo5);
//                notesDao.insertTodo(todo6);
//                notesDao.insertTodo(todo7);
//                notesDao.insertTodo(todo8);
//
                notesDao.insertUser(user);
            });
        }
    };

    public static synchronized NotesDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), NotesDatabase.class, "notes_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    public abstract NotesDao mNotesDao();
}