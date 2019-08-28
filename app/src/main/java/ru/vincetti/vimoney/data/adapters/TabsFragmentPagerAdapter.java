package ru.vincetti.vimoney.data.adapters;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ru.vincetti.vimoney.transaction.TransactionDebtFragment;
import ru.vincetti.vimoney.transaction.TransactionIncomeFragment;
import ru.vincetti.vimoney.transaction.TransactionSpentFragment;
import ru.vincetti.vimoney.transaction.TransactionTransferFragment;

public class TabsFragmentPagerAdapter extends FragmentPagerAdapter {
    private final Bundle fragmentBundle;

    public TabsFragmentPagerAdapter(FragmentManager fm, Bundle data) {
        super(fm);
        fragmentBundle = data;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "-";
            case 1:
                return "+";
            case 2:
                return "=";
//            case 3:
//                return "|";
            default:
                return null;
        }
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                final TransactionSpentFragment spentFragment = new TransactionSpentFragment();
                spentFragment.setArguments(this.fragmentBundle);
                return spentFragment;
            case 1:
                final TransactionIncomeFragment incomeFragment = new TransactionIncomeFragment();
                incomeFragment.setArguments(this.fragmentBundle);
                return incomeFragment;
            case 2:
                final TransactionTransferFragment transferFragment = new TransactionTransferFragment();
                transferFragment.setArguments(this.fragmentBundle);
                return transferFragment;
//            case 3:
//                return new TransactionDebtFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
