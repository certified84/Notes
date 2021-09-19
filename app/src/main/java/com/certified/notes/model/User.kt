package com.certified.notes.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    val name: String,
    val school: String,
    val department: String,
    val level: String,
    @ColumnInfo(name = "profile_image") val profileImage: Uri?
) {
    @PrimaryKey
    var id: Int = 0
    var email: String = ""
    var uid: String = ""
}