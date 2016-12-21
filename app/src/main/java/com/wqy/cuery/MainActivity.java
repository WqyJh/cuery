package com.wqy.cuery;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText etUsername;
    private EditText etPassword;
    private Button btLogin;
    private Button btRegister;
    private DBHelper helper;
    private RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = (RelativeLayout) findViewById(R.id.container);
        etUsername = (EditText) findViewById(R.id.username);
        etPassword = (EditText) findViewById(R.id.password);
        btLogin = (Button) findViewById(R.id.login);
        btRegister = (Button) findViewById(R.id.register);
        helper= new DBHelper(this);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Login");
                String username = String.valueOf(etUsername.getText());
                String password = String.valueOf(etPassword.getText());
                SQLiteDatabase db = helper.getReadableDatabase();
                Cursor c = db.rawQuery("SELECT password from User WHERE username=?", new String[]{username});
                if (c.moveToFirst()) {
                    String p = c.getString(c.getColumnIndex(DBContract.User.PASSWORD));
                    if (TextUtils.equals(password, p)) {
                        snackbar(R.string.login_success);
                    } else {
                        snackbar(R.string.login_failed);
                    }
                } else {
                    snackbar(R.string.user_not_found);
                }
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Register");
                String username = String.valueOf(etUsername.getText());
                String password = String.valueOf(etPassword.getText());
                SQLiteDatabase db = helper.getWritableDatabase();
                db.execSQL("INSERT INTO User(username,password) VALUES(?,?)", new String[] {username, password});
            }
        });


    }

    public void snackbar(String message) {
        Snackbar.make(container, message, Snackbar.LENGTH_SHORT).show();
    }
    public void snackbar(int stringId) {
        Snackbar.make(container, stringId, Snackbar.LENGTH_SHORT).show();
    }
}
