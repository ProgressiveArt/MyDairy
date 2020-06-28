package com.example.mydairy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

public class DairyActivity extends AppCompatActivity {

    EditText dateBox;
    EditText recordBox;
    Button delButton;
    Button saveButton;
    Button getImage;
    Calendar date = Calendar.getInstance();
    EasyImage easyImage;
    ImageView imageView;

    private DatabaseAdapter adapter;
    long recordId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy);

        dateBox = findViewById(R.id.date);
        setInitialDateTime();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recordBox = findViewById(R.id.record);
        delButton = findViewById(R.id.deleteButton);
        saveButton = findViewById(R.id.saveButton);
        getImage = findViewById(R.id.getImage);
        imageView = findViewById(R.id.imageView2);
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

        final Activity thisActivity = this;
        easyImage = new EasyImage.Builder(this)
                .setCopyImagesToPublicGalleryFolder(false)
                .allowMultiple(false)
                .build();

        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                easyImage.openGallery(thisActivity);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        easyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onMediaFilesPicked(MediaFile[] imageFiles, MediaSource source) {
                MediaFile imageFile = imageFiles[0];
                Bitmap bmp = BitmapFactory.decodeFile(imageFile.getFile().getAbsolutePath());
                imageView.setImageBitmap(bmp);
            }

            @Override
            public void onImagePickerError(@NonNull Throwable error, @NonNull MediaSource source) {
                //Some error handling
                error.printStackTrace();
            }

            @Override
            public void onCanceled(@NonNull MediaSource source) {
                //Not necessary to remove any files manually anymore
            }
        });
    }

    public void save(final View view) {
        String date = dateBox.getText().toString();
        String textRecord = recordBox.getText().toString();
        String imageBase64 = getImageBase64();
        final Record record = new Record(recordId, imageBase64, date, textRecord);

        String alert = getAlert(record);

        if (alert != null) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(alert)
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
                                adapter.update(record);
                            } else {
                                adapter.insert(record);
                            }
                            adapter.close();
                            goHome(view);
                        }
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        }
    }

    private String getImageBase64() {
        if (imageView == null) {
            return null;
        }

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageInByte = baos.toByteArray();
        return Base64.encodeToString(imageInByte, Base64.DEFAULT);
    }

    private String getAlert(Record record) {
        if (record.getRecord().trim().equals("")) {
            return "Запись не может быть пустой!!!";
        }

        if (record.getImageBase64() == null) {
            return "Картинка не загружена :с";
        }

        return null;
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