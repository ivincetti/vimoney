package ru.vincetti.vimoney.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

interface TransactionFragmentInterface {
    View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);
    // views initialization
    void initFragmentViews(View view);
    // save transaction logic
    void setTypeAction();
}
