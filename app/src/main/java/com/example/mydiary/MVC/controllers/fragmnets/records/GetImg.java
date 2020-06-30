package com.example.mydiary.MVC.controllers.fragmnets.records;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.mydiary.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetImg extends AsyncTask<String,Void, Bitmap> {

    @SuppressLint("StaticFieldLeak")
    Context mcontext;

    public GetImg(Context context){
        mcontext = context;
    }

    @Override
    protected Bitmap doInBackground(String... path) {
        String src = path[0];

        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            return BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.ic_default);
        }
    }
}
