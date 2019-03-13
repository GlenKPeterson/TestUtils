package org.organicdesign.testUtils;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static java.util.Locale.TRADITIONAL_CHINESE;
import static org.junit.Assert.*;
import static org.organicdesign.testUtils.FakeHttpServletResponse.httpServletResponse;

public class FakeHttpServletResponseTest {
    @Test public void testBasics() {
        FakeHttpServletResponse hsr = httpServletResponse();
        hsr.setStatus(9);
        assertEquals(9, hsr.getStatus());
        hsr.sendRedirect("somewhere");
        assertEquals("somewhere", hsr.redirect);

//        assertFalse(hsr.containsHeader("Hello"));
//        hsr.setHeader("Hello", "World");
//        assertTrue(hsr.containsHeader("Hello"));
//        assertEquals("World", hsr.getHeader("Hello"));
//
//        hsr.addHeader("Hello", "Pumpkin");
//        assertTrue(hsr.containsHeader("Hello"));
//        assertEquals(Arrays.asList("World", "Pumpkin"),
//                     hsr.getHeaders("Hello"));
//        assertEquals("World", hsr.getHeader("Hello"));
//
//        assertFalse(hsr.containsHeader("Buddy"));
//        assertNull(hsr.getHeader("Buddy"));
//        hsr.setHeader("Buddy", "Rich");
//
//        assertEquals(new HashSet<>(Arrays.asList("Hello", "Buddy")),
//                     hsr.getHeaderNames());


        hsr.setLocale(TRADITIONAL_CHINESE);
        assertEquals(TRADITIONAL_CHINESE,
                     hsr.getLocale());

    }

}