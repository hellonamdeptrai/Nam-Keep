package com.example.namkeep.object;

import android.graphics.Bitmap;

public class Note {
    private int id;
    private String title;
    private String content;
    private int color;
    private Bitmap background;
    private int categoryId;

    public Note() {}

    public Note(int id, String title, String content, int color, Bitmap background, int categoryId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.color = color;
        this.background = background;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Bitmap getBackground() {
        return background;
    }

    public void setBackground(Bitmap background) {
        this.background = background;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
