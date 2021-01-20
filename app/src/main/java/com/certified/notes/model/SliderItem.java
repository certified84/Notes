package com.certified.notes.model;

public class SliderItem {

    private int animation;
    private String title;
    private String description;

    public SliderItem(int animation, String title, String description) {
        this.animation = animation;
        this.title = title;
        this.description = description;
    }

    public int getAnimation() {
        return animation;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}