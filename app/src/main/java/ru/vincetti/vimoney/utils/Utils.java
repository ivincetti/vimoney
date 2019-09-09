package ru.vincetti.vimoney.utils;

import java.util.HashMap;
import java.util.List;

import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.AccountListModel;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.transaction.TransactionViewModel;

public class Utils {

    public static HashMap<Integer, String> genCurrencyIdHash(List<AccountListModel> t) {
        HashMap<Integer, String> hash = new HashMap<>();
        for (AccountListModel o : t) {
            hash.put(o.getId(), o.getSymbol());
        }
        return hash;
    }

    public static HashMap<Integer, String> genAccountsHash(List<AccountModel> t) {
        HashMap<Integer, String> hash = new HashMap<>();
        for (AccountModel o : t) {
            hash.put(o.getId(), o.getName());
        }
        return hash;
    }

    public static void viewModelUpdate(AppDatabase mDb, TransactionViewModel viewModel) {
        List<AccountModel> tmpAccList = mDb.accountDao().loadAllAccountsList();
        List<AccountModel> tmpAccNotArchList = mDb.accountDao().loadNotArhiveAccountsList();
        List<AccountListModel> tmpAccListFull = mDb.accountDao().loadAllAccountsFullList();
        AppExecutors.getsInstance().mainThreadIO().execute(() -> {
            viewModel.setAccountNames(genAccountsHash(tmpAccList));
            viewModel.setNotArchiveAccountNames(genAccountsHash(tmpAccNotArchList));
            viewModel.setCurrencyIdSymbols(genCurrencyIdHash(tmpAccListFull));
        });
    }
}
