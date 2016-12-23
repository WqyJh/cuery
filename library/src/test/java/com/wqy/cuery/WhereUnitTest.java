package com.wqy.cuery;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by wqy on 16-12-23.
 */

public class WhereUnitTest {

    Where where = new Where();

    @Test
    public void testConditionCount() {
        assertEquals(0, where.getConditionCount());
        where.whereEqualTo("c1", 1)
                .whereGreaterThan("c2", 2)
                .whereLessThan("c3", 3)
                .whereGreaterThanOrEqualTo("c4", 4)
                .whereLessThanOrEqualTo("c5", 5)
                .whereNotEqualTo("c6", 6)
                .whereBetween("c7", 7, 7)
                .whereGlob("c8", "glob")
                .whereLike("c9", "like")
                .whereIsNull("c10")
                .whereNotNull("c11")
                .where("c12", ">", 1)
                .whereContainedIn("c13", new Object[]{1, 2, 3})
                .whereNotContainedIn("c14", new Object[]{4, 5, 6})
                .and("c15", new Object[]{1, 2, 3})
                .and("c16", new Object[]{1, 2, 3})
                .and("c17 > 1")
                .and("c18 > 2")
                .and("c19", new ArrayList<Object>())
                .and("c20", new ArrayList<Object>())
                .and(new Where())
                .or(new Where())
                .whereExists(new Query())
                .whereNotExists(new Query());
        assertEquals(24, where.getConditionCount());
    }

