package ru.vincetti.vimoney.transaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.check.CheckViewModel;
import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.utils.LogicMath;

public class TransactionSpentFragment extends Fragment {
    AppDatabase mDb;
    TransactionModel mTrans;

    private TextView txtName, txtSum, txtDate;
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
        spinnerInit(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final TransactionViewModel viewModel =
                ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
        viewModel.getTransaction().observe(getViewLifecycleOwner(), new Observer<TransactionModel>() {
            @Override
            public void onChanged(TransactionModel transactionModel) {
//                Log.d("DEBUG", "данные модели во фрагменте " + transactionModel.getSum()
//                        + " #" + transactionModel.getId()
//                        + "comment " + transactionModel.getDescription());
                mTrans.copyFrom(transactionModel);
                if (transactionModel.getId() != TransactionModel.DEFAULT_ID) {
                    btnSave.setText(getString(R.string.add_btn_update));
                    txtSum.setText(String.valueOf(transactionModel.getSum()));
                    txtName.setText(transactionModel.getDescription());
                    mDate = transactionModel.getDate();
                    txtDate.setText(DateFormat
                            .getDateInstance(
                                    DateFormat.MEDIUM).format(transactionModel.getDate()
                            ));
                    accountEntered();
                }
            }
        });
    }

    private void initViews(View view) {
        txtSum = view.findViewById(R.id.add_sum);
        txtSum.requestFocus();
        txtName = view.findViewById(R.id.add_desc);
        mDate = new Date();
        txtDate = view.findViewById(R.id.add_date_txt);
        txtDate.setText(DateFormat
                .getDateInstance(DateFormat.MEDIUM).format(mDate));
        view.findViewById(R.id.add_date_block)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDateDialog();
                    }
                });
        btnSave = view.findViewById(R.id.add_btn);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
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
            accountEntered();
        });

        accSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mTrans.setAccountId(((AccountModel) adapterView.getSelectedItem()).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });

    }

    // spinner load option selected
    private void accountEntered() {
        int mAccID = mTrans.getAccountId();
        if (mAccID != TransactionModel.DEFAULT_ID) {
            LiveData<AccountModel> lAcc = mDb.accountDao().loadAccountById(mAccID);
            lAcc.observe(this, new Observer<AccountModel>() {
                @Override
                public void onChanged(AccountModel accountModel) {
                    lAcc.removeObserver(this);
                    int pos = getIndex(accountModel);
                    accSpinner.setSelection(pos);
                }
            });
        }
    }

    // get index in spinner
    private int getIndex(AccountModel tmpAcc) {
        if (tmpAcc != null) {
            for (int i = 0; i < accSpinner.getCount(); i++) {
                if (accSpinner.getItemAtPosition(i).toString().equals(tmpAcc.getName())) {
                    return i;
                }
            }
        }
        return 0;
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
