package com.certified.notes.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {

    //    Columns
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "course_code")
    private final String courseCode;

    @NonNull
    @ColumnInfo(name = "note_title")
    private final String title;

    @ColumnInfo(name = "note_content")
    private final String content;

    public Note(@NonNull String courseCode, @NonNull String title, String content) {
        this.courseCode = courseCode;
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

    public String getCourseCode() {
        return courseCode;
    }

    @NonNull
    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", courseCode='" + courseCode + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}