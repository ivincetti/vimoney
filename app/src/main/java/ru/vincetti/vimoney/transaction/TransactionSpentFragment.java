package ru.vincetti.vimoney.transaction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TransactionSpentFragment extends Fragment {
    private AppDatabase mDb;
    private TransactionModel mTrans;
    private HashMap<Integer, String> curSymbols;
    private HashMap<Integer, String> accountNames;
    private HashMap<Integer, String> notArchiveAccountNames;

    private TransactionViewModel viewModel;
    private TextView txtName, txtSum, txtDate, txtCurrency, txtAccount;
    private Spinner accSpinner;
    private Button btnSave;
    private Date mDate;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_spent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDb = AppDatabase.getInstance(getActivity());

        initViews(view);
        mTrans = new TransactionModel();
        curSymbols = new HashMap<>();
        accountNames = new HashMap<>();
        viewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
        viewModel.getCurrencySymbols().observe(getViewLifecycleOwner(), integerStringHashMap -> {
            curSymbols = integerStringHashMap;
            Log.d("DEBUG", "init " + curSymbols.toString());
        });

        viewModel.getAccountNames().observe(getViewLifecycleOwner(), integerStringHashMap -> {
            accountNames = integerStringHashMap;
            Log.d("DEBUG", "All acc " + accountNames.toString());
        });

        viewModel.getNotArchiveAccountNames().observe(getViewLifecycleOwner(), integerStringHashMap -> {
            notArchiveAccountNames = integerStringHashMap;
            Log.d("DEBUG", "active acc " + notArchiveAccountNames.toString());
        });

        spinnerInit(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel.getTransaction().observe(getViewLifecycleOwner(), transactionModel -> {
            mTrans.copyFrom(transactionModel);
            Log.d("DEBUG", "acc id in Fragment " + transactionModel.getAccountId());
            if (transactionModel.getId() != TransactionModel.DEFAULT_ID) {
                btnSave.setText(getString(R.string.add_btn_update));
                txtSum.setText(String.valueOf(transactionModel.getSum()));
                txtName.setText(transactionModel.getDescription());
                mDate = transactionModel.getDate();
                Log.d("DEBUG", "acc id " + transactionModel.getAccountId());
                txtAccount.setText(notArchiveAccountNames.get(transactionModel.getAccountId()));
                Log.d("DEBUG", "acc from hash " + notArchiveAccountNames.get(transactionModel.getAccountId()));
                txtDate.setText(DateFormat
                        .getDateInstance(
                                DateFormat.MEDIUM).format(transactionModel.getDate()
                        ));
            } else if (transactionModel.getAccountId() != TransactionModel.DEFAULT_ID) {
                Log.d("DEBUG", "acc id " + transactionModel.getAccountId());
                txtAccount.setText(notArchiveAccountNames.get(transactionModel.getAccountId()));
                Log.d("DEBUG", "acc from hash " + notArchiveAccountNames.get(transactionModel.getAccountId()));
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

    private void initViews(View view) {
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
        btnSave.setOnClickListener(view1 -> save());
        txtAccount = view.findViewById(R.id.add_acc_name);
        txtAccount.setOnClickListener(view13 -> {
            accSpinner.setVisibility(View.VISIBLE);
            accSpinner.performClick();
        });
    }

    private void spinnerInit(View view) {
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
                    int code = ((AccountModel) adapterView.getSelectedItem()).getCurrency();
                    mTrans.setAccountId(id);
                    txtCurrency.setText(curSymbols.get(code));
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
    private void save() {
        Log.d("DEBUG", "before save " + mTrans.toString());
        if (mTrans.getAccountId() != TransactionModel.DEFAULT_ID) {
            mTrans.setDescription(String.valueOf(txtName.getText()));
            mTrans.setDate(mDate);
            mTrans.setType(TransactionModel.TRANSACTION_TYPE_SPENT);
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

    private void showDateDialog() {
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
