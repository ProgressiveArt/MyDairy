package com.example.mydiary.MVC;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.mydiary.R;

import java.io.IOException;
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
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            Bitmap defaultIcon = BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.ic_default);
            return defaultIcon;
        }
    }
}
