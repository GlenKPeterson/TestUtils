package org.organicdesign.testUtils.experimental;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.organicdesign.testUtils.experimental.TestHttpServletRequest.httpServletRequest;

public class TestHttpServletRequestTest {
    @Test public void testBasics() throws Exception {
        Map<String,String> headers = new TreeMap<>();
        headers.put("First", "Primero");
        headers.put("Second", "Secundo");
        headers.put("Third", "Tercero");

        String[] stuff = new String[] { "a", "b", "c" };
        String[] thing = new String[] { "JustOne" };

        Map<String,List<String>> params = new TreeMap<>();
        params.put("stuff", Arrays.asList(stuff));
        params.put("thing", Arrays.asList(thing));

        HttpServletRequest hsr = httpServletRequest("https://sub.example.com", "/path/file.html", headers, params);
        Enumeration<String> hEn = hsr.getHeaders(null);
        assertEquals("First:Primero", hEn.nextElement());
        assertEquals("Second:Secundo", hEn.nextElement());
        assertEquals("Third:Tercero", hEn.nextElement());
        assertFalse(hEn.hasMoreElements());

        assertEquals("Primero", hsr.getHeader("First"));

        assertEquals("stuff=a&stuff=b&stuff=c&thing=JustOne", hsr.getQueryString());
        Enumeration<String> pNmEn = hsr.getParameterNames();
        assertEquals("stuff", pNmEn.nextElement());
        assertEquals("thing", pNmEn.nextElement());
        assertFalse(pNmEn.hasMoreElements());

        assertArrayEquals(stuff, hsr.getParameterValues("stuff"));
        assertArrayEquals(thing, hsr.getParameterValues("thing"));


        Map<String,String[]> testMap = hsr.getParameterMap();
        assertEquals(params.size(), testMap.size());
        for (String k : params.keySet()) {
            assertTrue(testMap.containsKey(k));
            assertArrayEquals("Param values equal for " + k,
                              params.get(k).toArray(), testMap.get(k));
        }

        assertEquals("/path/file.html", hsr.getPathInfo());
        assertEquals("/path/file.html", hsr.getRequestURI());
        assertEquals("/path/file.html", hsr.getServletPath());
        assertEquals("https://sub.example.com/path/file.html", hsr.getRequestURL().toString());

        assertEquals(Locale.US, hsr.getLocale());

        assertNull(hsr.getAttribute("attr1"));
        assertFalse(hsr.getAttributeNames().hasMoreElements());

        hsr.setAttribute("attr1", "val1");
        assertEquals("val1", hsr.getAttribute("attr1"));

        Enumeration<String> attrNameEn = hsr.getAttributeNames();
        assertEquals("attr1", attrNameEn.nextElement());
        assertFalse(attrNameEn.hasMoreElements());

        hsr.removeAttribute("attr1");
        assertNull(hsr.getAttribute("attr1"));


        hsr.setCharacterEncoding("WinAnsi");
        assertEquals("WinAnsi", hsr.getCharacterEncoding());
    }

}