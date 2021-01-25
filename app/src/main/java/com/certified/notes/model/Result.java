package com.certified.notes.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

//@Entity(tableName = "result_table")
public class Result {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "course_code")
    private final String courseCode;

    @NonNull
    @ColumnInfo(name = "course_unit")
    private final Integer courseUnit;

    @NonNull
    @ColumnInfo(name = "course_mark")
    private final Integer courseMark;

    public Result(@NonNull String courseCode, @NonNull Integer courseUnit, @NonNull Integer courseMark) {
        this.courseCode = courseCode;
        this.courseUnit = courseUnit;
        this.courseMark = courseMark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getCourseCode() {
        return courseCode;
    }

    @NonNull
    public Integer getCourseUnit() {
        return courseUnit;
    }

    @NonNull
    public Integer getCourseMark() {
        return courseMark;
    }
}
