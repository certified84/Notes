package com.certified.notes.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
class User(
    val name: String,
    val school: String,
    val department: String,
    val level: String,
    @field:ColumnInfo(name = "profile_image") val profileImage: Bitmap?
) {
    @PrimaryKey
    var id = 0
}