package org.organicdesign.testUtils;

import org.jetbrains.annotations.Nullable;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 This mocks an HttpServletRequest - EXPERIMENTAL
 */
@SuppressWarnings("WeakerAccess")
public class FakeHttpServletRequest implements HttpServletRequest {

    // HTTP headers are case-insensitive.
    // https://stackoverflow.com/questions/8236945/case-insensitive-string-as-hashmap-key
    // For case insensitive hack:
    // https://stackoverflow.com/questions/8236945/case-insensitive-string-as-hashmap-key
    // Actual implementation in Jetty uses an *array*.
    private final Map.Entry<String,String>[] heads;


    private final String baseUrl;
    private final String uri;
    private final Map<String,List<String>> params;

    private Locale locale = Locale.US;
    private Map<String,Object> attributes = new HashMap<>();
    private String characterEncoding = "UTF-8";

    /**
     Mocks a fake HTTP servlet request for testing.

     @param baseU http://localhost:8080
     @param u /Goodbye/cruel/world
     @param hs the HTTP headers as a map of keys/values
     Technically this maps keys to a list of values, but it's simpler to just map to a
     single value.
     @param ps the request parameters as a map of keys and lists of values.
     */
    @SuppressWarnings("unchecked")
    private FakeHttpServletRequest(String baseU,
                                   String u,
                                   List<? extends Map.Entry<String,String>> hs,
                                   Map<String,List<String>> ps) {
        baseUrl = baseU;
        uri = u;
        heads = hs.toArray((Map.Entry<String,String>[]) new Map.Entry[0]);
        params = ps;
    }

    // These are public fields that you can mutate freely
    // Kind of a low-budget way to get an implementation going quickly.
    // Should probably make a builder instead.
    public String method = "GET";
    public String requestedSessionId = "2FCF6F9AA75782B8B783308DE74BC557";
    public String remoteAddr = "0:0:0:0:0:0:0:1";

    @Override public String getAuthType() {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public Cookie[] getCookies() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public long getDateHeader(String s) { throw new UnsupportedOperationException("Not implemented"); }
    @Override public String getHeader(String s) {
        if (s != null) {
            for (Map.Entry<String, String> head : heads) {
                if (s.equalsIgnoreCase(head.getKey())) {
                    return head.getValue();
                }
            }
        }
        return null;
    }
    @Override public Enumeration<String> getHeaders(String s) {
        String header = getHeader(s);
        return (header == null) ? null : enumeration(Collections.singletonList(header));
    }
    @Override public Enumeration<String> getHeaderNames() {
        return enumeration(Arrays.stream(heads)
                                 .map(Map.Entry::getKey)
                                 .collect(Collectors.toList()));
    }
    @Override public int getIntHeader(String s) {
        String v = getHeader(s);
        return v == null ? -1 : Integer.parseInt(v);
    }
    @Override public String getMethod() { return method; }
    // 2018-03-02: Tomcat 8 can return null here.  Jetty does not.
    @Override public @Nullable String getPathInfo() { return uri; }
    @Override public String getPathTranslated() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public String getContextPath() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public String getQueryString() {
        StringBuilder sB = new StringBuilder();
        for (Map.Entry<String,List<String>> kv :params.entrySet()) {
            for (String v : kv.getValue()) {
                sB.append(sB.length() > 0 ? "&" : "")
                  .append(kv.getKey()).append("=")
                  .append(v);
            }
        }
        return sB.toString();
    }
    @Override public String getRemoteUser() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public boolean isUserInRole(String s) { throw new UnsupportedOperationException("Not implemented"); }
    @Override public Principal getUserPrincipal() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public String getRequestedSessionId() {
        return requestedSessionId;
    }
    @Override public String getRequestURI() { return uri; }
    @Override public StringBuffer getRequestURL() {
        return new StringBuffer(baseUrl).append(uri);
    }
    @Override public String getServletPath() { return uri; }
    @Override public HttpSession getSession(boolean b) { throw new UnsupportedOperationException("Not implemented"); }
    @Override public HttpSession getSession() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public String changeSessionId() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public boolean isRequestedSessionIdValid() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public boolean isRequestedSessionIdFromCookie() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public boolean isRequestedSessionIdFromURL() { throw new UnsupportedOperationException("Not implemented"); }
    @Deprecated
    @Override public boolean isRequestedSessionIdFromUrl() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public boolean authenticate(HttpServletResponse httpServletResponse) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public void login(String s, String s1) { }
    @Override public void logout() { }
    @Override public Collection<Part> getParts() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public Part getPart(String s) { throw new UnsupportedOperationException("Not implemented"); }
    @Override public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) { throw new UnsupportedOperationException("Not implemented"); }
    @Override public Object getAttribute(String s) { return attributes.get(s); }
    @Override public Enumeration<String> getAttributeNames() {
        return enumeration(attributes.keySet());
    }
    @Override public String getCharacterEncoding() { return characterEncoding; }
    @Override public void setCharacterEncoding(String s) { characterEncoding = s; }
    @Override public int getContentLength() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public long getContentLengthLong() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public String getContentType() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public ServletInputStream getInputStream() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public String getParameter(String s) { throw new UnsupportedOperationException("Not implemented"); }
    @Override public Enumeration<String> getParameterNames() {
        return enumeration(params.keySet());
    }
    @Override public String[] getParameterValues(String s) {
        List<String> os = params.get(s);
        return (os == null) ? null : os.toArray(new String[0]);
    }
    @Override public Map<String,String[]> getParameterMap() {
        Map<String,String[]> ret = new HashMap<>();
        for (Map.Entry<String,List<String>> entry : params.entrySet()) {
            List<String> val = entry.getValue();
            ret.put(entry.getKey(), val.toArray(new String[0]));
        }
        return ret;
    }
    @Override public String getProtocol() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public String getScheme() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public String getServerName() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public int getServerPort() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public BufferedReader getReader() { throw new UnsupportedOperationException("Not implemented"); }

