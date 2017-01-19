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
    private String table = null;
    private String action = null;
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

//    /**
//     * Execute this Query.
//     * @param db the SQLiteDatabase to be applied to
//     * @return a {@link ResultSet} object. If it's SELECT operation, it would contain a
//     * {@link Cursor} object. If INSERT operation, id of the newly inserted row. Otherwise, number
//     * of rows affected.
//     */
//    public ResultSet execute(SQLiteDatabase db) {
//        beforeExecute();
//        ResultSet rs = new ResultSet();
//        switch (actionValue) {
//            case SELECT:
//                Cursor c = performQuery(db);
//                rs.setCursor(c);
//                break;
//            case INSERT:
//                rs.setRowId(
//                        performInsert(db)
//                );
//                break;
//            case UPDATE:
//                rs.setRowAffected(
//                        performUpdate(db)
//                );
//                break;
//            case DELETE:
//                rs.setRowAffected(
//                        performDelete(db)
//                );
//                break;
//            default:
//        }
//        return rs;
//    }
//
//    /**
//     * Perform the SELECT operation.
//     * @param db the SQLiteDatabase to be applied to
//     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
//     * {@link Cursor}s are not synchronized, see the documentation for more details.
//     */
//    public Cursor performQuery(SQLiteDatabase db) {
//        String[] selectionArgs = mWhere == null ? null : mWhere.getStringArgs();
//        return db.rawQuery(sql, selectionArgs);
//    }
//
//    /**
//     * Create a new SQLiteStatement.
//     * @param db the SQLiteDatabase to be applied to
//     * @return a {@link SQLiteStatement} object
//     */
//    private SQLiteStatement createStatement(SQLiteDatabase db) {
//        SQLiteStatement statement = db.compileStatement(sql);
//        for (int i = 0; i < values.size(); i++) {
//            DatabaseUtils.bindObjectToProgram(statement, i + 1, values.get(i));
//        }
//        return statement;
//    }
//
//    /**
//     * Perform INSERT operation.
//     * @param db the SQLiteDatabase to be applied to
//     * @return the id of the newly inserted row
//     */
//    private long performInsert(SQLiteDatabase db) {
//        long rowId = -1;
//        db.acquireReference();
//        try {
//            SQLiteStatement statement = createStatement(db);
//            try {
//                rowId = statement.executeInsert();
//            } finally {
//                statement.close();
//            }
//        } finally {
//            db.releaseReference();
//        }
//        return rowId;
//    }
//
//    /**
//     * Perform the UPDATE operation.
//     * @param db the SQLiteDatabase to be applied to
//     * @return number of rows affected
//     */
//    private int performUpdate(SQLiteDatabase db) {
//        int affectedRows = 0;
//        db.acquireReference();
//        try {
//            SQLiteStatement statement = createStatement(db);
//            try {
//                affectedRows = statement.executeUpdateDelete();
//            } finally {
//                statement.close();
//            }
//        } finally {
//            db.releaseReference();
//        }
//        return affectedRows;
//    }
//
//    /**
//     * Perform the DELETE operation.
//     * @param db the SQLiteDatabase to be applied to
//     * @return number of rows affected
//     */
//    private int performDelete(SQLiteDatabase db) {
//        int rowsAffected = 0;
//        db.acquireReference();
//        try {
//            SQLiteStatement statement = createStatement(db);
//            try {
//                rowsAffected = statement.executeUpdateDelete();
//            } finally {
//                statement.close();
//            }
//        } finally {
//            db.releaseReference();
//        }
//        return rowsAffected;
//    }
//
//    /**
//     * Do something before execute this Query.
//     */
//    public void beforeExecute() {
//        if (sql == null) {
//            getSql();
//        }
//    }

    /**
     * Compile and return the sql String.
     * @return sql String
     */
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

    /**
     * Compile SELECT operation.
     */
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

    /**
     * Compile INSERT operation.
     */
    private void compileInsert() {
        sqlBuilder.append("INSERT INTO ").append(table)
                .append(" (");

        appendColumns(columns);

        sqlBuilder.append(") VALUES (");

        appendPlaceHolder(columns.size());

        sqlBuilder.append(")");

        values.addAll(insertValue);
    }

    /**
     * Compile UPDATE operation.
     */
    private void compileUpdate() {
        sqlBuilder.append("UPDATE ").append(table)
                .append(" SET ");

        if (updateCount > 0) {
            sqlBuilder.append(updateBuilder.toString());
            values.addAll(updateValues);
        }

        appendWhere();
    }

    /**
     * Compile DELETE operation.
     */
    private void compileDelete() {
        sqlBuilder.append("DELETE FROM ").append(table);

        appendWhere();
    }

    /**
     * Append the place holders '?' to the sqlBuilder.
     * @param num number of place holders
     */
    private void appendPlaceHolder(int num) {
        for (int i = 0; i < num - 1; i++) {
            sqlBuilder.append("?,");
        }
        sqlBuilder.append("?");
    }

    /**
     * Append columns to sqlBuilder.
     * @param columns
     */
    private void appendColumns(List<String> columns) {
        Iterator<String> iterator = columns.iterator();
        int size = columns.size();
        for (int i = 0; i < size - 1; i++) {
            String column = iterator.next();
            sqlBuilder.append(column).append(",");
        }
        sqlBuilder.append(iterator.next());
    }

    /**
     * Append WHERE clause to the sqlBuilder.
     */
    private void appendWhere() {
        if (mWhere != null) {
            where = mWhere.getWhereClause();
            values.addAll(mWhere.getArgs());
            sqlBuilder.append(" WHERE ").append(where);
        }
    }

    /**
     * Append GROUP BY clause to the sqlBuilder.
     */
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

    /**
     * Append ORDER BY clause to the sqlBuilder.
     */
    private void appendOrder() {
        if (orderCount > 0) {
            sqlBuilder.append(" ORDER BY ");
            order = orderBuilder.toString();
            sqlBuilder.append(order);
        }
    }

    /**
     * Compile the LIMIT and OFFSET clause.
     */
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

    /**
     * Indicate this Query is a SELECT operation.
     * @return this Query
     */
    public Query select() {
        actionValue = SELECT;
        this.action = "SELECT";
        return this;
    }

    /**
     * Indicate this Qeury is a INSERT operation.
     * @return this Query
     */
    public Query insert() {
        actionValue = INSERT;
        this.action = "INSERT INTO";
        return this;
    }

    /**
     * Indicate this Query is a UPDATE operation.
     * @return this Query
     */
    public Query update() {
        actionValue = UPDATE;
        this.action = "UPDATE";
        return this;
    }

    /**
     * Indicate this Query is a DELETE operation.
     * @return this Query
     */
    public Query delete() {
        actionValue = DELETE;
        this.action = "DELETE";
        return this;
    }

    /**
     * Indicate this Query is a SELECT operation.
     * @param columns columns to be selected
     * @return this Query
     */
    public Query select(String... columns) {
        select();
        this.columns(columns);
        return this;
    }

    /**
     * Set the table to be applied to.
     * @param table table name
     * @return this Query
     */
    public Query table(@NonNull String table) {
        this.table = table;
        return this;
    }

    /**
     * Set the columns in INSERT operation.
     * @param columns column names
     * @return this Query
     */
    public Query columns(@NonNull String... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    /**
     * Add the SET clause in UPDATE operation.
     * @param column column name
     * @param value value to be set to the column
     * @return this Query
     */
    public Query set(@NonNull String column, Object value) {
        beforeUpdate();
        updateBuilder.append(column).append("=?");
        updateValues.add(value);
        return this;
    }

    /**
     * Add the VALUES clause in INSERT operation.
     * @param values values to be inserted
     * @return this Query
     */
    public Query values(Object... values) {
        if (insertValue == null) {
            insertValue = new LinkedList<>();
        }
        insertValue.addAll(Arrays.asList(values));
        return this;
    }

    /**
     * Do something before add the SET clause in UPDATE operation.
     */
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

    /**
     * Add the ORDER BY clause with ASC.
     * @param column column name
     * @return this Query
     */
    public Query orderByAsc(String column) {
        beforeOrder();
        orderBuilder.append(column).append(" ASC");
        orderCount++;
        return this;
    }

    /**
     * Add the ORDER BY clause with DESC.
     * @param column column name
     * @return this Query
     */
    public Query orderByDesc(String column) {
        beforeOrder();
        orderBuilder.append(column).append(" DESC");
        orderCount++;
        return this;
    }

    /**
     * Do something before add the ORDER BY clause.
     */
    private void beforeOrder() {
        if (orderBuilder == null) {
            orderBuilder = new StringBuilder();
        }
        if (orderCount != 0) {
            orderBuilder.append(",");
        }
    }

    /**
     * Add the GROUP BY clause.
     * @param columns column names
     * @return this Query
     */
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

    /**
     * Do something before add the GROUP BY clause.
     */
    @Deprecated
    private void beforeGroup() {
        if (groupBuilder == null) {
            groupBuilder = new StringBuilder();
        }
        if (groupCount != 0) {
            groupBuilder.append(",");
        }
    }

    /**
     * Add the HAVING clause.
     * @param condition condition string using '?' as parameters and dividing by ','
     * @param values values bind to the parameters
     * @return this Query
     */
    public Query having(String condition, Object... values) {
        this.having = condition;
        havingValue = Arrays.asList(values);
        return this;
    }

    /**
     * Set the nullable columns used in SELECT.
     * @param columns nullable columns.
     * @return this Query
     */
    @Deprecated
    public Query nullableColumns(String... columns) {
        this.nullableColumns = Arrays.asList(columns);
        return this;
    }

    /**
     * Start WHERE clause. If invoked for multiple times, only the last invoke is effective.
     * @return a new {@link Where} object
     */
    public Where startWhere() {
        mWhere = new Where(this);
        return mWhere;
    }

    /**
     * Add LIMIT clause.
     * @param n limit number
     * @return this Query
     */
    public Query limit(int n) {
        this.limit = n;
        return this;
    }

    /**
     * Add OFFSET clause.
     * @param n offset number
     * @return this Query
     */
    public Query offset(int n) {
        this.offset = n;
        return this;
    }

    /**
     * To distinct the query result.
     * @return this Query
     */
    public Query distinct() {
        distinct = true;
        return this;
    }

    /**
     * Get the values that would be bind to parameters corresponding to '?'.
     * @return list of values
     */
    public List<Object> getValues() {
        return values;
    }

    public int getActionValue() {
        return actionValue;
    }

    public Where getWhere() {
        return mWhere;
    }
}
