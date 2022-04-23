package com.example.namkeep.object;

public class Label {
    private int id;
    private String label;
    private int idNote;

    public Label() {
    }

    public Label(int id, String label, int idNote) {
        this.id = id;
        this.label = label;
        this.idNote = idNote;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getIdNote() {
        return idNote;
    }

    public void setIdNote(int idNote) {
        this.idNote = idNote;
    }
}
