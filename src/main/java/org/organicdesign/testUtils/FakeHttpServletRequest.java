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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
public class FakeHttpServletRequest {

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
                                                        Map<String,String> headers,
                                                        Map<String,List<String>> params) {
        return new HttpServletRequest() {
            private Locale locale = Locale.US;
            private Map<String,Object> attributes = new HashMap<>();
            private String characterEncoding = "UTF-8";

            @Override public String getAuthType() { return null; }
            @Override public Cookie[] getCookies() { return new Cookie[0]; }
            @Override public long getDateHeader(String s) { return new Date().getTime(); }
            @Override public String getHeader(String s) { return headers.get(s); }
            @Override public Enumeration<String> getHeaders(String s) {
                return enumeration(headers.entrySet()
                                          .stream()
                                          .map(kv -> kv.getKey() + ":" + kv.getValue())
                                          .collect(Collectors.toList()));
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
            @Override public String getRemoteUser() { return null; }
            @Override public boolean isUserInRole(String s) { return false; }
            @Override public Principal getUserPrincipal() { return null; }
            @Override public String getRequestedSessionId() { return "2FCF6F9AA75782B8B783308DE74BC557"; }
            @Override public String getRequestURI() { return uri; }
            @Override public StringBuffer getRequestURL() {
                return new StringBuffer(baseUrl).append(uri);
            }
            @Override public String getServletPath() { return uri; }
            @Override public HttpSession getSession(boolean b) { return null; }
            @Override public HttpSession getSession() { return null; }
            @Override public String changeSessionId() { return null; }
            @Override public boolean isRequestedSessionIdValid() { return true; }
            @Override public boolean isRequestedSessionIdFromCookie() { return false; }
            @Override public boolean isRequestedSessionIdFromURL() { return true; }
            @Override public boolean isRequestedSessionIdFromUrl() { return false; }
            @Override public boolean authenticate(HttpServletResponse httpServletResponse) {
                return false;
            }
            @Override public void login(String s, String s1) { }
            @Override public void logout() { }
            @Override public Collection<Part> getParts() { return null; }
            @Override public Part getPart(String s) { return null; }
            @Override public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) { return null; }
            @Override public Object getAttribute(String s) { return attributes.get(s); }
            @Override public Enumeration<String> getAttributeNames() {
                return enumeration(attributes.keySet());
            }
            @Override public String getCharacterEncoding() { return characterEncoding; }
            @Override public void setCharacterEncoding(String s) { characterEncoding = s; }
            @Override public int getContentLength() { return 0; }
            @Override public long getContentLengthLong() { return 0; }
            @Override public String getContentType() { return null; }
            @Override public ServletInputStream getInputStream() { return null; }
            @Override public String getParameter(String s) { return null; }
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
            @Override public String getProtocol() { return null; }
            @Override public String getScheme() { return null; }
            @Override public String getServerName() { return null; }
            @Override public int getServerPort() { return 0; }
            @Override public BufferedReader getReader() { return null; }

            // Looks like an IP address...
            @Override public String getRemoteAddr() { return "0:0:0:0:0:0:0:1"; }
            @Override public String getRemoteHost() { return null; }
            @Override public void setAttribute(String s, Object o) { attributes.put(s, o); }
            @Override public void removeAttribute(String s) { attributes.remove(s); }
            @Override public Locale getLocale() { return locale; }
            @Override public Enumeration<Locale> getLocales() {
                return enumeration(Collections.singletonList(locale));
            }
            @Override public boolean isSecure() { return false; }
            @Override public RequestDispatcher getRequestDispatcher(String s) { return null; }
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

    static <E> Enumeration<E> enumeration(Iterable<E> iterable) {
        return new Enumeration<E>() {
            Iterator<E> iter = iterable.iterator();
            @Override public boolean hasMoreElements() { return iter.hasNext(); }
            @Override public E nextElement() { return iter.next(); }
        };
    }
}
