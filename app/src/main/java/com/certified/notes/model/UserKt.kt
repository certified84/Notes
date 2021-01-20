package com.certified.notes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "user_table")
class UserKt (val name: String, val school: String, val department: String, val level: String){
    @PrimaryKey(autoGenerate = true)
    var id = 0
}