package com.wqy.cuery;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static com.wqy.cuery.Assert.*;

/**
 * Created by wqy on 16-12-23.
 */

public class QueryUnitTest {
    Query query = new Query();

    @Test
    public void testSelect() {
        query.select()
                .columns("c1", "c2", "c3")
                .table("User")
                .startWhere()
                .whereEqualTo("c1", 11)
                .whereGlob("c2", "xixi")
                .endWhere()
                .groupBy("c3", "c4")
                .having("sum(c3) < ?", 14)
                .orderByAsc("c1")
                .orderByDesc("c3")
                .limit(10)
                .offset(18)
                .distinct();
        String expected = "SELECT DISTINCT c1,c2,c3 FROM User WHERE c1=? AND c2 GLOB 'xixi' GROUP BY c3,c4 HAVING sum(c3) < ? ORDER BY c1 ASC,c3 DESC LIMIT ? OFFSET ?";
        assertEquals(expected, query.getSql());
        assertArrayEquals(new Object[] {11, 14, 10, 18}, query.getValues().toArray());
    }

    @Test
    public void testInsert() {
        query.insert()
                .table("User")
                .columns("c1", "c2", "c3")
                .values(1, 2, 3);
        assertEquals("INSERT INTO User (c1,c2,c3) VALUES (?,?,?)", query.getSql());
    }

    @Test
    public void testUpdate() {
        query.update()
                .table("User")
                .set("c1", 10)
                .set("c2", 14)
                .set("c3", 0)
                .startWhere()
                .whereEqualTo("c3", 10)
                .whereLessThanOrEqualTo("c1", 2)
                .endWhere();
        String expected = "UPDATE User SET c1=?,c2=?,c3=? WHERE c3=? AND c1<=?";
        assertEquals(expected, query.getSql());
        assertArrayEquals(new Object[]{10, 14, 0, 10, 2}, query.getValues().toArray());
    }

    @Test
    public void testDelete() {
        query.delete()
                .table("User")
                .startWhere()
                .whereEqualTo("c1", 11)
                .or().whereGlob("c2", "xixi");
        String expected = "DELETE FROM User WHERE c1=? OR c2 GLOB 'xixi'";
        assertEquals(expected, query.getSql());
        assertArrayEquals(new Object[]{11}, query.getValues().toArray());
    }
}
