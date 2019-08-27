package ru.vincetti.vimoney.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.models.TransactionModel;

public class TransactionIncomeFragment extends TransactionFragment implements TransactionFragmentInterface {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_spent, container, false);
    }

    @Override
    public void initFragmentViews(View view) {

    }

    @Override
    public void setTypeAction() {
        typeAction = TransactionModel.TRANSACTION_TYPE_INCOME;
    }
}


