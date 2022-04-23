package com.example.namkeep.object;

import android.graphics.Bitmap;

public class Image {
    private int id;
    private Bitmap image;
    private int idNote;

    public Image() {
    }

    public Image(int id, Bitmap image, int idNote) {
        this.id = id;
        this.image = image;
        this.idNote = idNote;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getIdNote() {
        return idNote;
    }

    public void setIdNote(int idNote) {
        this.idNote = idNote;
    }
}
