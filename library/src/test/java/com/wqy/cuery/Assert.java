package com.wqy.cuery;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by wqy on 16-12-23.
 */

public class Assert {
    public static void assertListEquals(List l1, List l2) {
        assertEquals(l1.size(), l2.size());
        for (int i = 0; i < l1.size(); i++) {
            assertEquals(l1.get(i), l2.get(i));
        }
    }

    public static void assertArrayEquals(Object[] a1, Object[] a2) {
        assertNotNull(a1);
        assertNotNull(a2);
        assertEquals(a1.length, a2.length);
        for (int i = 0; i < a1.length; i++) {
            assertEquals(a1[i], a2[i]);
        }
    }
}
