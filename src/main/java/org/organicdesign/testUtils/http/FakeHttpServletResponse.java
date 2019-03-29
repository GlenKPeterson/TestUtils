package org.organicdesign.testUtils.http;

import org.organicdesign.testUtils.http.FakeHttpServletRequest.Companion.Kv;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 This mocks an HttpServletResponse.  It is a very raw and early version.
 */
public class FakeHttpServletResponse implements HttpServletResponse {
    private int status = 0;
    public String redirect = null;
    private Locale locale;
    private List<Map.Entry<String, String>> headers = new ArrayList<>();
    private String contentType = null;
    private boolean committed = false;
    private FakeServletOutputStream outputStream = new FakeServletOutputStream();

    @Override public Collection<String> getHeaderNames() {
        return headers.stream()
                      .map(Map.Entry::getKey)
                      .collect(Collectors.toSet());
    }
    @Override public String getHeader(String s) {
        Iterator<String> iter = getHeaders(s).iterator();
        return (iter.hasNext()) ? iter.next()
                                : null;
    }
    @Override public Collection<String> getHeaders(String s) {
        List<String> values = new ArrayList<>();
        if (s != null) {
            for (Map.Entry<String, String> head : headers) {
                if (s.equalsIgnoreCase(head.getKey())) {
                    values.add(head.getValue());
                }
            }
        }
        return values;
    }
    @Override public boolean containsHeader(String s) {
        if (s != null) {
            for (Map.Entry<String, String> head : headers) {
                if (s.equalsIgnoreCase(head.getKey())) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override public void setHeader(String s, String s1)  {
        if (s != null) {
            for (int i = 0; i < headers.size(); i++) {
                Map.Entry<String, String> head = headers.get(i);
                if (s.equalsIgnoreCase(head.getKey())) {
                    headers.set(i, new Kv(s, s1));
                    return;
                }
            }
            addHeader(s, s1);
        }
    }
    @Override public void addHeader(String s, String s1)  {
        headers.add(new Kv(s, s1));
    }
    @Override public void setDateHeader(String s, long l)  {
        setHeader(s, String.valueOf(l));
    }
    @Override public void addDateHeader(String s, long l)  {
        addHeader(s, String.valueOf(l));
    }
    @Override public void setIntHeader(String s, int i)  {
        setHeader(s, String.valueOf(i));
    }
    @Override public void addIntHeader(String s, int i)  {
        addHeader(s, String.valueOf(i));
    }

    @Override public void addCookie(Cookie cookie) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public String encodeURL(String s) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public String encodeRedirectURL(String s) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Deprecated
    @Override public String encodeUrl(String s) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Deprecated
    @Override public String encodeRedirectUrl(String s) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public void sendError(int i, String s) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public void sendError(int i) {
        if (committed) {
            throw new IllegalStateException("Response already committed.");
        }
        status = i;
        contentType = "text/html";
        committed = true;
        // TODO: "clear the buffer"
    }
    @Override public void sendRedirect(String s) { redirect = s; }
    @Override public void setStatus(int i)  { status = i; }
    @Deprecated
    @Override public void setStatus(int i, String s)  { status = i; }
    @Override public int getStatus() { return status; }
    @Override public String getCharacterEncoding() {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public String getContentType() { return contentType; }
    @Override public ServletOutputStream getOutputStream() {
        return outputStream;
    }
    @Override public PrintWriter getWriter() {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public void setCharacterEncoding(String s) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public void setContentLength(int i) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public void setContentLengthLong(long l) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public void setContentType(String s) { contentType = s; }
    @Override public void setBufferSize(int i) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public int getBufferSize() {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public void flushBuffer() {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public void resetBuffer() {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public boolean isCommitted() { return committed; }
    @Override public void reset() {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public void setLocale(Locale locale)  { this.locale = locale; }
    @Override public Locale getLocale() { return locale; }

    public static FakeHttpServletResponse httpServletResponse() {
        return new FakeHttpServletResponse() { };
    }
}
