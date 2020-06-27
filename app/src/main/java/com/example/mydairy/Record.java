package com.example.mydairy;

public class Record {

    private long id;
    private String date;
    private String record;

    Record(long id, String record, String date){
        this.id = id;
        this.record = record;
        this.date = date;
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

    @Override
    public String toString() {
        return this.record + " : " + this.date;
    }
}
