package ru.vincetti.vimoney.transaction.debt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.transaction.TransactionFragmentInterface;

public class TransactionDebtFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_spent, container, false);
    }

}
