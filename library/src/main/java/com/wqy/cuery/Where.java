package com.wqy.cuery;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by wqy on 16-12-23.
 */

public class Where {
    private String where = null;
    private StringBuilder builder = null;
    private Query query = null;
    private List<String> columns = null;
    private List<Object> args;
    private int clauseNum = 0;
    private boolean negative = false;
    private boolean and = true;

    public Where() {
        builder = new StringBuilder();
        columns = new LinkedList<>();
        args = new LinkedList<>();
    }

    public String getWhereClause() {
        if (where == null) {
            builder.deleteCharAt(builder.length() - 1);
            where = builder.toString();
        }
        return where;
    }

    public Where whereEqualTo(@NonNull String column, Object value) {
        return where(column, "=", value);
    }

    public Where whereNotEqualTo(@NonNull String column, Object value) {
        return where(column, "!=", value);
    }

    public Where whereGreaterThan(@NonNull String column, Object value) {
        return where(column, ">", value);
    }

    public Where whereLessThan(@NonNull String column, Object value) {
        return where(column, "<", value);
    }

    public Where whereGreaterThanOrEqualTo(@NonNull String column, Object value) {
        return where(column, ">=", value);
    }

    public Where whereLessThanOrEqualTo(@NonNull String column, Object value) {
        return where(column, "<=", value);
    }

    public Where whereBetween(@NonNull String column, Object from, Object to) {
        before();
        builder.append(column).append(" BETWEEN ? AND ?");
        columns.add(column);
        args.add(from);
        args.add(to);
        return this;
    }

    public Where whereExists(@NonNull Query subQuery) {
        before();
        builder.append("EXISTS (")
                .append(subQuery.getSql())
                .append(")");
        return this;
    }

    private Where whereNotExists(@NonNull Query subQuery) {
        not();
        whereExists(subQuery);
        return this;
    }

    private void containedIn(@NonNull String column, @NonNull Object[] values) {
        builder.append(column).append("IN (");
        int i = 0;
        for (;i < values.length - 1; i++) {
            builder.append("?,");
            args.add(values[i]);
        }
        builder.append("?)");
        columns.add(column);
        args.add(values[i]);
    }

    public Where whereContainedIn(@NonNull String column, @NonNull Object[] values) {
        if (values == null || values.length == 0) {
            return this;
        }
        before();
        containedIn(column, values);
        return this;
    }

    public Where whereNotContainedIn(@NonNull String column, @NonNull Object[] values) {
        if (values == null || values.length == 0) {
            return this;
        }
        not();
        whereContainedIn(column, values);
        return this;
    }

    public Where whereIsNull(@NonNull String column) {
        before(column);
        builder.append(column).append(" IS NULL");
        return this;
    }

    public Where whereNotNull(@NonNull String column) {
        before(column);
        builder.append(column).append(" IS NOT NULL");
        return this;
    }

    public Where whereLike(@NonNull String column, @NonNull String pattern) {
        before(column);
        builder.append(column).append(" LIKE ").append(pattern);
        return this;
    }

    public Where whereGlob(@NonNull String column, @NonNull String glob) {
        before(column);
        builder.append(column).append(" GLOB ").append(glob);
        return this;
    }

    /**
     * <p>
     * Add an AND condition which is a combination of the conditions in the
     * sub where. There may be one or more conditions in it so the sub where will be wrap by
     * '(' and ')'.
     * </p>
     * @param where the<code>Where</code> to be added with the AND constraint.
     * @return a reference to this object.
     */
    public Where and(@NonNull Where where) {
        return and(where.getWhereClause());
    }

    /**
     * <p>
     * Add an OR condition which is a combination of the conditions in the
     * sub where. There may be one or more conditions in it so the sub where will be wrap by
     * '(' and ')'.
     * </p>
     * @param where the<code>Where</code> to be added with the OR constraint.
     * @return a reference to this object.
     */
    public Where or(@NonNull Where where) {
        return or(where.getWhereClause());
    }

    /**
     * <p>
     * Add an AND condition which is represented by the given string param. As it may be a
     * combination of multiple conditions, it will be wrap by '(' and ')'.
     * Note that the where String shouldn't contain '?' for it requires one '?' had one argument
     * to correspond with.
     * </p>
     * @param where the sub where condition represented by string.
     * @return a reference to this object.
     */
    public Where and(@NonNull String where) {
        and();
        before();
        builder.append("(").append(where).append(")");
        return this;
    }

    /**
     * <p>
     * Add an OR condition which is represented by the given string param. As it may be a
     * combination of multiple conditions, it will be wrap by '(' and ')'.
     * Note that the where String shouldn't contain '?' for it requires one '?' had one argument
     * to correspond with.
     * </p>
     * @param where the sub where condition represented by string.
     * @return a reference to this object.
     */
    public Where or(@NonNull String where) {
        or();
        before();
        builder.append("(").append(where).append(")");
        return this;
    }

    /**
     * <p>
     * Add an AND condition which is represented by the given string param. As it may be a
     * combination of multiple conditions, it will be wrap by '(' and ')'.
     * </p>
     * @param where the sub where condition represented by string.
     * @param values the arguments corresponded with the '?' in the <code>where</code>
     * @return a reference to this object.
     */
    public Where and(@NonNull String where, @NonNull Object[] values) {
        if (args == null || values.length == 0) {
            return this;
        }
        and();
        subWhere(where, values);
        return this;
    }

    public Where or(@NonNull String where, @NonNull Object[] values) {
        if (args == null || values.length == 0) {
            return this;
        }
        or();
        subWhere(where, values);
        return this;
    }

    private void subWhere(String where, Object[] values) {
        before();
        builder.append("(").append(where).append(")");
        for (int i = 0; i < values.length; i++) {
            this.args.add(values[i]);
        }
    }

    /**
     * Add a NOT prefix to next where clause.
     * @return
     */
    public Where not() {
        negative = true;
        return this;
    }

    public Where and() {
        and = true;
        return this;
    }

    public Where or() {
        and = false;
        return this;
    }

    private void before() {
        if (and) {
            whenAnd();
        } else {
            whenOr();
        }
    }

    private void before(String column) {
        if (and) {
            whenAnd(column);
        } else {
            whenOr(column);
        }
    }

    private void before(String column, Object value) {
        if (and) {
            whenAnd(column, value);
        } else {
            whenOr(column, value);
        }
    }

    public Where where(@NonNull String column, String opr, Object value) {
        before(column, value);
        appendExpression(column, opr);
        return this;
    }

    private void appendExpression(String column, String opr) {
        builder.append(column).append(opr).append("?");
    }

    private void whenAnd() {
        if (clauseNum > 0) {
            builder.append(" AND ");
        }
        if (negative) {
            builder.append(" NOT ");
            negative = false;
        }
        clauseNum++;
    }

    private void whenOr() {
        if (clauseNum > 0) {
            builder.append(" OR ");
        }
        if (negative) {
            builder.append(" NOT ");
            negative = false;
        }
        clauseNum++;
    }

    private void whenAnd(String column) {
        whenAnd();
        columns.add(column);
    }

    private void whenOr(String column) {
        whenOr();
        columns.add(column);
    }

    private void whenAnd(String column, Object value) {
        whenAnd();
        columns.add(column);
        args.add(value);
    }

    private void whenOr(String column, Object value) {
        whenOr();
        columns.add(column);
        args.add(value);
    }
}