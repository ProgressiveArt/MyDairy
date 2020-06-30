package com.example.mydiary.MVC.models;

import org.jetbrains.annotations.NotNull;

public class Record {
    private long id;
    private String date;
    private String record;
    private String imagePath;

    public Record(long id, String record, String date, String imagePath) {
        this.id = id;
        this.record = record;
        this.date = date;
        this.imagePath = imagePath;
    }

    public long getId() {
        return id;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @NotNull
    @Override
    public String toString() {
        return this.record + " : " + this.date;
    }
}
