package org.organicdesign.testUtils.experimental;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;
import static org.organicdesign.testUtils.experimental.TestHttpServletRequest.httpServletRequest;

public class TestHttpServletRequestTest {
    @Test public void testBasics() {
        Map<String,String> headers = new TreeMap<>();
        headers.put("First", "Primero");
        headers.put("Second", "Secundo");
        headers.put("Third", "Tercero");

        Map<String,List<String>> params = new TreeMap<>();
        params.put("stuff", Arrays.asList("a", "b", "c"));
        params.put("thing", Collections.singletonList("JustOne"));

        HttpServletRequest hsr = httpServletRequest("baseUrl", "uri", headers, params);
        Enumeration<String> hEn = hsr.getHeaders(null);
        assertEquals("First:Primero", hEn.nextElement());
        assertEquals("Second:Secundo", hEn.nextElement());
        assertEquals("Third:Tercero", hEn.nextElement());
        assertFalse(hEn.hasMoreElements());

        assertEquals("stuff=a&stuff=b&stuff=c&thing=JustOne", hsr.getQueryString());
    }

}