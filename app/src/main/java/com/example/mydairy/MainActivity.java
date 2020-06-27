package com.example.mydairy;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class MainActivity extends AppCompatActivity {
    public static final String showSettingsActivityAction = "SHOW_SETTINGS_ACTIVITY";
    public static final String showDairyActivityMainAction = "SHOW_DAIRY_ACTIVITY_MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                showSettingsActivity();
                return true;

            case R.id.about_settings:
                showAboutDialog(item);
                return true;

            case R.id.exit_settings:
                showExitDialog(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDairyMainActivity(View view) {
        Intent intent = new Intent(showDairyActivityMainAction);
        startActivity(intent);
    }

    public void showSettingsActivity() {
        Intent intent = new Intent(showSettingsActivityAction);
        startActivity(intent);
    }

    public void showAboutDialog(MenuItem item) {
        DialogFragment aboutDialog = new AboutDialogFragment();
        aboutDialog.show(getSupportFragmentManager(), "about");
    }

    public void showExitDialog(MenuItem item) {
        DialogFragment exitDialog = new ExitDialogFragment();
        exitDialog.show(getSupportFragmentManager(), "exit");
    }
}
