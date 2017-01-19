package com.wqy.cuery;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by wqy on 17-1-19.
 */

public class Executor {
    private static volatile Executor instance;

    public static Executor getInstance() {
        if (instance == null) {
            synchronized (Executor.class) {
                if (instance == null) {
                    instance = new Executor();
                }
            }
        }
        return instance;
    }

    /**
     * Execute this Query.
     * @param db the SQLiteDatabase to be applied to
     * @return a {@link ResultSet} object. If it's SELECT operation, it would contain a
     * {@link Cursor} object. If INSERT operation, id of the newly inserted row. Otherwise, number
     * of rows affected.
     */
    public ResultSet execute(Query query, SQLiteDatabase db) {
        query.getSql();
        ResultSet rs = new ResultSet();

        switch (query.getActionValue()) {
            case Query.SELECT:
                Cursor c = performQuery(query, db);
                rs.setCursor(c);
                break;
            case Query.INSERT:
                rs.setRowId(
                        performInsert(query, db)
                );
                break;
            case Query.UPDATE:
                rs.setRowAffected(
                        performUpdate(query, db)
                );
                break;
            case Query.DELETE:
                rs.setRowAffected(
                        performDelete(query, db)
                );
                break;
            default:
        }
        return rs;
    }

    public void executeAsync(Query query, SQLiteDatabase db, Callback cb) {
        new QueryTask(cb).execute(query, db);
    }

    /**
     * Perform the SELECT operation.
     * @param db the SQLiteDatabase to be applied to
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     */
    public Cursor performQuery(Query query, SQLiteDatabase db) {
        Where where = query.getWhere();
        String[] selectionArgs = where == null ? null : where.getStringArgs();
        return db.rawQuery(query.getSql(), selectionArgs);
    }

    /**
     * Create a new SQLiteStatement.
     * @param db the SQLiteDatabase to be applied to
     * @return a {@link SQLiteStatement} object
     */
    private SQLiteStatement createStatement(Query query, SQLiteDatabase db) {
        SQLiteStatement statement = db.compileStatement(query.getSql());
        List<Object> values = query.getValues();
        for (int i = 0; i < values.size(); i++) {
            DatabaseUtils.bindObjectToProgram(statement, i + 1, values.get(i));
        }
        return statement;
    }

    /**
     * Perform INSERT operation.
     * @param db the SQLiteDatabase to be applied to
     * @return the id of the newly inserted row
     */
    private long performInsert(Query query, SQLiteDatabase db) {
        long rowId = -1;
        db.acquireReference();
        try {
            SQLiteStatement statement = createStatement(query, db);
            try {
                rowId = statement.executeInsert();
            } finally {
                statement.close();
            }
        } finally {
            db.releaseReference();
        }
        return rowId;
    }

    /**
     * Perform the UPDATE operation.
     * @param db the SQLiteDatabase to be applied to
     * @return number of rows affected
     */
    private int performUpdate(Query query, SQLiteDatabase db) {
        int affectedRows = 0;
        db.acquireReference();
        try {
            SQLiteStatement statement = createStatement(query, db);
            try {
                affectedRows = statement.executeUpdateDelete();
            } finally {
                statement.close();
            }
        } finally {
            db.releaseReference();
        }
        return affectedRows;
    }

    /**
     * Perform the DELETE operation.
     * @param db the SQLiteDatabase to be applied to
     * @return number of rows affected
     */
    private int performDelete(Query query, SQLiteDatabase db) {
        int rowsAffected = 0;
        db.acquireReference();
        try {
            SQLiteStatement statement = createStatement(query, db);
            try {
                rowsAffected = statement.executeUpdateDelete();
            } finally {
                statement.close();
            }
        } finally {
            db.releaseReference();
        }
        return rowsAffected;
    }

    private class QueryTask extends AsyncTask<Object, Integer, ResultSet> {
        private Callback cb;

        public QueryTask(Callback cb) {
            this.cb = cb;
        }

        @Override
        protected ResultSet doInBackground(Object... params) {
            Query query = (Query) params[0];
            SQLiteDatabase db = (SQLiteDatabase) params[1];
            return Executor.getInstance().execute(query, db);
        }

        @Override
        protected void onPostExecute(ResultSet resultSet) {
            cb.onResult(resultSet);
        }
    }

    public interface Callback {
        void onResult(ResultSet rs);
    }
}
