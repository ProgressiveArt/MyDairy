package com.example.mydiary.ui.records;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mydiary.DatabaseAdapter;
import com.example.mydiary.R;
import com.example.mydiary.Record;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;


public class EditRecordFragment extends Fragment {
    private Calendar date = Calendar.getInstance();
    NavController navController;

    private EditText dateBox;
    private EditText recordBox;

    Button getImage;
    EasyImage easyImage;
    ImageView imageView;

    private DatabaseAdapter adapter;
    private long recordId = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_edit_record, container, false);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        getImage = root.findViewById(R.id.getImage);
        imageView = root.findViewById(R.id.imageView2);

        recordBox = root.findViewById(R.id.record);
        dateBox = root.findViewById(R.id.date);
        setInitialDateTime();

        Button dateButton = root.findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setDate(root);
            }
        });


        Button delButton = root.findViewById(R.id.deleteButton);
        delButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                delete(root);
            }
        });

        Button saveButton = root.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                save(root);
            }
        });

        if (getArguments() != null && getArguments().containsKey("id")) {
            recordId = getArguments().getLong("id");
        }

        adapter = new DatabaseAdapter(getActivity());
        if (recordId > 0) {
            adapter.open();
            Record record = adapter.getRecord(recordId);
            dateBox.setText(record.getDate());
            recordBox.setText(String.valueOf(record.getRecord()));
            adapter.close();
        } else {
            delButton.setVisibility(View.GONE);
        }

        easyImage = new EasyImage.Builder(getActivity())
                .setCopyImagesToPublicGalleryFolder(false)
                .allowMultiple(false)
                .build();

        final Fragment thisFragment = this;
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                easyImage.openGallery(thisFragment);
            }
        });
        return root;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        easyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
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

    public void save(View view) {
        String date = dateBox.getText().toString();
        String textRecord = recordBox.getText().toString();
        String imageBase64 = getImageBase64();
        final Record record = new Record(recordId, textRecord, date, imageBase64);

        String alert = getAlert(record);

        if (alert != null) {
            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(alert)
                    .setPositiveButton("Ок", null)
                    .show();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_info)
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
                            navController.navigate(R.id.nav_records);
                        }
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        }
    }

    private String getImageBase64() {
        if (imageView == null || imageView.getDrawable() == null) {
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

    public void delete(View view) {
        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Удаление записи")
                .setMessage("Вы уверены, что хотите удалить запись?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.open();
                        adapter.delete(recordId);
                        adapter.close();
                        navController.navigate(R.id.nav_records);
                    }
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private void setDate(View v) {
        new DatePickerDialog(v.getContext(), d,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, monthOfYear);
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    private void setInitialDateTime() {
        dateBox.setText(DateUtils.formatDateTime(getActivity(),
                date.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }
}
