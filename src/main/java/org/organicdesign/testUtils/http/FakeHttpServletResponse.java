package org.organicdesign.testUtils.http;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

/**
 This mocks an HttpServletResponse.  It is a very raw and early version.
 */
@SuppressWarnings("WeakerAccess")
public class FakeHttpServletResponse implements HttpServletResponse {
    private int status = 0;
    public String redirect = null;
    private Locale locale;

    @Override public void addCookie(Cookie cookie) { }
    @Override public boolean containsHeader(String s) { return false; }
    @Override public String encodeURL(String s) { return null; }
    @Override public String encodeRedirectURL(String s) { return null; }
    @Deprecated
    @Override public String encodeUrl(String s) { return null; }
    @Deprecated
    @Override public String encodeRedirectUrl(String s) { return null; }
    @Override public void sendError(int i, String s) { }
    @Override public void sendError(int i) { }
    @Override public void sendRedirect(String s) { redirect = s; }
    @Override public void setDateHeader(String s, long l)  { }
    @Override public void addDateHeader(String s, long l)  { }
    @Override public void setHeader(String s, String s1)  { }
    @Override public void addHeader(String s, String s1)  { }
    @Override public void setIntHeader(String s, int i)  { }
    @Override public void addIntHeader(String s, int i)  { }
    @Override public void setStatus(int i)  { status = i; }
    @Deprecated
    @Override public void setStatus(int i, String s)  { status = i; }
    @Override public int getStatus() { return status; }
    @Override public String getHeader(String s) { return null; }
    @Override public Collection<String> getHeaders(String s) { return null; }
    @Override public Collection<String> getHeaderNames() { return null; }
    @Override public String getCharacterEncoding() { return null; }
    @Override public String getContentType() { return null; }
    @Override public ServletOutputStream getOutputStream() { return null; }
    @Override public PrintWriter getWriter() { return null; }
    @Override public void setCharacterEncoding(String s)  { }
    @Override public void setContentLength(int i)  { }
    @Override public void setContentLengthLong(long l)  { }
    @Override public void setContentType(String s)  { }
    @Override public void setBufferSize(int i)  { }
    @Override public int getBufferSize() { return 0; }
    @Override public void flushBuffer() { }
    @Override public void resetBuffer()  { }
    @Override public boolean isCommitted() { return false; }
    @Override public void reset()  { }
    @Override public void setLocale(Locale locale)  { this.locale = locale; }
    @Override public Locale getLocale() { return locale; }

    public static FakeHttpServletResponse httpServletResponse() {
        return new FakeHttpServletResponse() { };
    }
}
