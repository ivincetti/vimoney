package ru.vincetti.vimoney.transaction;

import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.ViewModelProviders;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.check.CheckViewModel;
import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.utils.LogicMath;

public class TransactionTransferFragment extends TransactionFragment implements TransactionFragmentInterface {
    private EditText txtSumTo;
    private TextView txtCurrencyTo, txtAccountTo;
    private Spinner accSpinnerTo;
    private int accIdTo = TransactionModel.DEFAULT_ID;
    private long idTo = TransactionModel.DEFAULT_ID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transfer, container, false);
    }

    @Override
    public void initFragmentViews(View view) {
        initViewTransfer(view);
        spinnerToInit(view);
        if (mTrans.getId() != TransactionModel.DEFAULT_ID) {
            Log.d("DEBUG", "LOAD date transfer "
                    + mTrans.getExtraKey() + " "
                    + mTrans.getExtraValue());
        }
    }

    @Override
    public void setTypeAction() {
        typeAction = TransactionModel.TRANSACTION_TYPE_TRANSFER;
    }

    private void initViewTransfer(View view) {
        txtSumTo = view.findViewById(R.id.add_sum_to);
        txtCurrencyTo = view.findViewById(R.id.add_acc_cur_to);
        txtAccountTo = view.findViewById(R.id.add_acc_name_to);
        txtAccountTo.setOnClickListener(view13 -> {
            accSpinnerTo.setVisibility(View.VISIBLE);
            accSpinnerTo.performClick();
        });
    }

    private void spinnerToInit(View view) {
        accSpinnerTo = view.findViewById(R.id.add_acc_list_to);
        CheckViewModel accViewModel = ViewModelProviders.of(getActivity()).get(CheckViewModel.class);
        accViewModel.getAccounts().observe(this, accountModels -> {
            // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
            ArrayAdapter<AccountModel> adapter =
                    new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accountModels);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Применяем адаптер к элементу spinner
            accSpinnerTo.setAdapter(adapter);

            accSpinnerTo.setSelected(false);  // must
            accSpinnerTo.setSelection(0, true);  //must
            accSpinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    accIdTo = ((AccountModel) adapterView.getSelectedItem()).getId();
                    txtCurrencyTo.setText(curSymbolsId.get(accIdTo));
                    txtAccountTo.setText(notArchiveAccountNames.get(accIdTo));
                    accSpinnerTo.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    accSpinnerTo.setVisibility(View.INVISIBLE);
                }
            });
        });

    }

    @Override
        // save transaction logic
    void save(int typeAction) {
        if (mTrans.getAccountId() != TransactionModel.DEFAULT_ID) {
            mTrans.setDescription(String.valueOf(txtName.getText()));
            mTrans.setDate(mDate);
            mTrans.setType(typeAction);
            mTrans.setSum(Float.valueOf(txtSum.getText().toString()));

            if (mTrans.getId() != TransactionModel.DEFAULT_ID) {
                // update logic
                transferUpdate();
            } else {
                // new transaction
                transferInsert();
            }
            // update balance for current (accId) account
            AppExecutors.getsInstance().diskIO().execute(
                    () -> LogicMath.accountBalanceUpdateById(getActivity(), mTrans.getAccountId()));
            AppExecutors.getsInstance().diskIO().execute(
                    () -> LogicMath.accountBalanceUpdateById(getActivity(), accIdTo));

            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.add_check_no_account_warning), Toast.LENGTH_SHORT).show();
        }
    }

    private void transferUpdate() {
        mTrans.setExtraKey("transfer_transaction_id");

        TransactionModel nestedTrans = new TransactionModel();
        nestedTrans.setId(Integer.valueOf(mTrans.getExtraValue()));
        nestedTrans.setSum(Float.valueOf(txtSumTo.getText().toString()));
        nestedTrans.setAccountId(accIdTo);
        nestedTrans.setExtraKey(TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY);
        nestedTrans.setExtraValue(String.valueOf(mTrans.getId()));
        nestedTrans.setDate(mDate);
        nestedTrans.setType(TransactionModel.TRANSACTION_TYPE_INCOME);
        nestedTrans.setSystem(true);

        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d("DEBUG","работаем с записью " + nestedTrans.getId());
                //mDb.transactionDao().updateTransaction(nestedTrans);
            }
        });
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d("DEBUG", "before save " + mTrans.toString());
                mDb.transactionDao().updateTransaction(mTrans);
            }
        });
    }

    private void transferInsert() {
        mTrans.setExtraKey("transfer_transaction_id");

        TransactionModel nestedTrans = new TransactionModel();
        nestedTrans.setSum(Float.valueOf(txtSumTo.getText().toString()));

        nestedTrans.setAccountId(accIdTo);
        nestedTrans.setExtraKey(TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY);
        nestedTrans.setType(TransactionModel.TRANSACTION_TYPE_INCOME);
        nestedTrans.setDate(mDate);
        nestedTrans.setExtraValue(String.valueOf(mTrans.getId()));
        nestedTrans.setSystem(true);

        AppExecutors.getsInstance().diskIO().execute(
                () -> idTo = mDb.transactionDao().insertTransaction(nestedTrans));

        AppExecutors.getsInstance().diskIO().execute(() -> {
            mTrans.setExtraValue(String.valueOf(idTo));
            mDb.transactionDao().insertTransaction(mTrans);
        });
    }
}

