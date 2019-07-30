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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.Transaction;
import ru.vincetti.vimoney.data.adapters.TransactionsRVAdapter;
import ru.vincetti.vimoney.data.sqlite.DbHelper;
import ru.vincetti.vimoney.utils.TransactionsGenerator;

public class HistoryFragment extends Fragment {
    private static String LOG_TAG = "HISTORY FRAGMENT DEBUG";

    private SQLiteDatabase db;
    private ArrayList<Transaction> trList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewInit(view);

        db = new DbHelper(getContext()).getReadableDatabase();
        trList = TransactionsGenerator.generate();

        // список транзакций
        TransactionsRVAdapter transactionsRVAdapter = new TransactionsRVAdapter(trList);
        RecyclerView trListView = view.findViewById(R.id.home_transactions_recycle_view);
        trListView.setHasFixedSize(true);
        LinearLayoutManager trLayoutManager = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);
        trListView.setLayoutManager(trLayoutManager);
        trListView.setAdapter(transactionsRVAdapter);
    }

    // view initialization
    private void viewInit(View view) {
        //
    }
}
