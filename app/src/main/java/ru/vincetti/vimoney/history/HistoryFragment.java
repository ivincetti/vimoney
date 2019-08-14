package ru.vincetti.vimoney.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.adapters.TransactionsRVAdapter;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.transaction.TransactionActivity;

public class HistoryFragment extends Fragment {
    private static String LOG_TAG = "HISTORY FRAGMENT DEBUG";
    private static String BUNDLETAG = "ru.vincetti.vimoney.transhistory";
    private static int TRANSACTIONS_COUNT = 25;

    TransactionsRVAdapter transactionsRVAdapter;
    private int trCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            trCount = getArguments().getInt(BUNDLETAG, TRANSACTIONS_COUNT);
        }

        // список транзакций
        transactionsRVAdapter = new TransactionsRVAdapter(position -> TransactionActivity.start(getActivity()));
        RecyclerView trListView = view.findViewById(R.id.home_transactions_recycle_view);
        trListView.setHasFixedSize(true);
        LinearLayoutManager trLayoutManager = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);
        trListView.setLayoutManager(trLayoutManager);
        trListView.setAdapter(transactionsRVAdapter);

        LiveData<List<TransactionModel>> transList = AppDatabase.getInstance(getContext()).transactionDao().loadAllTransactionsCount(trCount);
        transList.observe(this, new Observer<List<TransactionModel>>() {
            @Override
            public void onChanged(List<TransactionModel> transactions) {
                transactionsRVAdapter.setTransaction(transactions);
            }
        });
    }
}
