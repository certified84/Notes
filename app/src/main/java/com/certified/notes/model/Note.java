package com.certified.notes.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {

    //    Columns
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "course_title")
    private String courseTitle;

    @NonNull
    @ColumnInfo(name = "note_title")
    private String title;

    @ColumnInfo(name = "note_content")
    private String content;

    public Note(String courseTitle, String title, String content) {
        this.courseTitle = courseTitle;
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCourseTitle() {
        return courseTitle;
    }
}
