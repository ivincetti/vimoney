package ru.vincetti.vimoney.data.sqlite;

import android.provider.BaseColumns;

public class VimonContract {

    private VimonContract(){
    }

    public static final class UserEntry implements BaseColumns{
        public static final String TABLE_NAME = "user";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_NAME = "name";
    }

    public static final class AccountsEntry implements BaseColumns{
        public static final String TABLE_NAME = "accounts";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ACCOUNT_ID = "acc_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_INSTRUMENT = "instrument";
        public static final String COLUMN_BALANCE = "balance";
    }

    public static final class ConfigEntry implements BaseColumns{
        public static final String TABLE_NAME = "config";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_CONFIG_KEY_NAME = "key";
        public static final String CONFIG_KEY_NAME_DATE_EDIT = "date_edit";
        public static final String COLUMN_CONFIG_KEY_VALUE = "value";
    }
}
