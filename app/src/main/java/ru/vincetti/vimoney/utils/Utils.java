package ru.vincetti.vimoney.utils;

import java.util.HashMap;
import java.util.List;

import ru.vincetti.vimoney.data.models.AccountListModel;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.models.CurrencyModel;

public class Utils {

    public static HashMap<Integer, String> genCurrencyCodeHash(List<CurrencyModel> t){
        HashMap<Integer, String> hash = new HashMap<>();
        for (CurrencyModel o: t) {
            hash.put(o.getCode(), o.getSymbol());
        }
        return hash;
    }

    public static HashMap<Integer, String> genCurrencyIdHash(List<AccountListModel> t){
        HashMap<Integer, String> hash = new HashMap<>();
        for (AccountListModel o: t) {
            hash.put(o.getId(), o.getSymbol());
        }
        return hash;
    }

    public static HashMap<Integer, String> genAccountsHash(List<AccountModel> t){
        HashMap<Integer, String> hash = new HashMap<>();
        for (AccountModel o: t) {
            hash.put(o.getId(), o.getName());
        }
        return hash;
    }
}
