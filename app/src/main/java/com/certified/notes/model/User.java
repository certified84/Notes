package com.certified.notes.model;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {

    //    Columns
    @PrimaryKey
    private int id;

    private String name;

    private String school;

    private String department;

    private String level;

    @ColumnInfo(name = "profile_image")
    private Bitmap profileImage;

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