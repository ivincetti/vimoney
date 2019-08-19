package ru.vincetti.vimoney.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;

public class AddCheckActivity extends AppCompatActivity {
    private final static String EXTRA_CHECK_ID = "Extra_check_id";
    private final static int DEFAULT_CHECK_ID = -1;

    private int mCheckId = DEFAULT_CHECK_ID;
    private int mSum = 0;
    private AppDatabase mDb;
    private EditText checkName;
    private Button btnSave;
    private ImageView btnDelete;
    private RadioGroup typeRadioGroup;

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
        initView();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_CHECK_ID)) {
            mCheckId = intent.getIntExtra(EXTRA_CHECK_ID, DEFAULT_CHECK_ID);
            btnSave.setText(getString(R.string.add_btn_update));
            btnDelete.setVisibility(View.VISIBLE);

            LiveData<AccountModel> checkLD = mDb.accountDao().loadAccountById(mCheckId);
            checkLD.observe(this, new Observer<AccountModel>() {
                @Override
                public void onChanged(AccountModel accModel) {
                    checkLD.removeObserver(this);
                    checkName.setText(String.valueOf(accModel.getName()));
                    mSum = accModel.getSum();
                    typeLoad(accModel.getType());
                }
            });
        }
    }

    private void initView() {
        checkName = findViewById(R.id.add_check_name);
        btnSave = findViewById(R.id.add_check_save_btn);
        btnDelete = findViewById(R.id.add_check_navigation_delete_btn);
        typeRadioGroup = findViewById(R.id.radioGroup);
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

    // save transaction logic
    private void save() {
        AccountModel tmp = new AccountModel(
                String.valueOf(checkName.getText()),
                typeEntered(),
                mSum
        );

        if (mCheckId != DEFAULT_CHECK_ID) {
            // update logic
            tmp.setId(mCheckId);
            AppExecutors.getsInstance().diskIO().execute(
                    () -> mDb.accountDao().updateAccount(tmp));
        } else {
            // new transaction
            AppExecutors.getsInstance().diskIO().execute(
                    () -> mDb.accountDao().insertAccount(tmp));
        }
        finish();
    }

    // delete transaction logic
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
                                        () -> mDb.accountDao().deleteAccountById(mCheckId));
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
