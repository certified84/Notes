package com.certified.notes.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

//@Entity(tableName = "result_table")
data class Result(
    @ColumnInfo(name = "course_code") val courseCode: String,
    @ColumnInfo(name = "course_unit") val courseUnit: String,
    @ColumnInfo(name = "course_mark") val courseMark: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}