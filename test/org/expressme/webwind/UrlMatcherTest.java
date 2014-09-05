package org.expressme.webwind;

import static org.junit.Assert.*;

import org.expressme.webwind.UrlMatcher;
import org.junit.Test;

public class UrlMatcherTest {

    @Test
    public void testMatchRoot() {
        UrlMatcher m = new UrlMatcher("/");
        assertEquals(0, m.orders.length);
        assertArrayEquals(toArray(), m.getMatchedParameters("/"));
        assertNull(m.getMatchedParameters(""));
        assertNull(m.getMatchedParameters("//"));
        assertNull(m.getMatchedParameters("/abc"));
    }

    @Test
    public void testMatch1Parameter() {
        UrlMatcher m = new UrlMatcher("/abc/$1/xyz");
        assertEquals(1, m.orders.length);
        assertEquals(0, m.orders[0]);
        // matched url:
        assertArrayEquals(toArray("123"), m.getMatchedParameters("/abc/123/xyz"));
        assertArrayEquals(toArray("QQQ"), m.getMatchedParameters("/abc/QQQ/xyz"));
        assertArrayEquals(toArray("---"), m.getMatchedParameters("/abc/---/xyz"));
        assertArrayEquals(toArray(""), m.getMatchedParameters("/abc//xyz"));
        // not matched url:
        assertNull(m.getMatchedParameters("/"));
        assertNull(m.getMatchedParameters("/abc/xyz"));
        assertNull(m.getMatchedParameters("/abc/123/"));
        assertNull(m.getMatchedParameters("/123/xyz"));
        assertNull(m.getMatchedParameters("/abc/123/xyz/"));
        assertNull(m.getMatchedParameters("/abc//---//xyz"));
        assertNull(m.getMatchedParameters("/abc/---//xyz"));
        assertNull(m.getMatchedParameters("/abc//---/xyz"));
    }

    @Test
    public void testMatch2Parameters() {
        UrlMatcher m = new UrlMatcher("/abc/$1/$2/xyz");
        assertEquals(2, m.orders.length);
        assertEquals(0, m.orders[0]);
        assertEquals(1, m.orders[1]);
        // matched url:
        assertArrayEquals(toArray("123", "456"), m.getMatchedParameters("/abc/123/456/xyz"));
        assertArrayEquals(toArray("QQQ", "VVV"), m.getMatchedParameters("/abc/QQQ/VVV/xyz"));
        assertArrayEquals(toArray("---", ""), m.getMatchedParameters("/abc/---//xyz"));
        assertArrayEquals(toArray("", "---"), m.getMatchedParameters("/abc//---/xyz"));
        assertArrayEquals(toArray("", ""), m.getMatchedParameters("/abc///xyz"));
        // not matched url:
        assertNull(m.getMatchedParameters("/"));
        assertNull(m.getMatchedParameters("/abc//xyz"));
        assertNull(m.getMatchedParameters("/abc//---//xyz"));
        assertNull(m.getMatchedParameters("/abc////xyz"));
        assertNull(m.getMatchedParameters("/abc/123/xyz/"));
        assertNull(m.getMatchedParameters("/123/xyz"));
        assertNull(m.getMatchedParameters("/abc/123/456/xyz/"));
    }

    @Test
    public void testMatch2ParametersDesc() {
        UrlMatcher m = new UrlMatcher("/abc/$2/$1/xyz");
        assertEquals(2, m.orders.length);
        assertEquals(1, m.orders[0]);
        assertEquals(0, m.orders[1]);
        // matched url:
        assertArrayEquals(toArray("456", "123"), m.getMatchedParameters("/abc/123/456/xyz"));
        assertArrayEquals(toArray("VVV", "QQQ"), m.getMatchedParameters("/abc/QQQ/VVV/xyz"));
        assertArrayEquals(toArray("", "---"), m.getMatchedParameters("/abc/---//xyz"));
        assertArrayEquals(toArray("---", ""), m.getMatchedParameters("/abc//---/xyz"));
        assertArrayEquals(toArray("", ""), m.getMatchedParameters("/abc///xyz"));
        // not matched url:
        assertNull(m.getMatchedParameters("/"));
        assertNull(m.getMatchedParameters("/abc/xyz"));
        assertNull(m.getMatchedParameters("/abc//123/"));
        assertNull(m.getMatchedParameters("/123/--/xyz"));
        assertNull(m.getMatchedParameters("/123////xyz"));
        assertNull(m.getMatchedParameters("/abc/123/456/xyz/"));
    }

    @Test
    public void testMatchFirst() {
        UrlMatcher m = new UrlMatcher("$1/xyz");
        assertEquals(1, m.orders.length);
        assertEquals(0, m.orders[0]);
        // matched url:
        assertArrayEquals(toArray("123"), m.getMatchedParameters("123/xyz"));
        assertArrayEquals(toArray("123456"), m.getMatchedParameters("123456/xyz"));
        assertArrayEquals(toArray("123"), m.getMatchedParameters("123/xyz"));
        assertArrayEquals(toArray("---"), m.getMatchedParameters("---/xyz"));
        assertArrayEquals(toArray(""), m.getMatchedParameters("/xyz"));
        // not matched url:
        assertNull(m.getMatchedParameters("/"));
        assertNull(m.getMatchedParameters("abc/u/xyz"));
        assertNull(m.getMatchedParameters("abc/xyz/"));
        assertNull(m.getMatchedParameters("/abc/xyz/"));
    }

    @Test
    public void testMatchLast() {
        UrlMatcher m = new UrlMatcher("/xyz/$1");
        assertEquals(1, m.orders.length);
        assertEquals(0, m.orders[0]);
        // matched url:
        assertArrayEquals(toArray("123"), m.getMatchedParameters("/xyz/123"));
        assertArrayEquals(toArray("123456"), m.getMatchedParameters("/xyz/123456"));
        assertArrayEquals(toArray("---"), m.getMatchedParameters("/xyz/---"));
        assertArrayEquals(toArray(""), m.getMatchedParameters("/xyz/"));
        // not matched url:
        assertNull(m.getMatchedParameters("/"));
        assertNull(m.getMatchedParameters("/xyz"));
        assertNull(m.getMatchedParameters("xyz/"));
        assertNull(m.getMatchedParameters("/xyz/123/"));
    }

    @Test
    public void testMatchEncoded() {
        UrlMatcher m = new UrlMatcher("/xyz/$1");
        assertEquals(1, m.orders.length);
        assertEquals(0, m.orders[0]);
        // matched url:
        assertArrayEquals(toArray("abc%20abc"), m.getMatchedParameters("/xyz/abc%20abc"));
        assertArrayEquals(toArray("abc abc"), m.getMatchedParameters("/xyz/abc abc"));
        // not matched url:
        assertNull(m.getMatchedParameters("/xyz/abc/abc"));
    }

    String[] toArray(String... ss) {
        return ss;
    }
}
