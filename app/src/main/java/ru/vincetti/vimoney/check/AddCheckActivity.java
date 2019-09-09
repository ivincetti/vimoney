package ru.vincetti.vimoney.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.models.CurrencyModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.transaction.TransactionViewModel;

import static ru.vincetti.vimoney.utils.Utils.viewModelUpdate;

public class AddCheckActivity extends AppCompatActivity {
    private final static String EXTRA_CHECK_ID = "Extra_check_id";
    private final static int DEFAULT_CHECK_ID = -1;

    private int mCheckId = DEFAULT_CHECK_ID;
    private int mSum = 0;
    private int checkCurrency;
    private AppDatabase mDb;
    private TransactionViewModel viewModel;
    private EditText checkName;
    private Button btnSave;
    private ImageView btnDelete, btnFromArhive;
    private RadioGroup typeRadioGroup;
    Spinner curSpinner;

    public static void start(Context context) {
        Intent starter = new Intent(context, AddCheckActivity.class);
        context.startActivity(starter);
    }

    public static void start(Context context, int id) {
        Intent starter = new Intent(context, AddCheckActivity.class);
        starter.putExtra(EXTRA_CHECK_ID, id);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_check);

        findViewById(R.id.setting_navigation_back_btn).setOnClickListener(view -> finish());

