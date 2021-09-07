package com.certified.notes.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    val id: Int = 0,
    val name: String,
    val school: String,
    val department: String,
    val level: String,
    @ColumnInfo(name = "profile_image") val profileImage: Bitmap?
)