package com.wqy.cuery;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wqy on 16-12-21.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String CREATE_TABLE = "CREATE TABLE " +
            DBContract.User.TABLE_NAME + "("  +
            DBContract.User._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DBContract.User.USERNAME + " TEXT NOT NULL," +
            DBContract.User.PASSWORD + " TEXT NOT NULL)";

    public DBHelper(Context context) {
        super(context, DBContract.DATABASE_NAME, null, DBContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
