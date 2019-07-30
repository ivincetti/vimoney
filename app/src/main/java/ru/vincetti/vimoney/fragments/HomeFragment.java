package ru.vincetti.vimoney.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.Account;
import ru.vincetti.vimoney.data.adapters.CardsListViewAdapter;
import ru.vincetti.vimoney.data.adapters.TransactionsRVAdapter;
import ru.vincetti.vimoney.data.Transaction;
import ru.vincetti.vimoney.data.sqlite.DbHelper;
import ru.vincetti.vimoney.data.sqlite.VimonContract;
import ru.vincetti.vimoney.utils.TransactionsGenerator;

public class HomeFragment extends Fragment {
    private static String LOG_TAG = "MAIN FRAGMENT DEBUG";

    private SQLiteDatabase db;
    private ArrayList<Account> accList;
    private ArrayList<Transaction> trList;

    private TextView mUserText;
    private TextView mTransactionTextLink;
    private TextView mBalanceText;
    private int mAllBalance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAllBalance = 0;

        db = new DbHelper(getContext()).getReadableDatabase();

        accList = new ArrayList<>();
        trList = TransactionsGenerator.generate();
        Log.d(LOG_TAG, String.valueOf(trList.size()));

        viewInit(view);

        // userLoadfromDB();
        accountsLoadFromDB();
        mAllBalance = userBalanceChange();

        // список карт/счетов
        CardsListViewAdapter adapter = new CardsListViewAdapter(accList);
        RecyclerView cardsListView = view.findViewById(R.id.home_cards_recycle_view);
        cardsListView.setHasFixedSize(true);
        LinearLayoutManager cardsLayoutManager = new LinearLayoutManager(getContext(),
                RecyclerView.HORIZONTAL, false);
        cardsListView.setLayoutManager(cardsLayoutManager);
        cardsListView.setAdapter(adapter);

        // список транзакций
        TransactionsRVAdapter transactionsRVAdapter = new TransactionsRVAdapter(trList);
        RecyclerView trListView = view.findViewById(R.id.home_transactions_recycle_view);
        trListView.setHasFixedSize(true);
        LinearLayoutManager trLayoutManager = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);
        trListView.setLayoutManager(trLayoutManager);
        trListView.setAdapter(transactionsRVAdapter);
    }


    // activity view initialization
    private void viewInit(View view) {
        mBalanceText = view.findViewById(R.id.home_user_balance);
        mTransactionTextLink = view.findViewById(R.id.home_transactions_link);
        mTransactionTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Ссылка будет", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void userLoad(String name) {
        mUserText.setText(name);
    }

    private int userBalanceChange() {
        int bal = 0;
        for (Account o : accList) {
            bal += o.getSum();
        }
        mBalanceText.setText(String.valueOf(bal));
        return bal;
    }

    private void userLoadfromDB() {
        Cursor userCurs = db.query(VimonContract.UserEntry.TABLE_NAME,
                null, null, null,
                null, null, null);
        try {
            if (userCurs.getCount() > 0) {
                Log.d(LOG_TAG, "user get from DB");
                userCurs.moveToFirst();
                Log.d(LOG_TAG, "user movetoFirst from DB");
                userLoad(userCurs.getString(
                        userCurs.getColumnIndex(VimonContract.UserEntry.COLUMN_NAME)
                ));
            }
        } finally {
            userCurs.close();
        }
    }

    private void accountsLoadFromDB() {
        Cursor accCurs = db.query(VimonContract.AccountsEntry.TABLE_NAME,
                null, null, null,
                null, null, null);
        try {
            if (accCurs.getCount() > 0) {
                while (accCurs.moveToNext()) {
                    accList.add(new Account(
                            accCurs.getString(
                                    accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TITLE)
                            ),
                            accCurs.getString(
                                    accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TYPE)
                            ),
                            accCurs.getInt(
                                    accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_BALANCE)
                            )
                    ));
                }
            }
        } finally {
            accCurs.close();
        }
    }
}
