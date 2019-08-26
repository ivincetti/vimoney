package ru.vincetti.vimoney.utils;

import java.util.HashMap;
import java.util.List;

import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.models.CurrencyModel;

public class Utils {

    public static HashMap<Integer, String> genCurrencyHash(List<CurrencyModel> t){
        HashMap<Integer, String> hash = new HashMap<>();
        for (CurrencyModel o: t) {
            hash.put(o.getCode(), o.getSymbol());
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
