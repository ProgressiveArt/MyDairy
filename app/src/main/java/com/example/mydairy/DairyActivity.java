package com.example.mydairy;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class DairyActivity extends AppCompatActivity {

    EditText dateBox;
    EditText recordBox;
    Button delButton;
    Button saveButton;
    Calendar date = Calendar.getInstance();

    private DatabaseAdapter adapter;
    long recordId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy);

        dateBox = findViewById(R.id.date);
        setInitialDateTime();

        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recordBox = findViewById(R.id.record);
        delButton = findViewById(R.id.deleteButton);
        saveButton = findViewById(R.id.saveButton);
        adapter = new DatabaseAdapter(this);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recordId = extras.getLong("id");
        }

        if (recordId > 0) {
            adapter.open();
            Record record = adapter.getRecord(recordId);
            dateBox.setText(record.getDate());
            recordBox.setText(String.valueOf(record.getRecord()));
            adapter.close();
        } else {
            delButton.setVisibility(View.GONE);
        }
    }

    public void save(final View view) {
        String date = dateBox.getText().toString();
        String record = recordBox.getText().toString();
        final Record record1 = new Record(recordId, date, record);

        if (record.trim().equals("")) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Запись не может быть пустой!!!")
                    .setPositiveButton("Ок", null)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Сохранить изменения?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.open();
                            if ((recordId > 0)) {
                                adapter.update(record1);
                            } else {
                                adapter.insert(record1);
                            }
                            adapter.close();
                            goHome(view);
                        }
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        }
    }

    public void delete(final View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Удаление записи")
                .setMessage("Вы уверены, что хотите удалить запись?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.open();
                        adapter.delete(recordId);
                        adapter.close();
                        goHome(view);
                    }
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    public void goHome(View view) {
        Intent intent = new Intent(this, DairyActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void setDate(View v) {
        new DatePickerDialog(DairyActivity.this, d,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, monthOfYear);
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    private void setInitialDateTime() {
        dateBox.setText(DateUtils.formatDateTime(this,
                date.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DairyActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}