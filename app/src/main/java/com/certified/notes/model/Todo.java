package com.certified.notes.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "todo_table")
public class Todo {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String todo;

    private boolean done;

    public Todo(String todo, boolean done) {
        this.todo = todo;
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTodo() {
        return todo;
    }

    public boolean isDone() {
        return done;
    }
}
