package com.certified.notes;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.certified.notes.model.Course;
import com.certified.notes.model.Note;
import com.certified.notes.model.Todo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class, Course.class, Todo.class}, version = 2, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static NotesDatabase instance;
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            Note note1 = new Note("Android Programming with Intents", "Dynamic intent resolution", "Wow, intents allow components to be resolved at runtime");
            Note note2 = new Note("Java Fundamentals: The Java Language", "Parameters", "Leverage variable-length parameter lists?");
            Note note3 = new Note("Android Programming with Intents", "Delegating intents", "PendingIntents are powerful; they delegate much more than just a component invocation");
            Note note4 = new Note("Java Fundamentals: The Core Platform", "Compiler options", "The -jar option isn't compatible with with the -cp option");
            Note note5 = new Note("Java Fundamentals: The Core Platform", "Serialization", "Remember to include SerialVersionUID to assure version compatibility");
            Note note6 = new Note("Android Async Programming and Services", "Service default threads", "Did you know that by default an Android Service will tie up the UI thread?");
            Note note7 = new Note("Java Fundamentals: The Java Language", "Anonymous classes", "Anonymous classes simplify implementing one-use types");
            Note note8 = new Note("Android Async Programming and Services", "Long running operations", "Foreground Services can be tied to a notification icon");

            Course course1 = new Course("CPE 301", "Java Fundamentals: The Java Language");
            Course course2 = new Course("MTS 301", "Android Programming with Intents");
            Course course3 = new Course("AGE 301", "Java Fundamentals: The Core Platform");
            Course course4 = new Course("GNS 201", "Android Async Programming and Services");

            databaseWriteExecutor.execute(() -> {
                NotesDao notesDao = instance.mNotesDao();

                notesDao.deleteAllNotes();
                notesDao.insertNote(note1);
                notesDao.insertNote(note2);
                notesDao.insertNote(note3);
                notesDao.insertNote(note4);
                notesDao.insertNote(note5);
                notesDao.insertNote(note6);
                notesDao.insertNote(note7);
                notesDao.insertNote(note8);

                notesDao.deleteAllCourses();
                notesDao.insertCourse(course1);
                notesDao.insertCourse(course2);
                notesDao.insertCourse(course3);
                notesDao.insertCourse(course4);
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
