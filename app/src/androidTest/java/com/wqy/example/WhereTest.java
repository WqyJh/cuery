package com.wqy.example;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by wqy on 16-12-22.
 */
@RunWith(AndroidJUnit4.class)
public class WhereTest {
    private Context context;
    private DBHelper dbHelper;

    @Before
    public void initialize() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
        dbHelper = DBHelper.getInstance();
    }

    @Test
    public void testBlobEquals() {
        byte[] bytes = Common.makeString(16).getBytes();
        ContentValues cv = new ContentValues();
        cv.put("Blob", bytes);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("INSERT INTO Test(" + DBContract.Test.BLOB + ") VALUES(?)");
    }
}
