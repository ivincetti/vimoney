package ru.vincetti.vimoney.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.Account;
import ru.vincetti.vimoney.data.CardsListViewAdapter;
import ru.vincetti.vimoney.data.sqlite.DbHelper;
import ru.vincetti.vimoney.data.sqlite.VimonContract;

public class HomeFragment extends Fragment {
    private static String LOG_TAG = "MAIN FRAGMENT DEBUG";

    private SQLiteDatabase db;
    private ArrayList<Account> accList;

    private TextView mUserText;
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

        viewInit(view);
        mAllBalance = userBalanceChange();
        // userLoadfromDB();

        accountsLoadFromDB();

        CardsListViewAdapter adapter = new CardsListViewAdapter(accList);

        RecyclerView listView = view.findViewById(R.id.home_cards_recycle_view);
        listView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        listView.setLayoutManager(layoutManager);

        listView.setAdapter(adapter);
    }


    // activity view initialization
    private void viewInit(View view) {
        mBalanceText = view.findViewById(R.id.home_user_balance);
    }

    private void userLoad(String name) {
        mUserText.setText(name);
    }

    private int userBalanceChange() {
        int bal = 0;
        for (Account o: accList) {
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
                Log.d(LOG_TAG, "Accounts get from DB " + accCurs.getCount());
                while (accCurs.moveToNext()) {
                    Log.d(LOG_TAG, "Accounts " + accCurs.getPosition());
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
