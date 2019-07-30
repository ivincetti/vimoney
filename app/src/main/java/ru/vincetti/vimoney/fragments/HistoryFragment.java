package ru.vincetti.vimoney.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.sqlite.DbHelper;

public class HistoryFragment extends Fragment {
    private static String LOG_TAG = "HISTORY FRAGMENT DEBUG";

    private SQLiteDatabase db;
    private TextView mMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new DbHelper(getContext()).getReadableDatabase();
        viewInit(view);
    }

    // view initialization
    private void viewInit(View view) {
        mMessage = view.findViewById(R.id.message);
    }
}
