package com.example.mydiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseAdapter {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor getRecords() {
        return getRecords(null);
    }

    public Cursor getRecords(String constraint) {
        if (constraint == null || constraint.trim().length() == 0) {
            return database.rawQuery("select * from " + DatabaseHelper.TABLE, null);
        }

        return database.rawQuery("select * " +
                "from " + DatabaseHelper.TABLE + " " +
                "where " + DatabaseHelper.COLUMN_DATE + "='" + constraint + "'", null);
    }

    public Cursor getRecordsLike(String[] likeString) {
        return database.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                DatabaseHelper.COLUMN_DATE + " like ?", likeString);
    }

    public Record getRecord(long id) {
        Record record = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?", DatabaseHelper.TABLE, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
            String textRecord = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD));
            String imageBase64 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE));
            record = new Record(id, textRecord, date, imageBase64);
        }
        cursor.close();
        return record;
    }

    public void insert(Record record) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_DATE, record.getDate());
        cv.put(DatabaseHelper.COLUMN_RECORD, record.getRecord());
        cv.put(DatabaseHelper.COLUMN_IMAGE, record.getImageBase64());
        database.insert(DatabaseHelper.TABLE, null, cv);
    }

    public void delete(long recordId) {
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(recordId)};
        database.delete(DatabaseHelper.TABLE, whereClause, whereArgs);
    }

    public void update(Record record) {
        String whereClause = DatabaseHelper.COLUMN_ID + "=" + record.getId();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_DATE, record.getDate());
        cv.put(DatabaseHelper.COLUMN_RECORD, record.getRecord());
        cv.put(DatabaseHelper.COLUMN_IMAGE, record.getImageBase64());
        database.update(DatabaseHelper.TABLE, cv, whereClause, null);
    }
}