package com.certified.notes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
class Note(
        @field:ColumnInfo(name = "course_code") val courseCode: String,
        @field:ColumnInfo(name = "note_title") val title: String,
        @field:ColumnInfo(name = "note_content") val content: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}