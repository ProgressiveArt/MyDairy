package com.example.mydiary.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mydiary.DatabaseHelper;
import com.example.mydiary.R;
import com.example.mydiary.Record;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ItemDiaryAdapter extends ArrayAdapter<Record> {

    public ItemDiaryAdapter(@NonNull Context context,
                            Cursor cursorRecords) {
        super(context, R.layout.item_diary_record, getArrayList(cursorRecords));
    }

    @NotNull
    private static ArrayList<Record> getArrayList(Cursor cursorRecords) {
        ArrayList<Record> records = new ArrayList<>();

        while (cursorRecords.moveToNext()) {
            long id = cursorRecords.getLong(cursorRecords.getColumnIndex(DatabaseHelper.COLUMN_ID));
            String date = cursorRecords.getString(cursorRecords.getColumnIndex(DatabaseHelper.COLUMN_DATE));
            String textRecord = cursorRecords.getString(cursorRecords.getColumnIndex(DatabaseHelper.COLUMN_RECORD));
            String imageBase64 = cursorRecords.getString(cursorRecords.getColumnIndex(DatabaseHelper.COLUMN_IMAGE));

            Record record = new Record(id, textRecord, date, imageBase64);
            records.add(record);
        }

        return records;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Record record = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_diary_record, null);
        }

        ((TextView) convertView.findViewById(R.id.textView2)).setText(record.getDate());
        ((TextView) convertView.findViewById(R.id.textView3)).setText(record.getRecord());
        byte[] decodedString = Base64.decode(record.getImageBase64(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ((ImageView) convertView.findViewById(R.id.imageView)).setImageBitmap(decodedByte);

        return convertView;
    }
}
