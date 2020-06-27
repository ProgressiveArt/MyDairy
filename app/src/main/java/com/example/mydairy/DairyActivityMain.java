package com.example.mydairy;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class DairyActivityMain extends AppCompatActivity {

    ListView recordList;
    DatabaseAdapter dbAdapter;
    Cursor recordCursor;
    SimpleCursorAdapter recordAdapter;
    EditText recordFilter;
    Calendar date = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy_main);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recordFilter = findViewById(R.id.recordFilter);

        recordList = findViewById(R.id.list);

        recordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DairyActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("click", 25);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        final DatabaseAdapter adapter = new DatabaseAdapter(this);

        adapter.open();

        recordCursor = adapter.getRecords();

        String[] headers = new String[]{DatabaseHelper.COLUMN_DATE, DatabaseHelper.COLUMN_RECORD};
        recordAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                recordCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);

        Filter(adapter);
        recordList.setAdapter(recordAdapter);
    }

    public void add(View view) {
        Intent intent = new Intent(this, DairyActivity.class);
        startActivity(intent);
    }

    public void Filter(final DatabaseAdapter adapter) {
        // если в текстовом поле есть текст, выполняем фильтрацию
        // данная проверка нужна при переходе от одной ориентации экрана к другой
        if (!recordFilter.getText().toString().isEmpty())
            recordAdapter.getFilter().filter(recordFilter.getText().toString());

        recordFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recordAdapter.getFilter().filter(s.toString());
            }
        });

        recordAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return (constraint == null || constraint.length() == 0) ? adapter.getRecords() : adapter.getRecordsLike(new String[]{"%" + constraint.toString() + "%"});
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recordCursor.close();
        dbAdapter.close();
        finish();
    }

    public void setDate(View v) {
        new DatePickerDialog(DairyActivityMain.this, d,
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
        recordFilter.setText(DateUtils.formatDateTime(this,
                date.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}