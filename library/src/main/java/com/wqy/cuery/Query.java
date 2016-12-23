package com.wqy.cuery;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wqy on 16-12-23.
 */

public class Query {

    public static final int SELECT = 1;
    public static final int INSERT = 2;
    public static final int UPDATE = 3;
    public static final int DELETE = 4;

    private String sql = null;
    private String action = null;
    private String table = null;
    private String where = null;
    private String group = null;
    private String having = null;
    private String order = null;
    private List<String> columns = null;
    private List<Object> values = null;
    private List<String> nullableColumns = null;
    private int limit = 0;
    private int offset = 0;
    private boolean distinct = false;
    private Where mWhere = null;
    private int actionValue = 0;
    private StringBuilder updateBuilder = null;
    private int updateCount = 0;
    private List<Object> updateValues = null;
    private StringBuilder orderBuilder = null;
    private int orderCount = 0;
    private StringBuilder groupBuilder = null;
    private int groupCount = 0;
    private List<Object> havingValue = null;
    private StringBuilder sqlBuilder = null;
    private List<Object> insertValue = null;

    public Query() {
        columns = new ArrayList<>();
        values = new ArrayList<>();
    }

    public ResultSet execute(SQLiteDatabase db) {
        beforeExecute();
        ResultSet rs = new ResultSet();
        switch (actionValue) {
            case SELECT:
                Cursor c = performQuery(db);
                rs.setCursor(c);
                break;
            case INSERT:
                rs.setRowInserted(
                        performInsert(db)
                );
                break;
            case UPDATE:
                rs.setRowAffected(
                        performUpdate(db)
                );
                break;
            case DELETE:
                rs.setRowAffected(
                        performDelete(db)
                );
                break;
            default:
        }
        return rs;
    }


    public Cursor performQuery(SQLiteDatabase db) {
        String[] selectionArgs = mWhere == null ? null : mWhere.getStringArgs();
        return db.rawQuery(sql, selectionArgs);
    }

    private SQLiteStatement createStatement(SQLiteDatabase db) {
        SQLiteStatement statement = db.compileStatement(sql);
        for (int i = 0; i < values.size(); i++) {
            DatabaseUtils.bindObjectToProgram(statement, i + 1, values.get(i));
        }
        return statement;
    }

