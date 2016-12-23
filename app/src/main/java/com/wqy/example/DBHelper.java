package com.wqy.example;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wqy on 16-12-21.
 */

public class DBHelper extends SQLiteOpenHelper {
    private volatile static DBHelper instance;
    private static final String CREATE_TABLE = "CREATE TABLE " +
            DBContract.User.TABLE_NAME + "("  +
            DBContract.User._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DBContract.User.USERNAME + " TEXT NOT NULL UNIQUE," +
            DBContract.User.PASSWORD + " TEXT NOT NULL)";

    private DBHelper(Context context) {
        super(context, DBContract.DATABASE_NAME, null, DBContract.DATABASE_VERSION);
    }

    public static void initialize(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
    }

    public static DBHelper getInstance() throws Exception {
        if (instance == null) {
            throw new Exception(DBContract.class.getName() + ": hasn't been initialized, please initialize it before use it");
        }
        return instance;
    }

    public static void terminate() {
        instance.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.User.CREATE_TABLE);
        db.execSQL(DBContract.Test.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
