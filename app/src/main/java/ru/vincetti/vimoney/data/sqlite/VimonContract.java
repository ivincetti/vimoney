package ru.vincetti.vimoney.data.sqlite;

import android.provider.BaseColumns;

public class VimonContract {

    private VimonContract() {
    }

    public static String DB_NAME = "vimoney.db";

    public static final class AccountsEntry implements BaseColumns {
        public static final String TABLE_NAME = "accounts";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ACCOUNT_ID = "acc_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_INSTRUMENT = "instrument";
        public static final String COLUMN_BALANCE = "balance";
    }

    public static final class ConfigEntry implements BaseColumns {
        public static final String TABLE_NAME = "config";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_CONFIG_KEY_NAME = "key";
        public static final String COLUMN_CONFIG_KEY_VALUE = "value";

        public static final String CONFIG_KEY_NAME_DATE_EDIT = "date_edit";
        public static final String CONFIG_KEY_NAME_USER_NAME = "user_name";
    }

    public static final class TransactionEntry implements BaseColumns {
        public static final String TABLE_NAME = "transactions";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TRANSACTIONS_ACCOUNT_ID = "account_id";
        public static final String COLUMN_TRANSACTIONS_NAME = "description";
        public static final String COLUMN_TRANSACTIONS_DATE = "date";
        public static final String COLUMN_TRANSACTIONS_UPDATEAT = "updated_at";
        public static final String COLUMN_TRANSACTIONS_TYPE = "type";
        public static final String COLUMN_TRANSACTIONS_SUM = "sum";
    }
}
