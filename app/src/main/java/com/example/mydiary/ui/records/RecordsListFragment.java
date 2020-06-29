package com.example.mydiary.ui.records;


import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.example.mydiary.DatabaseAdapter;
import com.example.mydiary.DatabaseHelper;
import com.example.mydiary.R;

import java.util.Calendar;

public class RecordsListFragment extends Fragment {

    private Calendar date = Calendar.getInstance();
    private SimpleCursorAdapter recordAdapter;
    private ListView recordList;
    NavController navController;
    private EditText recordFilter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_records_list, container, false);

        recordFilter = root.findViewById(R.id.recordFilter);

        recordList = root.findViewById(R.id.list);
        recordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putLong("id", id);
                navController.navigate(R.id.fragment_edit_record, bundle);
            }
        });

        Button dateButton = root.findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setDate(root);
            }
        });

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        Button addButton = root.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navController.navigate(R.id.fragment_edit_record);
            }
        });

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        DatabaseAdapter adapter = new DatabaseAdapter(getActivity());
        adapter.open();
        Cursor records = adapter.getRecords();
        String[] headers = new String[]{DatabaseHelper.COLUMN_DATE, DatabaseHelper.COLUMN_RECORD, DatabaseHelper.COLUMN_IMAGE};
        recordAdapter = new SimpleCursorAdapter(getActivity(), R.layout.item_diary_record, records, headers, new int[]{R.id.textView2, R.id.textView3, R.id.imageView2}, 0);
        filter(adapter);
        recordList.setAdapter(recordAdapter);

        adapter.close();
    }

    public void filter(final DatabaseAdapter adapter) {
        // если в текстовом поле есть текст, выполняем фильтрацию
        // данная проверка нужна при переходе от одной ориентации экрана к другой
        if (!recordFilter.getText().toString().isEmpty()) {
            recordAdapter.getFilter().filter(recordFilter.getText().toString());
        }
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
                return (constraint == null || constraint.length() == 0)
                        ? adapter.getRecords()
                        : adapter.getRecordsLike(new String[]{"%" + constraint.toString() + "%"});
            }
        });
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
        recordFilter.setText(DateUtils.formatDateTime(getActivity(),
                date.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        onResume();
    }
}
