package com.example.mydairy;

import org.jetbrains.annotations.NotNull;

public class Record {

    private long id;
    private String date;
    private String record;
    private String imageBase64;

    public Record(long id, String record, String date, String imageBase64) {
        this.id = id;
        this.record = record;
        this.date = date;
        this.imageBase64 = imageBase64;
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

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    @NotNull
    @Override
    public String toString() {
        return this.record + " : " + this.date;
    }
}
