// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.server;

import java.io.IOException;
import javax.servlet.AsyncEvent;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.springframework.util.Assert;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;

public class ServletServerHttpAsyncRequestControl implements ServerHttpAsyncRequestControl, AsyncListener
{
    private static long NO_TIMEOUT_VALUE;
    private final ServletServerHttpRequest request;
    private final ServletServerHttpResponse response;
    private AsyncContext asyncContext;
    private AtomicBoolean asyncCompleted;
    
    public ServletServerHttpAsyncRequestControl(final ServletServerHttpRequest request, final ServletServerHttpResponse response) {
        this.asyncCompleted = new AtomicBoolean(false);
        Assert.notNull(request, "request is required");
        Assert.notNull(response, "response is required");
        Assert.isTrue(request.getServletRequest().isAsyncSupported(), "Async support must be enabled on a servlet and for all filters involved in async request processing. This is done in Java code using the Servlet API or by adding \"<async-supported>true</async-supported>\" to servlet and filter declarations in web.xml. Also you must use a Servlet 3.0+ container");
        this.request = request;
        this.response = response;
    }
    
    @Override
    public boolean isStarted() {
        return this.asyncContext != null && this.request.getServletRequest().isAsyncStarted();
    }
    
    @Override
    public boolean isCompleted() {
        return this.asyncCompleted.get();
    }
    
    @Override
    public void start() {
        this.start(ServletServerHttpAsyncRequestControl.NO_TIMEOUT_VALUE);
    }
    
    @Override
    public void start(final long timeout) {
        Assert.state(!this.isCompleted(), "Async processing has already completed");
        if (this.isStarted()) {
            return;
        }
        final HttpServletRequest servletRequest = this.request.getServletRequest();
        final HttpServletResponse servletResponse = this.response.getServletResponse();
        (this.asyncContext = servletRequest.startAsync((ServletRequest)servletRequest, (ServletResponse)servletResponse)).addListener((AsyncListener)this);
        if (timeout != ServletServerHttpAsyncRequestControl.NO_TIMEOUT_VALUE) {
            this.asyncContext.setTimeout(timeout);
        }
    }
    
    @Override
    public void complete() {
        if (this.isStarted() && !this.isCompleted()) {
            this.asyncContext.complete();
        }
    }
    
    public void onComplete(final AsyncEvent event) throws IOException {
        this.asyncContext = null;
        this.asyncCompleted.set(true);
    }
    
    public void onStartAsync(final AsyncEvent event) throws IOException {
    }
    
    public void onError(final AsyncEvent event) throws IOException {
    }
    
    public void onTimeout(final AsyncEvent event) throws IOException {
    }
    
    static {
        ServletServerHttpAsyncRequestControl.NO_TIMEOUT_VALUE = Long.MIN_VALUE;
    }
}
