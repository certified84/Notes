package com.certified.notes.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

//@Entity(tableName = "result_table")
class Result(
        @field:ColumnInfo(name = "course_code") val courseCode: String,
        @field:ColumnInfo(name = "course_unit") val courseUnit: String,
        @field:ColumnInfo(name = "course_mark") val courseMark: String
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}