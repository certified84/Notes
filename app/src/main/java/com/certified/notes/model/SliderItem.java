package com.certified.notes.model;

public class SliderItem {

    private final int animation;
    private final String title;
    private final String description;

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