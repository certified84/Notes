package com.certified.notes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "todo_table")
class TodoKt(val todo: String, val isDone: Boolean) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}