    @Test
    public void testColumns() {
        where.whereEqualTo("c1", 1)
                .whereGreaterThan("c2", 2)
                .whereLessThan("c3", 3)
                .whereGreaterThanOrEqualTo("c4", 4)
                .whereLessThanOrEqualTo("c5", 5)
                .whereNotEqualTo("c6", 6)
                .whereBetween("c7", 7, 7)
                .whereGlob("c8", "glob")
                .whereLike("c9", "like")
                .whereIsNull("c10")
                .whereNotNull("c11")
                .where("c12", ">", 1)
                .whereContainedIn("c13", new Object[]{1, 2, 3})
                .whereNotContainedIn("c14", new Object[]{4, 5, 6});
        String[] expected = new String[]{"c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "c10",
                "c11", "c12", "c13", "c14"};
        List<String> columns = where.getColumns();

        assertEquals(expected.length, columns.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], columns.get(i));
        }
    }

    public void testWhere(String column, String opr, Object value) {
        int size = 10;
        List<Object> expected = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            expected.add(value);
            where.where(column, opr, value);
        }
        assertListEquals(expected, where.getArgs());

        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append(column).append(opr).append("?");
        for (int i = 1; i < size; i++) {
            commandBuilder.append(" AND ").append(column).append(opr).append("?");
        }
        assertEquals(commandBuilder.toString(), where.getWhereClause());

        List<Object> args = where.getArgs();
        assertEquals(size, args.size());
        for (int i = 0; i < size; i++) {
            assertEquals(value, args.get(i));
        }

        System.out.println(where.getWhereClause());
    }

    @Test
    public void testWhereXxx() {
        testWhere("column2", "=", 100);
    }

    @Test
    public void testWhereEqualTo() {
        where.whereEqualTo("column", 1);
        assertEquals("column=?", where.getWhereClause());
        assertEquals(1, where.getArgs().get(0));
    }

    @Test
    public void testWhereNotEqualTo() {
        where.whereNotEqualTo("column", 2);
        assertEquals("column!=?", where.getWhereClause());
        assertEquals(2, where.getArgs().get(0));
    }

    @Test
    public void testWhereGreaterThan() {
        where.whereGreaterThan("column", 3);
        assertEquals("column>?", where.getWhereClause());
        assertEquals(3, where.getArgs().get(0));
    }

    @Test
    public void testWhereLessThan() {
        where.whereLessThan("column", 4);
        assertEquals("column<?", where.getWhereClause());
        assertEquals(4, where.getArgs().get(0));
    }

    @Test
    public void testWhereGreaterThanOrEqualTo() {
        where.whereGreaterThanOrEqualTo("column", 5);
        assertEquals("column>=?", where.getWhereClause());
        assertEquals(5, where.getArgs().get(0));
    }

    @Test
    public void testWhereLessThanOrEqualTo() {
        where.whereLessThanOrEqualTo("column", 6);
        assertEquals("column<=?", where.getWhereClause());
        assertEquals(6, where.getArgs().get(0));
    }

    @Test
    public void testWhereBetween() {
        where.whereBetween("column", 3, 6);
        assertEquals("column BETWEEN ? AND ?", where.getWhereClause());
        assertArrayEquals(new Object[]{3, 6}, where.getArgs().toArray());
    }

    @Test
    public void testWhereExists() {
        Query query = new Query();
        where.whereExists(query);
        assertEquals("EXISTS (" + query.getSql() + ")", where.getWhereClause());
    }

    @Test
    public void testWhereNotExists() {
        Query query = new Query();
        where.whereNotExists(query);
        assertEquals("NOT EXISTS (" + query.getSql() + ")", where.getWhereClause());
    }

    @Test
    public void testContainedIn() {
        Object[] values = new Object[]{1, 2, 3};
        where.whereContainedIn("column", values);
        assertEquals("column IN (?,?,?)", where.getWhereClause());
        assertArrayEquals(values, where.getArgs().toArray());
    }

    @Test
    public void testNotContainedIn() {
        Object[] values = new Object[]{1, 2, 3};
        where.whereNotContainedIn("column", values);
        assertEquals("column NOT IN (?,?,?)", where.getWhereClause());
        assertArrayEquals(values, where.getArgs().toArray());
    }

    @Test
    public void testIsNull() {
        where.whereIsNull("column");
        assertEquals("column IS NULL", where.getWhereClause());
    }

    @Test
    public void testNotNull() {
        where.whereNotNull("column");
        assertEquals("column IS NOT NULL", where.getWhereClause());
    }

    @Test
    public void testWhereLike() {
        where.whereLike("column", "XXXX");
        assertEquals("column LIKE 'XXXX'", where.getWhereClause());
    }

    @Test
    public void testWhereGlob() {
        where.whereGlob("column", "XXXX");
        assertEquals("column GLOB 'XXXX'", where.getWhereClause());
    }

    @Test
    public void testAnd1() {
        Where w2 = new Where();
        w2.whereEqualTo("c1", 1)
                .whereGreaterThan("c2", 2);
        where.whereLessThanOrEqualTo("c1", 4)
                .and(w2);
        assertEquals("c1<=? AND (c1=? AND c2>?)", where.getWhereClause());
        assertArrayEquals(new Object[]{4, 1, 2}, where.getArgs().toArray());
    }


    @Test
    public void testAnd2() {
        String w2 = "c1 < 2 OR c2 > 1";
        where.whereEqualTo("c1", 1).and(w2);
        assertEquals("c1=? AND (c1 < 2 OR c2 > 1)", where.getWhereClause());
        assertArrayEquals(new Object[]{1}, where.getArgs().toArray());
    }

    @Test
    public void testAnd3() {
        where.and("c1<? OR c2 < ?", new Object[]{1, 2})
                .whereEqualTo("c2", 1);
        assertEquals("(c1<? OR c2 < ?) AND c2=?", where.getWhereClause());
        assertArrayEquals(new Object[]{1, 2, 1}, where.getArgs().toArray());
    }

    @Test
    public void testOr1() {
        Where w2 = new Where();
        w2.whereEqualTo("c1", 1)
                .whereGreaterThan("c2", 2);
        where.whereLessThanOrEqualTo("c1", 4)
                .or(w2);
        assertEquals("c1<=? OR (c1=? AND c2>?)", where.getWhereClause());
        assertArrayEquals(new Object[]{4, 1, 2}, where.getArgs().toArray());
    }

    @Test
    public void testOr2() {
        String w2 = "c1 < 2 OR c2 > 1";
        where.whereEqualTo("c1", 1).or(w2);
        assertEquals("c1=? OR (c1 < 2 OR c2 > 1)", where.getWhereClause());
        assertArrayEquals(new Object[]{1}, where.getArgs().toArray());
    }

    @Test
    public void testOr3() {
        where.or("c1<? OR c2 < ?", new Object[]{1, 2})
                .whereEqualTo("c2", 1);
        assertEquals("(c1<? OR c2 < ?) OR c2=?", where.getWhereClause());
        assertArrayEquals(new Object[]{1, 2, 1}, where.getArgs().toArray());
    }

    @Test
    public void testAndOr() {
        where.whereNotNull("c3")
                .and("c1>1")
                .or("c1<?", new Object[]{5})
                .whereEqualTo("c2", 5)
                .and()
                .whereGreaterThan("c3", 4);
        assertEquals("c3 IS NOT NULL AND (c1>1) OR (c1<?) OR c2=? AND c3>?", where.getWhereClause());
        assertArrayEquals(new Object[]{5, 5, 4}, where.getArgs().toArray());
    }

    @Test
    public void testNestedAndOr() {
        Where w1 = new Where();
        w1.whereIsNull("c1")
                .or()
                .whereGreaterThan("c2", 3)
                .whereGreaterThanOrEqualTo("c4", 5)
                .and()
                .whereEqualTo("c1", 7)
                .where("c3", "!=", 9);
        Where w2 = new Where();
        w2.whereBetween("c2", 2, 6)
                .or().whereEqualTo("c4", 5)
                .and(w1);
        where.whereGlob("c7", "xixi")
                .or(w2);
        assertEquals("c7 GLOB 'xixi' OR (c2 BETWEEN ? AND ? OR c4=? AND (c1 IS NULL OR c2>? OR c4>=? AND c1=? AND c3!=?))",
                where.getWhereClause());
        System.out.println(where.getWhereClause());
        assertArrayEquals(new Object[]{2, 6, 5, 3, 5, 7, 9}, where.getArgs().toArray());
    }

    private void assertListEquals(List l1, List l2) {
        assertEquals(l1.size(), l2.size());
        for (int i = 0; i < l1.size(); i++) {
            assertEquals(l1.get(i), l2.get(i));
        }
    }

    private void assertArrayEquals(Object[] a1, Object[] a2) {
        assertNotNull(a1);
        assertNotNull(a2);
        assertEquals(a1.length, a2.length);
        for (int i = 0; i < a1.length; i++) {
            assertEquals(a1[i], a2[i]);
        }
    }
}
