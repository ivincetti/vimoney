package ru.vincetti.vimoney.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.adapters.TransactionsRVAdapter;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.transaction.TransactionActivity;

public class HistoryFragment extends Fragment {
    public final static String BUNDLETAG_TRANS_COUNT_NAME = "ru.vincetti.vimoney.transhistory_count";
    public final static String BUNDLETAG_TRANS_CHECK_ID_NAME = "ru.vincetti.vimoney.transhistory_check_id";

    private final static int DEFAULT_TRANSACTIONS_COUNT = 25;
    private final static int DEFAULT_CHECK_ID = -1;

    TransactionsRVAdapter transactionsRVAdapter;
    private int trCount = DEFAULT_TRANSACTIONS_COUNT;
    private int trCheckId = DEFAULT_CHECK_ID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // количество элементов
        if (getArguments() != null && getArguments().containsKey(BUNDLETAG_TRANS_COUNT_NAME)) {
            trCount = getArguments().getInt(BUNDLETAG_TRANS_COUNT_NAME, DEFAULT_TRANSACTIONS_COUNT);
        }
        // список транзакций
        transactionsRVAdapter = new TransactionsRVAdapter(
                itemId -> TransactionActivity.start(getActivity(), itemId));
        RecyclerView trListView = view.findViewById(R.id.home_transactions_recycle_view);
        trListView.setHasFixedSize(true);
        LinearLayoutManager trLayoutManager = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);
        trListView.setLayoutManager(trLayoutManager);
        trListView.setAdapter(transactionsRVAdapter);
        // уточнение счета
        if (getArguments() != null && getArguments().containsKey(BUNDLETAG_TRANS_CHECK_ID_NAME)) {
            trCheckId = getArguments().getInt(BUNDLETAG_TRANS_CHECK_ID_NAME);
            LiveData<List<TransactionModel>> transList = AppDatabase.getInstance(getContext())
                    .transactionDao().loadCheckTransactionsCount(trCheckId, trCount);
            transList.observe(this,
                    transactions -> transactionsRVAdapter.setTransaction(transactions));
        } else {
            LiveData<List<TransactionModel>> transList = AppDatabase.getInstance(getContext())
                    .transactionDao().loadAllTransactionsCount(trCount);
            transList.observe(this,
                    transactions -> transactionsRVAdapter.setTransaction(transactions));
        }
    }
}
