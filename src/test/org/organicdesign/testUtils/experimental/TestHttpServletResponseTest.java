package org.organicdesign.testUtils.experimental;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.organicdesign.testUtils.experimental.TestHttpServletResponse.httpServletResponse;

public class TestHttpServletResponseTest {
    @Test public void testBasics() throws Exception {
        TestHttpServletResponse hsr = httpServletResponse();
        hsr.setStatus(9);
        assertEquals(9, hsr.getStatus());
        hsr.sendRedirect("somewhere");
        assertEquals("somewhere", hsr.redirect);
    }

}