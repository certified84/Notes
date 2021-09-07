package com.certified.notes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course_table")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "course_code")
    val courseCode: String,
    @ColumnInfo(name = "course_title")
    val courseTitle: String,
    @ColumnInfo(name = "course_unit")
    val courseUnit: Int,
    @ColumnInfo(name = "course_mark")
    val courseMark: Int,
    @ColumnInfo(name = "course_grade")
    val courseGrade: String,
    @ColumnInfo(name = "course_grade_point")
    val courseGradePoint: Int,
    @ColumnInfo(name = "course_credit_point")
    val courseCreditPoint: Int = courseGradePoint * courseUnit
)