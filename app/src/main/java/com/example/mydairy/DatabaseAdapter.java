package com.example.mydairy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ResourceCursorAdapter;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapter open() {
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor getRecords() {
        return database.rawQuery("select * from " + DatabaseHelper.TABLE, null);
    }

    public Cursor getRecordsLike(String[] likeString) {
        return database.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                DatabaseHelper.COLUMN_RECORD + " like ?", likeString);
    }

    private Cursor getAllEntries() {
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_DATE, DatabaseHelper.COLUMN_RECORD};
        return database.query(DatabaseHelper.TABLE, columns, null, null, null, null, null);
    }

    public long getCount() {
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE);
    }

    public Record getRecord(long id) {
        Record record = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?", DatabaseHelper.TABLE, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD));
            String year = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
            String imageBase64 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE));
            record = new Record(id, imageBase64, name, year);
        }
        cursor.close();
        return record;
    }

    public long insert(Record record) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_DATE, record.getDate());
        cv.put(DatabaseHelper.COLUMN_RECORD, record.getRecord());
        cv.put(DatabaseHelper.COLUMN_IMAGE, record.getImageBase64());
        return database.insert(DatabaseHelper.TABLE, null, cv);
    }

    public long delete(long recordId) {
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(recordId)};
        return database.delete(DatabaseHelper.TABLE, whereClause, whereArgs);
    }

    public long update(Record record) {
        String whereClause = DatabaseHelper.COLUMN_ID + "=" + record.getId();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_DATE, record.getDate());
        cv.put(DatabaseHelper.COLUMN_RECORD, record.getRecord());
        cv.put(DatabaseHelper.COLUMN_IMAGE, record.getImageBase64());
        return database.update(DatabaseHelper.TABLE, cv, whereClause, null);
    }
}