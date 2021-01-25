package com.certified.notes.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {

    //    Columns
    @PrimaryKey
    private int id;

    @NonNull
    private final String name;

    @NonNull
    private final String school;

    @NonNull
    private final String department;

    @NonNull
    private final String level;

    @ColumnInfo(name = "profile_image")
    private final Bitmap profileImage;

    public User(String name, String school, String department, String level, Bitmap profileImage) {
        this.name = name;
        this.school = school;
        this.department = department;
        this.level = level;
        this.profileImage = profileImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getSchool() {
        return school;
    }

    public String getDepartment() {
        return department;
    }

    public String getLevel() {
        return level;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }
}