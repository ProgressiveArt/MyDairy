package com.example.mydiary.MVC.controllers.fragmnets.records;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mydiary.MVC.controllers.listeners.OnClickRateLimitedDecoratedListener;
import com.example.mydiary.data.DatabaseAdapter;
import com.example.mydiary.R;
import com.example.mydiary.MVC.models.Record;

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

    Button getImageFromDevice;
    Button getImageFromServer;
    EasyImage easyImage;
    ImageView imageView;

    private DatabaseAdapter adapter;
    private long recordId = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_edit_record, container, false);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        getImageFromDevice = root.findViewById(R.id.getImageFromDevice);
        getImageFromServer = root.findViewById(R.id.getImageFromServer);
        imageView = root.findViewById(R.id.imageView2);

        recordBox = root.findViewById(R.id.record);
        dateBox = root.findViewById(R.id.date);
        setInitialDateTime();

        int buttonDelay = 500;
        Button dateButton = root.findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new OnClickRateLimitedDecoratedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(root);
            }
        }, buttonDelay));


        Button delButton = root.findViewById(R.id.deleteButton);
        delButton.setOnClickListener(new OnClickRateLimitedDecoratedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(root);
            }
        }, buttonDelay));

        Button saveButton = root.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new OnClickRateLimitedDecoratedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(root);
            }
        }, buttonDelay));

        if (getArguments() != null && getArguments().containsKey("id")) {
            recordId = getArguments().getLong("id") + 1;
        }

        adapter = new DatabaseAdapter(getActivity());
        if (recordId > 0) {
            adapter.open();
            Record record = adapter.getRecord(recordId);
            dateBox.setText(record.getDate());
            recordBox.setText(String.valueOf(record.getRecord()));
            byte[] decodedString = Base64.decode(record.getImageBase64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
            adapter.close();
        } else {
            delButton.setVisibility(View.GONE);
        }

        easyImage = new EasyImage.Builder(getActivity())
                .setCopyImagesToPublicGalleryFolder(false)
                .allowMultiple(false)
                .build();

        getImageFromServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasConnection(getActivity()) == true) {
                    navController.navigate(R.id.fragment_get_images); //Переход на будущее
//                    Bitmap bmp = null;
//                    GetImg getImg = new GetImg(getActivity());
//                    getImg.execute("https://cdn.icon-icons.com/icons2/2416/PNG/128/letter_love_mail_message_post_wings_valentine_icon_146720.png");
//                    try {
//                        bmp = getImg.get();
//                    } catch (Exception e) {
//                    }
//                    imageView.setImageBitmap(bmp);
                } else {
                    Toast.makeText(getActivity(),"Нет доступа в интернет, но можно загрузить картинку с телефона" ,Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Fragment thisFragment = this;
        getImageFromDevice.setOnClickListener(new View.OnClickListener() {
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

    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        return wifiInfo != null && wifiInfo.isConnected();
    }
}
