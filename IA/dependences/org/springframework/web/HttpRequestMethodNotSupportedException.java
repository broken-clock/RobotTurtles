// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web;

import java.util.Collections;
import java.util.LinkedHashSet;
import org.springframework.http.HttpMethod;
import java.util.Set;
import java.util.Collection;
import javax.servlet.ServletException;

public class HttpRequestMethodNotSupportedException extends ServletException
{
    private String method;
    private String[] supportedMethods;
    
    public HttpRequestMethodNotSupportedException(final String method) {
        this(method, (String[])null);
    }
    
    public HttpRequestMethodNotSupportedException(final String method, final String[] supportedMethods) {
        this(method, supportedMethods, "Request method '" + method + "' not supported");
    }
    
    public HttpRequestMethodNotSupportedException(final String method, final Collection<String> supportedMethods) {
        this(method, supportedMethods.toArray(new String[supportedMethods.size()]));
    }
    
    public HttpRequestMethodNotSupportedException(final String method, final String msg) {
        this(method, null, msg);
    }
    
    public HttpRequestMethodNotSupportedException(final String method, final String[] supportedMethods, final String msg) {
        super(msg);
        this.method = method;
        this.supportedMethods = supportedMethods;
    }
    
    public String getMethod() {
        return this.method;
    }
    
    public String[] getSupportedMethods() {
        return this.supportedMethods;
    }
    
    public Set<HttpMethod> getSupportedHttpMethods() {
        final Set<HttpMethod> supportedMethods = new LinkedHashSet<HttpMethod>();
        for (final String value : this.supportedMethods) {
            supportedMethods.add(HttpMethod.valueOf(value));
        }
        return Collections.unmodifiableSet((Set<? extends HttpMethod>)supportedMethods);
    }
}
