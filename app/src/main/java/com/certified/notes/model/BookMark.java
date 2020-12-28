package com.certified.notes.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookmark_table")
public class BookMark {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "note_id")
    private int noteId;

    @NonNull
    @ColumnInfo(name = "course_code")
    private String courseCode;

    @NonNull
    @ColumnInfo(name = "note_title")
    private String noteTitle;

    @ColumnInfo(name = "note_content")
    private String noteContent;

    public BookMark(int noteId, @NonNull String courseCode, @NonNull String noteTitle, String noteContent) {
        this.noteId = noteId;
        this.courseCode = courseCode;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNoteId() {
        return noteId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    @NonNull
    public String getNoteTitle() {
        return noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }
}
