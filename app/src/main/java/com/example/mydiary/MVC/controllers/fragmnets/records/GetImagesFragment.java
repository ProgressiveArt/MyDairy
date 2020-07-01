package com.example.mydiary.MVC.controllers.fragmnets.records;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mydiary.MVC.controllers.fragmnets.adapters.ItemImageAdapter;
import com.example.mydiary.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class GetImagesFragment extends Fragment {

    public Elements title;
    public ArrayList<String> titleList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private ListView lv;
    NavController navController;
    private long recordId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_get_images, container, false);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        if (getArguments() != null && getArguments().containsKey("id")) {
            recordId = getArguments().getLong("id");
        }

        lv = root.findViewById(R.id.listView1);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putLong("id", recordId);
                bundle.putString("url", titleList.get((int) id));
                navController.navigate(R.id.fragment_edit_record, bundle);
            }
        });

        Thread myThready = new Thread(new Runnable()
        {
            public void run()
            {
                new NewThread().execute();
            }
        });
        myThready.start();

        adapter = new ItemImageAdapter(getActivity(), titleList);

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

                for (int i = 0; i < title.size(); i++) {
                    titleList.add(title.get(i).attributes().get("data-original"));
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