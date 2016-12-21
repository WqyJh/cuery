package com.wqy.cuery;

import android.provider.BaseColumns;

/**
 * Created by wqy on 16-12-21.
 */

public final class DBContract {
    private DBContract() {}

    public static final String DATABASE_NAME = "cuery_test.db";
    public static final int DATABASE_VERSION = 1;

    public static class User implements BaseColumns {
        public static final String TABLE_NAME = "User";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
    }
}
