package ru.vincetti.vimoney.transaction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.check.CheckViewModel;
import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.AccountModel;
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
        curSymbolsId = new HashMap<>();
        accountNames = new HashMap<>();
        viewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
        viewModel.getCurrencyIdSymbols().observe(getViewLifecycleOwner(), integerStringHashMap -> {
            curSymbolsId = integerStringHashMap;
        });

        viewModel.getAccountNames().observe(getViewLifecycleOwner(), integerStringHashMap -> {
            accountNames = integerStringHashMap;
        });

        viewModel.getNotArchiveAccountNames().observe(getViewLifecycleOwner(), integerStringHashMap -> {
            notArchiveAccountNames = integerStringHashMap;
        });
        spinnerInit(view);
        initFragmentViews(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTypeAction();
        viewModel.getTransaction().observe(getViewLifecycleOwner(), transactionModel -> {
            mTrans.copyFrom(transactionModel);
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
            } else if (transactionModel.getAccountId() != TransactionModel.DEFAULT_ID) {
                txtAccount.setText(notArchiveAccountNames.get(transactionModel.getAccountId()));
                txtCurrency.setText(curSymbolsId.get(transactionModel.getAccountId()));
                initFragmentLogic();
            } else {
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
            accSpinner.setVisibility(View.VISIBLE);
            accSpinner.performClick();
        });
    }

    void initFragmentViews(View view) {
    }

    void initFragmentLogic() {
    }

    void setTypeAction() {}

    void spinnerInit(View view) {
        accSpinner = view.findViewById(R.id.add_acc_list);
        CheckViewModel accViewModel = ViewModelProviders.of(getActivity()).get(CheckViewModel.class);
        accViewModel.getAccounts().observe(this, accountModels -> {
            // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
            ArrayAdapter<AccountModel> adapter =
                    new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accountModels);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Применяем адаптер к элементу spinner
            accSpinner.setAdapter(adapter);

            accSpinner.setSelected(false);  // must
            accSpinner.setSelection(0, true);  //must
            accSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    int id = ((AccountModel) adapterView.getSelectedItem()).getId();
                    mTrans.setAccountId(id);
                    txtCurrency.setText(curSymbolsId.get(id));
                    txtAccount.setText(notArchiveAccountNames.get(id));
                    accSpinner.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    accSpinner.setVisibility(View.INVISIBLE);
                }
            });
        });

    }

    // save transaction logic
    void save(int typeAction) {
        Log.d("DEBUG", "before save " + mTrans.toString());
        if (mTrans.getAccountId() != TransactionModel.DEFAULT_ID) {
            mTrans.setDescription(String.valueOf(txtName.getText()));
            mTrans.setDate(mDate);
            mTrans.setType(typeAction);
            mTrans.setSum(Float.valueOf(txtSum.getText().toString()));

            if (mTrans.getId() != TransactionModel.DEFAULT_ID) {
                // update logic
                AppExecutors.getsInstance().diskIO().execute(
                        () -> mDb.transactionDao().updateTransaction(mTrans));
            } else {
                // new transaction
                AppExecutors.getsInstance().diskIO().execute(
                        () -> mDb.transactionDao().insertTransaction(mTrans));
            }
            // update balance for current (accId) account
            AppExecutors.getsInstance().diskIO().execute(
                    () -> LogicMath.accountBalanceUpdateById(getActivity(), mTrans.getAccountId()));

            getActivity().finish();
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
