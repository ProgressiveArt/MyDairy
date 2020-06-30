package com.example.mydiary.MVC.controllers.fragmnets.adapters;

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

import com.example.mydiary.MVC.controllers.fragmnets.records.GetImg;
import com.example.mydiary.data.DatabaseHelper;
import com.example.mydiary.R;
import com.example.mydiary.MVC.models.Record;

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
            String imagePath = cursorRecords.getString(cursorRecords.getColumnIndex(DatabaseHelper.COLUMN_IMAGE));

            Record record = new Record(id, textRecord, date, imagePath);
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

        String imagePath = record.getImagePath();
        Bitmap bitmap = null;
        if(imagePath.contains("EasyImage")) {
            bitmap = BitmapFactory.decodeFile(imagePath);
        } else {
            GetImg getImg = new GetImg(convertView.getContext());
            getImg.execute(imagePath);
            try {
                bitmap = getImg.get();
            } catch (Exception e) {
            }
        }

        ((ImageView) convertView.findViewById(R.id.imageView)).setImageBitmap(bitmap);

        return convertView;
    }
}
