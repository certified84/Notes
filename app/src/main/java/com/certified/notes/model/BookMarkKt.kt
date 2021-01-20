package com.certified.notes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "bookmark_table")
class BookMarkKt(
        @field:ColumnInfo(name = "note_id") val noteId: Int,
        @field:ColumnInfo(name = "course_code") val courseCode: String,
        @field:ColumnInfo(name = "note_title") val noteTitle: String,
        @field:ColumnInfo(name = "note_content") val noteContent: String
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}