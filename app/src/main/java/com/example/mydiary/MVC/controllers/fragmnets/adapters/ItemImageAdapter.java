package com.example.mydiary.MVC.controllers.fragmnets.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mydiary.MVC.controllers.fragmnets.records.GetImg;
import com.example.mydiary.MVC.models.Record;
import com.example.mydiary.R;
import com.example.mydiary.data.DatabaseHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ItemImageAdapter extends ArrayAdapter<String> {

    public ItemImageAdapter(@NonNull Context context,
                            ArrayList<String> arrayList) {
        super(context, R.layout.item_get_images, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String imgUrl = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_get_images, null);
        }

        GetImg getImg = new GetImg(convertView.getContext());
        getImg.execute(imgUrl);
        Bitmap img = null;
        try {
            img = getImg.get();
        } catch (Exception e) {
        }

        ((ImageView) convertView.findViewById(R.id.image)).setImageBitmap(img);

        return convertView;
    }
}
