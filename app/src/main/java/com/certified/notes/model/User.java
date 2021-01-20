package com.certified.notes.model;

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

    public User(String name, String school, String department, String level) {
        this.name = name;
        this.school = school;
        this.department = department;
        this.level = level;
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
}