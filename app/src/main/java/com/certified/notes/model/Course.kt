package com.certified.notes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course_table")
class Course(
        @field:ColumnInfo(name = "course_code") val courseCode: String,
        @field:ColumnInfo(name = "course_title") val courseTitle: String,
        @field:ColumnInfo(name = "course_unit") val courseUnit: Int,
        @field:ColumnInfo(name = "course_mark") val courseMark: Int,
        @field:ColumnInfo(name = "course_grade") val courseGrade: String,
        @field:ColumnInfo(name = "course_grade_point") val courseGradePoint: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    @field:ColumnInfo(name = "course_credit_point") var courseCreditPoint: Int = courseGradePoint * courseUnit
}