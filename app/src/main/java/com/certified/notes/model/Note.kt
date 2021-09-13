package com.certified.notes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    @ColumnInfo(name = "course_code")
    val courseCode: String,
    @ColumnInfo(name = "note_title")
    val title: String,
    @ColumnInfo(name = "note_content")
    val content: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}