        mDb = AppDatabase.getInstance(this);
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        initView();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_CHECK_ID)) {
            mCheckId = intent.getIntExtra(EXTRA_CHECK_ID, DEFAULT_CHECK_ID);
            btnSave.setText(getString(R.string.add_btn_update));

            LiveData<AccountModel> checkLD = mDb.accountDao().loadAccountById(mCheckId);
            checkLD.observe(this, new Observer<AccountModel>() {
                @Override
                public void onChanged(AccountModel accModel) {
                    checkLD.removeObserver(this);
                    checkName.setText(String.valueOf(accModel.getName()));
                    mSum = accModel.getSum();
                    checkCurrency = accModel.getCurrency();
                    typeLoad(accModel.getType());

                    if (accModel.isArhive()) {
                        btnFromArhive.setVisibility(View.VISIBLE);
                    } else {
                        btnDelete.setVisibility(View.VISIBLE);
                    }
                    currencyEntered();
                }
            });
        }
        spinnerInit();
    }

    private void initView() {
        checkName = findViewById(R.id.add_check_name);
        btnSave = findViewById(R.id.add_check_save_btn);
        btnSave.setOnClickListener(view -> save());
        btnDelete = findViewById(R.id.add_check_navigation_delete_btn);
        btnDelete.setOnClickListener(view -> delete());
        btnFromArhive = findViewById(R.id.add_check_navigation_from_archive_btn);
        btnFromArhive.setOnClickListener(view -> restore());
        typeRadioGroup = findViewById(R.id.radioGroup);

        findViewById(R.id.add_check_navigation_add_btn)
                .setOnClickListener(view -> save());
        findViewById(R.id.setting_navigation_back_btn)
                .setOnClickListener(view -> showUnsavedDialog());
    }

    // radioButton clicked option selected
    private String typeEntered() {
        int checkedId = typeRadioGroup.getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.add_check_type_cash:
                return AccountModel.ACCOUNT_TYPE_CASH;
            case R.id.add_check_type_debit:
                return AccountModel.ACCOUNT_TYPE_DEBIT;
            case R.id.add_check_type_credit:
                return AccountModel.ACCOUNT_TYPE_CREDIT;
        }
        return AccountModel.ACCOUNT_TYPE_CASH;
    }

    // radioButton option load
    private void typeLoad(String type) {
        switch (type) {
            case AccountModel.ACCOUNT_TYPE_CASH:
                typeRadioGroup.check(R.id.add_check_type_cash);
                break;
            case AccountModel.ACCOUNT_TYPE_DEBIT:
                typeRadioGroup.check(R.id.add_check_type_debit);
                break;
            case AccountModel.ACCOUNT_TYPE_CREDIT:
                typeRadioGroup.check(R.id.add_check_type_credit);
                break;
            default:
                typeRadioGroup.check(R.id.add_check_type_cash);
                break;
        }
    }

    private void spinnerInit() {
        curSpinner = findViewById(R.id.add_check_currency_spinner);
        LiveData<List<CurrencyModel>> ldCurrencyList = mDb.currentDao().loadAllCurrency();
        ldCurrencyList.observe(this, currencyModels -> {
            // adapter init
            ArrayAdapter<CurrencyModel> adapter =
                    new ArrayAdapter<>(AddCheckActivity.this, R.layout.spinner_item, currencyModels);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            curSpinner.setAdapter(adapter);
            currencyEntered();
        });

        curSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                checkCurrency = ((CurrencyModel) adapterView.getSelectedItem()).getCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });

    }

    // spinner load option selected
    private void currencyEntered() {
        LiveData<CurrencyModel> ldCurrency = mDb.currentDao().loadCurrencyByCode(checkCurrency);
        ldCurrency.observe(this, new Observer<CurrencyModel>() {
            @Override
            public void onChanged(CurrencyModel currencyModel) {
                ldCurrency.removeObserver(this);
                int pos = getIndex(currencyModel);
                curSpinner.setSelection(pos);
            }
        });
    }

    // get index in spinner
    private int getIndex(CurrencyModel tmpAcc) {
        if (tmpAcc != null) {
            for (int i = 0; i < curSpinner.getCount(); i++) {
                if (curSpinner.getItemAtPosition(i).toString().equals(tmpAcc.getSymbol())) {
                    return i;
                }
            }
        }
        return 0;
    }

    // save account logic
    private void save() {
        AccountModel tmp = new AccountModel(
                String.valueOf(checkName.getText()),
                typeEntered(),
                mSum,
                checkCurrency
        );

        if (mCheckId != DEFAULT_CHECK_ID) {
            // update logic
            tmp.setId(mCheckId);
            AppExecutors.getsInstance().diskIO().execute(
                    () -> {
                        mDb.accountDao().updateAccount(tmp);
                        viewModelUpdate(mDb, viewModel);
                    });
        } else {
            // new transaction
            AppExecutors.getsInstance().diskIO().execute(
                    () -> {
                        mDb.accountDao().insertAccount(tmp);
                        viewModelUpdate(mDb, viewModel);
                    });
        }
        finish();
    }

    // restore from archive account logic
    private void restore() {
        if (mCheckId != DEFAULT_CHECK_ID) {
            AppExecutors.getsInstance().diskIO().execute(
                    () -> {
                        mDb.accountDao().fromArchiveAccountById(mCheckId);
                        viewModelUpdate(mDb, viewModel);
                    });
            finish();
        }
    }

    // archive account logic
    private void delete() {
        if (mCheckId != DEFAULT_CHECK_ID) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage(R.string.check_delete_alert_question)
                    .setNegativeButton(R.string.check_delete_alert_negative,
                            (dialogInterface, i) -> {
                                if (dialogInterface != null) dialogInterface.dismiss();
                            })
                    .setPositiveButton(R.string.check_delete_alert_positive,
                            (dialogInterface, i) -> {
                                // delete query
                                AppExecutors.getsInstance().diskIO().execute(
                                        () -> {
                                            mDb.accountDao().archiveAccountById(mCheckId);
                                            viewModelUpdate(mDb, viewModel);
                                        });
                                finish();

                            });
            builder.create().show();
        }
    }

    // not saved transaction cancel dialog
    private void showUnsavedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.check_add_alert_question)
                .setNegativeButton(R.string.check_add_alert_negative,
                        (dialogInterface, i) -> finish())
                .setPositiveButton(R.string.check_add_alert_positive,
                        (dialogInterface, i) -> {
                            if (dialogInterface != null) dialogInterface.dismiss();
                        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        showUnsavedDialog();
    }
}
