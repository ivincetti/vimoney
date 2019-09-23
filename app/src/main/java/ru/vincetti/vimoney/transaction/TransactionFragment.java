package ru.vincetti.vimoney.transaction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.utils.LogicMath;

public class TransactionFragment extends Fragment {
    int typeAction;
    AppDatabase mDb;
    TransactionModel mTrans;
    HashMap<Integer, String> curSymbolsId;
    HashMap<Integer, String> accountNames;
    HashMap<Integer, String> notArchiveAccountNames;
    int accOld;

    LinearLayout container;
    ProgressBar progressBar;
    TransactionViewModel viewModel;
    TextView txtName, txtSum, txtDate, txtCurrency, txtAccount;
    Spinner accSpinner;
    Button btnSave;
    Date mDate;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDb = AppDatabase.getInstance(getActivity());

        initViews(view);
        mTrans = new TransactionModel();
        viewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
        viewModel.getCurrencyIdSymbols().observe(getViewLifecycleOwner(), integerStringHashMap -> {
            curSymbolsId = integerStringHashMap;
        });

        viewModel.getAccountNames().observe(getViewLifecycleOwner(), integerStringHashMap -> {
            accountNames = integerStringHashMap;
        });

        viewModel.getNotArchiveAccountNames().observe(getViewLifecycleOwner(), integerStringHashMap -> {
            notArchiveAccountNames = integerStringHashMap;
            spinnerInit(view);
            spinner2Init(view);
        });

        if (getArguments() != null && getArguments().getInt(TransactionActivity.EXTRA_TRANS_ID) > 0) {
            container.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
        initFragmentViews(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTypeAction();

        viewModel.getTransaction().observe(getViewLifecycleOwner(), transactionModel -> {
            mTrans.copyFrom(transactionModel);
            accOld = transactionModel.getAccountId();
            if (transactionModel.getId() != TransactionModel.DEFAULT_ID) {
                btnSave.setText(getString(R.string.add_btn_update));
                txtSum.setText(String.valueOf(transactionModel.getSum()));
                txtName.setText(transactionModel.getDescription());
                mDate = transactionModel.getDate();
                txtAccount.setText(notArchiveAccountNames.get(transactionModel.getAccountId()));
                txtCurrency.setText(curSymbolsId.get(transactionModel.getAccountId()));
                txtDate.setText(DateFormat
                        .getDateInstance(
                                DateFormat.MEDIUM).format(transactionModel.getDate()
                        ));
                initFragmentLogic();
                container.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            } else if (transactionModel.getAccountId() != TransactionModel.DEFAULT_ID) {
                txtAccount.setText(notArchiveAccountNames.get(transactionModel.getAccountId()));
                txtCurrency.setText(curSymbolsId.get(transactionModel.getAccountId()));
                initFragmentLogic();
            } else {
                txtAccount.setText(getResources().getString(R.string.add_no_account_text));
                initFragmentLogic();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        txtSum.setFocusableInTouchMode(true);
        txtSum.requestFocus();

        // Show Keyboard
        getActivity();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    void initViews(View view) {
        txtSum = view.findViewById(R.id.add_sum);
        txtName = view.findViewById(R.id.add_desc);
        mDate = new Date();
        txtCurrency = view.findViewById(R.id.add_acc_cur);

        txtDate = view.findViewById(R.id.add_date_txt);
        txtDate.setText(DateFormat
                .getDateInstance(DateFormat.MEDIUM).format(mDate));
        view.findViewById(R.id.add_date_block)
                .setOnClickListener(view12 -> showDateDialog());
        btnSave = view.findViewById(R.id.add_btn);
        btnSave.setOnClickListener(view1 -> save(typeAction));
        txtAccount = view.findViewById(R.id.add_acc_name);
        txtAccount.setOnClickListener(view13 -> {
            accSpinner.performClick();
        });

        container = view.findViewById(R.id.fragment_container);
        progressBar = view.findViewById(R.id.fragment_progress_bar);
    }

    void initFragmentViews(View view) {
    }

    void initFragmentLogic() {
    }

    void setTypeAction() {
    }

    void spinnerInit(View view) {
        accSpinner = view.findViewById(R.id.add_acc_list);
        ArrayList<String> accountsArray = new ArrayList<>();
        accountsArray.add(getResources().getString(R.string.add_no_account_text));
        for (Map.Entry<Integer, String> entry : notArchiveAccountNames.entrySet()) {
            accountsArray.add(entry.getValue());
        }
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accountsArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Применяем адаптер к элементу spinner
        accSpinner.setAdapter(adapter);
        accSpinner.setSelected(false);
        accSpinner.setSelection(TransactionModel.DEFAULT_ID, false);
        accSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        mTrans.setAccountId(id);
                        txtCurrency.setText(curSymbolsId.get(id));
                        txtAccount.setText(accountNames.get(id));
                        accSpinner.setTag(position);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });
    }

    void spinner2Init(View view) {

    }

    // save transaction logic
    void save(int typeAction) {
        if (mTrans.getAccountId() != TransactionModel.DEFAULT_ID) {
            if (txtSum.getText().toString().equals("")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.add_check_no_sum_warning), Toast.LENGTH_SHORT).show();
            } else {
                mTrans.setDescription(String.valueOf(txtName.getText()));
                mTrans.setDate(mDate);
                mTrans.setType(typeAction);
                mTrans.setSum(Float.valueOf(txtSum.getText().toString()));

                if (mTrans.getId() != TransactionModel.DEFAULT_ID) {
                    // update logic
                    AppExecutors.getsInstance().diskIO().execute(
                            () -> {
                                mDb.transactionDao().updateTransaction(mTrans);
                                LogicMath.accountBalanceUpdateById(mDb, accOld);
                            });
                } else {
                    // new transaction
                    AppExecutors.getsInstance().diskIO().execute(
                            () -> mDb.transactionDao().insertTransaction(mTrans));
                }
                // update balance for current (accId) account
                LogicMath.accountBalanceUpdateById(mDb, mTrans.getAccountId());
                getActivity().finish();
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.add_check_no_account_warning), Toast.LENGTH_SHORT).show();
        }
    }

    void showDateDialog() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(mDate);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (datePicker, year, month, day) -> {
                    GregorianCalendar tmpCal = new GregorianCalendar(year, month, day);
                    mDate = tmpCal.getTime();
                    txtDate.setText(DateFormat
                            .getDateInstance(DateFormat.MEDIUM).format(mDate));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
