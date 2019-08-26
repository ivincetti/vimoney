package ru.vincetti.vimoney.data.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ru.vincetti.vimoney.transaction.TransactionDebtFragment;
import ru.vincetti.vimoney.transaction.TransactionIncomeFragment;
import ru.vincetti.vimoney.transaction.TransactionSpentFragment;
import ru.vincetti.vimoney.transaction.TransactionTransferFragment;

public class TabsFragmentPagerAdapter extends FragmentPagerAdapter {
    public TabsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
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
            case 3:
                return "|";
            default:
                return null;
        }
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new TransactionSpentFragment();
            case 1:
                return new TransactionIncomeFragment();
            case 2:
                return new TransactionTransferFragment();
            case 3:
                return new TransactionDebtFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
