package com.wqy.example;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.wqy.cuery.Query;
import com.wqy.cuery.ResultSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class CueryTest {
    private static final String TAG = CueryTest.class.getSimpleName();
    private Context context;
    private DBHelper dbHelper;

    @Before
    public void initialize() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
        dbHelper = DBHelper.getInstance();
    }

    @Test
    public void testSelect() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String username = Common.makeString(10);
        String password = Common.makeString(10);
        db.execSQL("INSERT INTO User(username,password) VALUES('" + username + "','" + password + "')");
        Query query = new Query();
        ResultSet rs = query.select("username", "password")
                .table("User")
                .startWhere()
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .endWhere()
                .execute(db);
        Log.d(TAG, "testSelect: " + query.getSql());
        System.out.println(query.getSql());
        Cursor cursor = rs.getCursor();
        cursor.moveToFirst();

        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());
        assertEquals(2, cursor.getColumnCount());
        assertEquals(username, cursor.getString(cursor.getColumnIndex("username")));
        assertEquals(password, cursor.getString(cursor.getColumnIndex("password")));
    }

    @Test
    public void testInsert() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String username = Common.makeString(10);
        String password = Common.makeString(10);
        Query query = new Query();
        ResultSet rs = query.insert()
                .table("User")
                .columns("username", "password")
                .values(username, password)
                .execute(db);
        assertFalse(rs.isEmpty());
        assertTrue(rs.getRowId() > 0);

        Cursor c = db.rawQuery("SELECT username, password FROM User WHERE username='" + username + "' AND password='" + password + "'", null);
        c.moveToFirst();

        assertEquals(1, c.getCount());
        assertEquals(username, c.getString(c.getColumnIndex("username")));
        assertEquals(password, c.getString(c.getColumnIndex("password")));
    }

    @Test
    public void testUpdate() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String username = Common.makeString(10);
        String password = Common.makeString(10);
        String newPassword = Common.makeString(10);
        db.execSQL("INSERT INTO User(username,password) VALUES('" + username + "','" + password + "')");
        Query query = new Query();
        ResultSet rs = query.update()
                .table("User")
                .set("password", newPassword)
                .startWhere()
                .whereEqualTo("username", username)
                .endWhere()
                .execute(db);

        assertFalse(rs.isEmpty());
        assertEquals(1, rs.getRowAffected());
    }

    @Test
    public void testDelete() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String username = Common.makeString(10);
        String password = Common.makeString(10);
        db.execSQL("INSERT INTO User(username,password) VALUES('" + username + "','" + password + "')");
        Query query = new Query();
        ResultSet rs = query.delete()
                .table("User")
                .startWhere()
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .endWhere()
                .execute(db);
        assertFalse(rs.isEmpty());
        assertEquals(1, rs.getRowAffected());

        Cursor c = db.rawQuery("SELECT * FROM User WHERE username='" + username + "' AND password='" + password + "'", null);
        assertEquals(0, c.getCount());
    }

}