    // Looks like an IP address...
    @Override public String getRemoteAddr() { return remoteAddr; }
    @Override public String getRemoteHost() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public void setAttribute(String s, Object o) { attributes.put(s, o); }
    @Override public void removeAttribute(String s) { attributes.remove(s); }
    @Override public Locale getLocale() { return locale; }
    @Override public Enumeration<Locale> getLocales() {
        return enumeration(Collections.singletonList(locale));
    }
    @Override public boolean isSecure() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public RequestDispatcher getRequestDispatcher(String s) { throw new UnsupportedOperationException("Not implemented"); }
    @Deprecated
    @Override public String getRealPath(String s) { throw new UnsupportedOperationException("Not implemented"); }
    @Override public int getRemotePort() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public String getLocalName() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public String getLocalAddr() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public int getLocalPort() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public ServletContext getServletContext() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public AsyncContext startAsync() throws IllegalStateException { throw new UnsupportedOperationException("Not implemented"); }
    @Override public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
            throws IllegalStateException {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override public boolean isAsyncStarted() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public boolean isAsyncSupported() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public AsyncContext getAsyncContext() { throw new UnsupportedOperationException("Not implemented"); }
    @Override public DispatcherType getDispatcherType() { throw new UnsupportedOperationException("Not implemented");
    }

    // GET params come from getQueryString()
    // POST params come from

    /**
     Mocks a fake HTTP servlet request for testing.

     @param baseUrl http://localhost:8080
     @param uri /Goodbye/cruel/world
     @param headers the HTTP headers as a map of keys/values
     Technically this maps keys to a list of values, but it's simpler to just map to a
     single value.
     @param params the request parameters as a map of keys and lists of values.
     @return a fake HTTP servlet request.
     */
    public static FakeHttpServletRequest fakeReq(
            String baseUrl,
            String uri,
            List<? extends Map.Entry<String,String>> headers,
            Map<String,List<String>> params) {
        return new FakeHttpServletRequest(baseUrl, uri, headers, params);
    }

    // Use this to pass the headers.
    public static class HttpField implements Map.Entry<String,String> {
        private final String key;
        private final String value;
        public HttpField(String k, String v) { key = k; value = v; }

        @Override
        public String getKey() { return key; }

        @Override
        public String getValue() { return value; }

        @Override @Deprecated
        public String setValue(String s) {
            throw new UnsupportedOperationException("No mutation");
        }
    }

    static <E> Enumeration<E> enumeration(Iterable<E> iterable) {
        return new Enumeration<E>() {
            Iterator<E> iter = iterable.iterator();
            @Override public boolean hasMoreElements() { return iter.hasNext(); }
            @Override public E nextElement() { return iter.next(); }
        };
    }
}
