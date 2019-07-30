package ru.vincetti.vimoney.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.sqlite.DbHelper;
import ru.vincetti.vimoney.data.sqlite.VimonContract;

public class HomeFragment extends Fragment {
    private static String LOG_TAG = "MAIN FRAGMENT DEBUG";

    private SQLiteDatabase db;

    private TextView mUserText;
    private TextView mBalanceText;
    private int mAllBalance;

    private TextView mAcc1Type;
    private TextView mAcc1Name;
    private TextView mAcc1Balance;

    private TextView mAcc2Type;
    private TextView mAcc2Name;
    private TextView mAcc2Balance;

    private TextView mAcc3Type;
    private TextView mAcc3Name;
    private TextView mAcc3Balance;

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

        viewInit(view);
        userBalanceChange(mAllBalance);
        userLoadfromDB();

        mAllBalance = account1LoadFromDB() + account2LoadFromDB() + account3LoadFromDB();
        userBalanceChange(mAllBalance);
    }


    // activity view initialization
    private void viewInit(View view) {
        mUserText = view.findViewById(R.id.home_user_name);
        mBalanceText = view.findViewById(R.id.home_user_balance);

        mAcc1Type = view.findViewById(R.id.home_acc1_type);
        mAcc1Name = view.findViewById(R.id.home_acc1_name);
        mAcc1Balance = view.findViewById(R.id.home_acc1_balance);

        mAcc2Type = view.findViewById(R.id.home_acc2_type);
        mAcc2Name = view.findViewById(R.id.home_acc2_name);
        mAcc2Balance = view.findViewById(R.id.home_acc2_balance);

        mAcc3Type = view.findViewById(R.id.home_acc3_type);
        mAcc3Name = view.findViewById(R.id.home_acc3_name);
        mAcc3Balance = view.findViewById(R.id.home_acc3_balance);
    }

    private void userLoad(String name) {
        mUserText.setText(name);
    }

    private void userBalanceChange(int newBalance) {
        mBalanceText.setText(String.valueOf(newBalance));
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

    private void acc1Load(String name, String type, int balance) {
        mAcc1Name.setText(name);
        mAcc1Type.setText(type);
        mAcc1Balance.setText(String.valueOf(balance));
    }

    private void acc2Load(String name, String type, int balance) {
        mAcc2Name.setText(name);
        mAcc2Type.setText(type);
        mAcc2Balance.setText(String.valueOf(balance));
    }

    private void acc3Load(String name, String type, int balance) {
        mAcc3Name.setText(name);
        mAcc3Type.setText(type);
        mAcc3Balance.setText(String.valueOf(balance));
    }

    private int account1LoadFromDB() {
        int num = 1;
        int balance = 0;
        Cursor accCurs = db.query(VimonContract.AccountsEntry.TABLE_NAME,
                null, null, null,
                null, null, null);
        try {
            if (accCurs.getCount() > 0) {
                Log.d(LOG_TAG, "Accounts1 get from DB");
                accCurs.move(num);
                Log.d(LOG_TAG, "Accounts1 " + num + " from DB");
                balance = accCurs.getInt(
                        accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_BALANCE)
                );
                Log.d(LOG_TAG, "Accounts1 balanca " + balance);
                acc1Load(accCurs.getString(
                        accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TITLE)
                        ),
                        accCurs.getString(
                                accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TYPE)
                        ),
                        balance
                );
            }
        } finally {
            accCurs.close();
        }
        return balance;
    }

    private int account2LoadFromDB() {
        int num = 2;
        int balance = 0;
        Cursor accCurs = db.query(VimonContract.AccountsEntry.TABLE_NAME,
                null, null, null,
                null, null, null);
        try {
            if (accCurs.getCount() > 0) {
                Log.d(LOG_TAG, "Accounts1 get from DB");
                accCurs.move(num);
                Log.d(LOG_TAG, "Accounts1 " + num + " from DB");
                balance = accCurs.getInt(
                        accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_BALANCE)
                );
                Log.d(LOG_TAG, "Accounts1 balanca " + balance);
                acc2Load(accCurs.getString(
                        accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TITLE)
                        ),
                        accCurs.getString(
                                accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TYPE)
                        ),
                        balance
                );
            }
        } finally {
            accCurs.close();
        }
        return balance;
    }

    private int account3LoadFromDB() {
        int num = 3;
        int balance = 0;
        Cursor accCurs = db.query(VimonContract.AccountsEntry.TABLE_NAME,
                null, null, null,
                null, null, null);
        try {
            if (accCurs.getCount() > 0) {
                Log.d(LOG_TAG, "Accounts1 get from DB");
                accCurs.move(num);
                Log.d(LOG_TAG, "Accounts1 " + num + " from DB");
                balance = accCurs.getInt(
                        accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_BALANCE)
                );
                Log.d(LOG_TAG, "Accounts1 balanca " + balance);
                acc3Load(accCurs.getString(
                        accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TITLE)
                        ),
                        accCurs.getString(
                                accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TYPE)
                        ),
                        balance
                );
            }
        } finally {
            accCurs.close();
        }
        return balance;
    }
}
