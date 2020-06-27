package com.example.mydairy;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.net.MalformedURLException;
import java.net.URL;

public class AboutDialogFragment extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.setTitle("About")
                .setMessage("Created by ProgressiveArt\n" +
                        "https://github.com/ProgressiveArt")
                .setPositiveButton("ОК", null)
                .create();
    }
}
