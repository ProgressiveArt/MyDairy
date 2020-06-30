package com.example.mydiary.MVC.controllers.fragmnets.records;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.mydiary.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class getImagesFragment extends Fragment {

    public Elements title;

    public ArrayList<String> titleList = new ArrayList<String>();

    private ArrayAdapter<String> adapter;

    private ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_get_images, container, false);

        lv = root.findViewById(R.id.listView1);

        Thread myThready = new Thread(new Runnable()
        {
            public void run() //Этот метод будет выполняться в побочном потоке
            {
                new NewThread().execute();
            }
        });
        myThready.start();


        adapter = new ArrayAdapter<>(getActivity(), R.layout.item_get_images, R.id.url, titleList);

        return root;
    }

    public class NewThread extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg) {

            Document doc;
            try {

                doc = Jsoup.connect("https://icon-icons.com/ru/pack/Color-Doodle-Wedding/2416").get();

                title = doc.select("img.lazy");

                titleList.clear();

                for (Element titles : title.select("data-original")) {
                    titleList.add(titles.text());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            lv.setAdapter(adapter);
        }
    }
}