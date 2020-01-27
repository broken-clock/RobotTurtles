// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

public class NoSupportAsyncWebRequest extends ServletWebRequest implements AsyncWebRequest
{
    public NoSupportAsyncWebRequest(final HttpServletRequest request, final HttpServletResponse response) {
        super(request, response);
    }
    
    @Override
    public void addCompletionHandler(final Runnable runnable) {
    }
    
    @Override
    public void setTimeout(final Long timeout) {
    }
    
    @Override
    public void addTimeoutHandler(final Runnable runnable) {
    }
    
    @Override
    public boolean isAsyncStarted() {
        return false;
    }
    
    @Override
    public void startAsync() {
        throw new UnsupportedOperationException("No async support in a pre-Servlet 3.0 runtime");
    }
    
    @Override
    public boolean isAsyncComplete() {
        throw new UnsupportedOperationException("No async support in a pre-Servlet 3.0 runtime");
    }
    
    @Override
    public void dispatch() {
        throw new UnsupportedOperationException("No async support in a pre-Servlet 3.0 runtime");
    }
}
