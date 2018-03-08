package org.organicdesign.testUtils.experimental;

import org.jetbrains.annotations.Nullable;
import org.organicdesign.fp.collections.ImMap;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.organicdesign.fp.StaticImports.xform;

/**
 This mocks httpServletRequests and Responses.
 */
public class TestHttpServletRequest {

    // GET params come from getQueryString()
    // POST params come from

    /**
     Mocks a fake HTTP servlet request for testing.

     @param baseUrl http://localhost:8080
     @param uri /Goodbye/cruel/world
     @param headers the HTTP headers as a map of keys/values
     @param params the request parameters as a map of keys and lists of values.
     @return a fake HTTP servlet request.
     */
    public static HttpServletRequest httpServletRequest(String baseUrl,
                                                        String uri,
                                                        ImMap<String,String> headers,
                                                        Map<String,List<String>> params) {
        return new HttpServletRequest() {
            private Locale locale = Locale.US;
            private Map<String,Object> attributes = new HashMap<>();

            @Override public String getAuthType() { return null; }
            @Override public Cookie[] getCookies() { return new Cookie[0]; }
            @Override public long getDateHeader(String s) { return new Date().getTime(); }
            @Override public String getHeader(String s) { return headers.get(s); }
            @Override public Enumeration<String> getHeaders(String s) {
                return enumeration(headers.map((kv) -> kv.getKey() + ":" + kv.getValue()));
            }
            @Override public Enumeration<String> getHeaderNames() {
                return enumeration(headers.keySet());
            }
            @Override public int getIntHeader(String s) {
                String v = getHeader(s);
                return v == null ? -1 : Integer.parseInt(v);
            }
            @Override public String getMethod() { return "GET"; }
            // 2018-03-02: Tomcat 8 can return null here.  Jetty does not.
            @Override public @Nullable String getPathInfo() { return uri; }
            @Override public String getPathTranslated() { return null; }
            @Override public String getContextPath() { return null; }
            @Override public String getQueryString() {
                return xform(params.entrySet()).fold(new StringBuilder(),
                                                     (sB, kv) -> sB.append(sB.length() > 0 ? "&" : "")
                                                                   .append(kv.getKey()).append("=")
                                                                   .append(kv.getValue()))
                                               .toString();
            }
            @Override public String getRemoteUser() { return null; }
            @Override public boolean isUserInRole(String s) { return false; }
            @Override public Principal getUserPrincipal() { return null; }
            @Override public String getRequestedSessionId() { return "2FCF6F9AA75782B8B783308DE74BC557"; }
            @Override public String getRequestURI() { return uri; }
            @Override public StringBuffer getRequestURL() {
                return new StringBuffer(baseUrl).append(uri);
            }
            @Override public String getServletPath() { return "/PlanBase/SupportEnvVars.act"; }
            @Override public HttpSession getSession(boolean b) { return null; }
            @Override public HttpSession getSession() { return null; }
            @Override public String changeSessionId() { return null; }
            @Override public boolean isRequestedSessionIdValid() { return true; }
            @Override public boolean isRequestedSessionIdFromCookie() { return false; }
            @Override public boolean isRequestedSessionIdFromURL() { return true; }
            @SuppressWarnings({"deprecation"})
            @Override public boolean isRequestedSessionIdFromUrl() { return false; }
            @Override public boolean authenticate(HttpServletResponse httpServletResponse) {
                return false;
            }
            @Override public void login(String s, String s1) throws ServletException { }
            @Override public void logout() throws ServletException { }
            @Override public Collection<Part> getParts() throws IOException, ServletException { return null; }
            @Override public Part getPart(String s) throws IOException, ServletException { return null; }
            @Override public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass)
                    throws IOException, ServletException {
                return null;
            }
            @Override public Object getAttribute(String s) { return attributes.get(s); }
            @Override public Enumeration<String> getAttributeNames() {
                return new Enumeration<String>() {
                    private final Iterator<String> iter = attributes.keySet().iterator();
                    @Override public boolean hasMoreElements() { return iter.hasNext(); }
                    @Override public String nextElement() { return iter.next(); }
                };
            }
            @Override public String getCharacterEncoding() { return null; }
            @Override public void setCharacterEncoding(String s) throws UnsupportedEncodingException { }
            @Override public int getContentLength() { return 0; }
            @Override public long getContentLengthLong() { return 0; }
            @Override public String getContentType() { return null; }
            @Override public ServletInputStream getInputStream() throws IOException { return null; }
            @Override public String getParameter(String s) { return null; }
            @Override public Enumeration<String> getParameterNames() {
                return enumeration(params.keySet());
            }
            @Override public String[] getParameterValues(String s) {
                List<String> os = params.get(s);
                return (os == null) ? null : os.toArray(new String[os.size()]);
            }
            @Override public Map<String,String[]> getParameterMap() {
                Map<String,String[]> ret = new HashMap<>();
                for (Map.Entry<String,List<String>> entry : params.entrySet()) {
                    List<String> val = entry.getValue();
                    ret.put(entry.getKey(), val.toArray(new String[val.size()]));
                }
                return ret;
            }
            @Override public String getProtocol() { return null; }
            @Override public String getScheme() { return null; }
            @Override public String getServerName() { return null; }
            @Override public int getServerPort() { return 0; }
            @Override public BufferedReader getReader() throws IOException { return null; }
            @Override public String getRemoteAddr() { return null; }
            @Override public String getRemoteHost() { return null; }
            @Override public void setAttribute(String s, Object o) { attributes.put(s, o); }
            @Override public void removeAttribute(String s) { attributes.remove(s); }
            @Override public Locale getLocale() { return locale; }
            @Override public Enumeration<Locale> getLocales() { return new Enumeration<Locale>() {
                boolean hasMoreElements = true;
                @Override public boolean hasMoreElements() { return hasMoreElements; }
                @Override public Locale nextElement() {
                    hasMoreElements = false;
                    return locale;
                }
            }; }
            @Override public boolean isSecure() { return false; }
            @Override public RequestDispatcher getRequestDispatcher(String s) { return null; }
            @SuppressWarnings({"deprecation"})
            @Override public String getRealPath(String s) { return null; }
            @Override public int getRemotePort() { return 0; }
            @Override public String getLocalName() { return null; }
            @Override public String getLocalAddr() { return null; }
            @Override public int getLocalPort() { return 0; }
            @Override public ServletContext getServletContext() { return null; }
            @Override public AsyncContext startAsync() throws IllegalStateException { return null; }
            @Override public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
                    throws IllegalStateException {
                return null;
            }
            @Override public boolean isAsyncStarted() { return false; }
            @Override public boolean isAsyncSupported() { return false; }
            @Override public AsyncContext getAsyncContext() { return null; }
            @Override public DispatcherType getDispatcherType() { return null;
            }
        };
    }

    public static <E> Enumeration<E> enumeration(Iterable<E> iterable) {
        return new Enumeration<E>() {
            Iterator<E> iter = iterable.iterator();
            @Override public boolean hasMoreElements() { return iter.hasNext(); }
            @Override public E nextElement() { return iter.next(); }
        };
    }

    public static class TestHttpServletResponse implements HttpServletResponse {
        private int status = 0;
        public String redirect = null;
        @Override public void addCookie(Cookie cookie) { }
        @Override public boolean containsHeader(String s) { return false; }
        @Override public String encodeURL(String s) { return null; }
        @Override public String encodeRedirectURL(String s) { return null; }
        @Deprecated
        @Override public String encodeUrl(String s) { return null; }
        @Deprecated
        @Override public String encodeRedirectUrl(String s) { return null; }
        @Override public void sendError(int i, String s) throws IOException  { }
        @Override public void sendError(int i) throws IOException  { }
        @Override public void sendRedirect(String s) throws IOException  { redirect = s; }
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
        @Override public ServletOutputStream getOutputStream() throws IOException { return null; }
        @Override public PrintWriter getWriter() throws IOException { return null; }
        @Override public void setCharacterEncoding(String s)  { }
        @Override public void setContentLength(int i)  { }
        @Override public void setContentLengthLong(long l)  { }
        @Override public void setContentType(String s)  { }
        @Override public void setBufferSize(int i)  { }
        @Override public int getBufferSize() { return 0; }
        @Override public void flushBuffer() throws IOException  { }
        @Override public void resetBuffer()  { }
        @Override public boolean isCommitted() { return false; }
        @Override public void reset()  { }
        @Override public void setLocale(Locale locale)  { }
        @Override public Locale getLocale() { return null; }

    }
    public static TestHttpServletResponse httpServletResponse() {
        return new TestHttpServletResponse() { };
    }
}
