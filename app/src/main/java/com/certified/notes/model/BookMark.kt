package com.certified.notes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark_table")
data class BookMark(
    @ColumnInfo(name = "note_id")
    val noteId: Int,
    @ColumnInfo(name = "course_code")
    val courseCode: String,
    @ColumnInfo(name = "note_title")
    val noteTitle: String,
    @ColumnInfo(name = "note_content")
    val noteContent: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0;
}