package com.certified.notes.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "course_table")
public class Course {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "course_code")
    private String courseCode;

    @NonNull
    @ColumnInfo(name = "course_title")
    private String courseTitle;

    @NonNull
    @ColumnInfo(name = "course_unit")
    private Integer courseUnit;

    @NonNull
    @ColumnInfo(name = "course_mark")
    private Integer courseMark;

    @NonNull
    @ColumnInfo(name = "course_grade")
    private String courseGrade;

    @NonNull
    @ColumnInfo(name = "course_grade_point")
    private Integer courseGradePoint;

    @NonNull
    @ColumnInfo(name = "course_credit_point")
    private Integer courseCreditPoint;

    public Course(@NonNull String courseCode, @NonNull String courseTitle, @NonNull Integer courseUnit,
                  @NonNull Integer courseMark, @NonNull String courseGrade, @NonNull Integer courseGradePoint) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.courseUnit = courseUnit;
        this.courseMark = courseMark;
        this.courseGrade = courseGrade;
        this.courseGradePoint = courseGradePoint;
        this.courseCreditPoint = courseGradePoint * courseUnit;
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
    public String getCourseTitle() {
        return courseTitle;
    }

    @NonNull
    public Integer getCourseUnit() {
        return courseUnit;
    }

    @NonNull
    public Integer getCourseMark() {
        return courseMark;
    }

    @NonNull
    public String getCourseGrade() {
        return courseGrade;
    }

    @NonNull
    public Integer getCourseGradePoint() {
        return courseGradePoint;
    }

    public void setCourseCreditPoint(@NonNull Integer courseCreditPoint) {
        this.courseCreditPoint = courseCreditPoint;
    }

    @NonNull
    public Integer getCourseCreditPoint() {
        return courseCreditPoint;
    }
}