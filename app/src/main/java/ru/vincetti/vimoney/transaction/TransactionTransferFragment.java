package ru.vincetti.vimoney.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.Map;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.utils.LogicMath;

public class TransactionTransferFragment extends TransactionFragment implements TransactionFragmentInterface {
    private EditText txtSumTo;
    private TextView txtCurrencyTo, txtAccountTo;
    private Spinner accSpinnerTo;
    private int accIdTo = TransactionModel.DEFAULT_ID;
    private long idTo = TransactionModel.DEFAULT_ID;
    private int accNew;
    private TransactionModel nestedTrans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transfer, container, false);
    }

    @Override
    public void initFragmentViews(View view) {
        txtSumTo = view.findViewById(R.id.add_sum_to);
        txtCurrencyTo = view.findViewById(R.id.add_acc_cur_to);
        txtAccountTo = view.findViewById(R.id.add_acc_name_to);
        txtAccountTo.setOnClickListener(view13 -> {
            accSpinnerTo.performClick();
        });

        nestedTrans = new TransactionModel();
    }

    @Override
    public void initFragmentLogic() {
        if (mTrans.getExtraKey().equals(TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY)
                && Integer.valueOf(mTrans.getExtraValue()) > 0) {
            LiveData<TransactionModel> trTransfer = AppDatabase.getInstance(getActivity()).transactionDao()
                    .loadTransactionById(Integer.valueOf(mTrans.getExtraValue()));
            trTransfer.observe(getActivity(),
                    transactionModel -> {
                        if (transactionModel != null) {
                            nestedTrans.copyFrom(transactionModel);
                            accNew = nestedTrans.getAccountId();
                            accIdTo = nestedTrans.getAccountId();
                            txtSumTo.setText(String.valueOf(transactionModel.getSum()));
                            txtAccountTo.setText(notArchiveAccountNames.get(transactionModel.getAccountId()));
                            txtCurrencyTo.setText(curSymbolsId.get(transactionModel.getAccountId()));
                        }
                    });
        }
    }

    @Override
    public void setTypeAction() {
        typeAction = TransactionModel.TRANSACTION_TYPE_TRANSFER;
    }

    @Override
    void spinner2Init(View view) {
        accSpinnerTo = view.findViewById(R.id.add_acc_list_to);
        ArrayList<String> accounts2Array = new ArrayList<>();
        accounts2Array.add("Выберите счет");
        for (Map.Entry<Integer, String> entry : accountNames.entrySet()) {
            accounts2Array.add(entry.getValue());
        }
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accounts2Array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        accSpinnerTo.setAdapter(adapter);
        accSpinnerTo.setSelected(false);
        accSpinnerTo.setSelection(TransactionModel.DEFAULT_ID, false);
        accSpinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position != TransactionModel.DEFAULT_ID) {
                    int id = TransactionModel.DEFAULT_ID;
                    for (Map.Entry<Integer, String> entry : accountNames.entrySet()) {
                        if (adapter.getItem(position).equals(entry.getValue())) {
                            id = entry.getKey();
                        }
                    }
                    if (id != TransactionModel.DEFAULT_ID) {
                        txtCurrencyTo.setText(curSymbolsId.get(id));
                        txtAccountTo.setText(notArchiveAccountNames.get(id));
                        accIdTo = id;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });


    }

    @Override
        // save transaction logic
    void save(int typeAction) {
        if (mTrans.getAccountId() != TransactionModel.DEFAULT_ID && accIdTo != TransactionModel.DEFAULT_ID && !txtSumTo.getText().toString().equals("")) {
            mTrans.setDescription(String.valueOf(txtName.getText()));
            mTrans.setDate(mDate);
            mTrans.setType(typeAction);
            mTrans.setSum(Float.valueOf(txtSum.getText().toString()));

            mTrans.setExtraKey("transfer_transaction_id");
            nestedTrans.setSum(Float.valueOf(txtSumTo.getText().toString()));
            nestedTrans.setAccountId(accIdTo);
            nestedTrans.setExtraKey(TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY);
            nestedTrans.setExtraValue(String.valueOf(mTrans.getId()));
            nestedTrans.setDate(mDate);
            nestedTrans.setType(TransactionModel.TRANSACTION_TYPE_INCOME);
            nestedTrans.setSystem(true);

            if (mTrans.getId() != TransactionModel.DEFAULT_ID) {
                // update logic
                transferUpdate();
            } else {
                // new transaction
                transferInsert();
            }
            // update balance for current (accId) account
            LogicMath.accountBalanceUpdateById(mDb, mTrans.getAccountId());
            LogicMath.accountBalanceUpdateById(mDb, accIdTo);
            // update balance for account updated
            if (mTrans.getAccountId() != accOld) {
                LogicMath.accountBalanceUpdateById(mDb, accOld);
            }
            if (accIdTo != accNew) {
                LogicMath.accountBalanceUpdateById(mDb, accNew);
            }

            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.add_check_no_account_warning), Toast.LENGTH_SHORT).show();
        }
    }

    private void transferUpdate() {
        nestedTrans.setId(Integer.valueOf(mTrans.getExtraValue()));
        AppExecutors.getsInstance().diskIO().execute(
                () -> mDb.transactionDao().updateTransaction(nestedTrans));
        AppExecutors.getsInstance().diskIO().execute(
                () -> mDb.transactionDao().updateTransaction(mTrans));
    }

    private void transferInsert() {
        AppExecutors.getsInstance().diskIO().execute(
                () -> idTo = mDb.transactionDao().insertTransaction(nestedTrans));

        AppExecutors.getsInstance().diskIO().execute(() -> {
            mTrans.setExtraValue(String.valueOf(idTo));
            mDb.transactionDao().insertTransaction(mTrans);
        });
    }
}

