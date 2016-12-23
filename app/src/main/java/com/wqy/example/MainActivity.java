package com.wqy.example;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.username)
    public EditText etUsername;

    @BindView(R.id.password)
    public EditText etPassword;

    @BindView(R.id.login)
    public Button btLogin;

    @BindView(R.id.register)
    public Button btRegister;

    @BindView(R.id.container)
    public RelativeLayout container;

    @BindView(R.id.recycler_view)
    public RecyclerView recyclerView;

    private RecyclerViewAdapter adapter;
    private DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        try {
            helper= DBHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM User", null);
        adapter = new RecyclerViewAdapter(this, c, recyclerView);
        recyclerView.setAdapter(adapter);

        c.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                Log.d(TAG, "onChanged: Dataset changed");
                SQLiteDatabase db = helper.getReadableDatabase();
                Cursor c = db.rawQuery("SELECT * FROM User", null);
                adapter.swapCursor(c);
                c.registerDataSetObserver(this);
            }

            @Override
            public void onInvalidated() {
                onChanged();
            }
        });

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
                try {
                    db.execSQL("INSERT INTO User(username,password) VALUES(?,?)", new String[] {username, password});
                } catch (SQLiteConstraintException e) {
                    e.printStackTrace();
                    snackbar(R.string.register_failed);
                }
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