    public long performInsert(SQLiteDatabase db) {
        long rowId = -1;
        db.acquireReference();
        try {
            SQLiteStatement statement = createStatement(db);
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

    public int performUpdate(SQLiteDatabase db) {
        int affectedRows = 0;
        db.acquireReference();
        try {
            SQLiteStatement statement = createStatement(db);
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

    public int performDelete(SQLiteDatabase db) {
        int rowsAffected = 0;
        db.acquireReference();
        try {
            SQLiteStatement statement = createStatement(db);
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

    public void beforeExecute() {
        if (sql == null) {
            getSql();
        }
    }

    public String getSql() {
        if (sql != null) {
            return sql;
        }

        if (sqlBuilder == null) {
            sqlBuilder = new StringBuilder();
        }

        switch (actionValue) {
            case SELECT:
                compileSelect();
                break;
            case INSERT:
                compileInsert();
                break;
            case UPDATE:
                compileUpdate();
                break;
            case DELETE:
                compileDelete();
                break;
            default:
        }

        sql = sqlBuilder.toString();
        return sql;
    }

    private void compileSelect() {
        sqlBuilder.append("SELECT ");
        if (distinct) {
            sqlBuilder.append("DISTINCT ");
        }

        appendColumns(columns);

        sqlBuilder.append(" FROM ").append(table);

        appendWhere();

        appendGroup();

        appendOrder();

        limitOffset();
    }

    private void compileInsert() {
        sqlBuilder.append("INSERT INTO ").append(table)
                .append(" (");

        appendColumns(columns);

        sqlBuilder.append(") VALUES (");

        appendPlaceHolder(columns.size());

        sqlBuilder.append(")");

        values.addAll(insertValue);
    }

    private void compileUpdate() {
        sqlBuilder.append("UPDATE ").append(table)
                .append(" SET ");

        if (updateCount > 0) {
            sqlBuilder.append(updateBuilder.toString());
            values.addAll(updateValues);
        }

        appendWhere();
    }

    private void compileDelete() {
        sqlBuilder.append("DELETE FROM ").append(table);

        appendWhere();
    }

    private void appendPlaceHolder(int num) {
        for (int i = 0; i < num - 1; i++) {
            sqlBuilder.append("?,");
        }
        sqlBuilder.append("?");
    }

    private void appendColumns(List<String> columns) {
        Iterator<String> iterator = columns.iterator();
        int size = columns.size();
        for (int i = 0; i < size - 1; i++) {
            String column = iterator.next();
            sqlBuilder.append(column).append(",");
        }
        sqlBuilder.append(iterator.next());
    }

    private void appendWhere() {
        if (mWhere != null) {
            where = mWhere.getWhereClause();
            values.addAll(mWhere.getArgs());
            sqlBuilder.append(" WHERE ").append(where);
        }
    }

    private void appendGroup() {
        if (groupCount > 0) {
            group = groupBuilder.toString();
            sqlBuilder.append(" GROUP BY ").append(group);
            if (having != null) {
                sqlBuilder.append(" HAVING ").append(having);
                values.addAll(havingValue);
            }
        }
    }

    private void appendOrder() {
        if (orderCount > 0) {
            sqlBuilder.append(" ORDER BY ");
            order = orderBuilder.toString();
            sqlBuilder.append(order);
        }
    }

    private void limitOffset() {
        if (limit > 0) {
            sqlBuilder.append(" LIMIT ?");
            values.add(limit);
        }

        if (offset > 0) {
            sqlBuilder.append(" OFFSET ?");
            values.add(offset);
        }
    }

    public Query select() {
        actionValue = SELECT;
        this.action = "SELECT";
        return this;
    }

    public Query insert() {
        actionValue = INSERT;
        this.action = "INSERT INTO";
        return this;
    }

    public Query update() {
        actionValue = UPDATE;
        this.action = "UPDATE";
        return this;
    }

    public Query delete() {
        actionValue = DELETE;
        this.action = "DELETE";
        return this;
    }

    public Query select(String... columns) {
        select();
        this.columns(columns);
        return this;
    }

    public Query table(@NonNull String table) {
        this.table = table;
        return this;
    }

    public Query columns(@NonNull String... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    public Query set(@NonNull String column, Object value) {
        beforeUpdate();
        updateBuilder.append(column).append("=?");
        updateValues.add(value);
        return this;
    }

    /**
     * Only used for INSERT.
     *
     * @param values
     * @return
     */
    public Query values(Object... values) {
        if (insertValue == null) {
            insertValue = new LinkedList<>();
        }
        insertValue.addAll(Arrays.asList(values));
        return this;
    }

    private void beforeUpdate() {
        if (updateValues == null) {
            updateValues = new ArrayList<>();
        }
        if (updateBuilder == null) {
            updateBuilder = new StringBuilder();
        }
        if (updateCount != 0) {
            updateBuilder.append(",");
        }
        updateCount++;
    }

    public Query orderByAsc(String column) {
        beforeOrder();
        orderBuilder.append(column).append(" ASC");
        orderCount++;
        return this;
    }

    public Query orderByDesc(String column) {
        beforeOrder();
        orderBuilder.append(column).append(" DESC");
        orderCount++;
        return this;
    }

    private void beforeOrder() {
        if (orderBuilder == null) {
            orderBuilder = new StringBuilder();
        }
        if (orderCount != 0) {
            orderBuilder.append(",");
        }
    }

    public Query groupBy(String... columns) {
        if (groupBuilder == null) {
            groupBuilder = new StringBuilder();
        }

        groupCount = columns.length;
        for (int i = 0; i < groupCount - 1; i++) {
            groupBuilder.append(columns[i]).append(",");
        }
        groupBuilder.append(columns[groupCount - 1]);

        return this;
    }

    private void beforeGroup() {
        if (groupBuilder == null) {
            groupBuilder = new StringBuilder();
        }
        if (groupCount != 0) {
            groupBuilder.append(",");
        }
    }

    public Query having(String condition, Object... values) {
        this.having = condition;
        havingValue = Arrays.asList(values);
        return this;
    }

    public Query nullableColumns(String... columns) {
        this.nullableColumns = Arrays.asList(columns);
        return this;
    }

    public Where startWhere() {
        mWhere = new Where(this);
        return mWhere;
    }

    public Query limit(int n) {
        this.limit = n;
        return this;
    }

    public Query offset(int n) {
        this.offset = n;
        return this;
    }

    public Query distinct() {
        distinct = true;
        return this;
    }

    public List<Object> getValues() {
        return values;
    }

    public interface Callback {
        void onResult(Cursor cursor);
    }
}
