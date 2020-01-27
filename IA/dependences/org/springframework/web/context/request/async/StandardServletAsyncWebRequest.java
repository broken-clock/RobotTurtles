// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import java.util.Iterator;
import java.io.IOException;
import javax.servlet.AsyncEvent;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.springframework.util.Assert;
import java.util.ArrayList;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import org.springframework.web.context.request.ServletWebRequest;

public class StandardServletAsyncWebRequest extends ServletWebRequest implements AsyncWebRequest, AsyncListener
{
    private Long timeout;
    private AsyncContext asyncContext;
    private AtomicBoolean asyncCompleted;
    private final List<Runnable> timeoutHandlers;
    private final List<Runnable> completionHandlers;
    
    public StandardServletAsyncWebRequest(final HttpServletRequest request, final HttpServletResponse response) {
        super(request, response);
        this.asyncCompleted = new AtomicBoolean(false);
        this.timeoutHandlers = new ArrayList<Runnable>();
        this.completionHandlers = new ArrayList<Runnable>();
    }
    
    @Override
    public void setTimeout(final Long timeout) {
        Assert.state(!this.isAsyncStarted(), "Cannot change the timeout with concurrent handling in progress");
        this.timeout = timeout;
    }
    
    @Override
    public void addTimeoutHandler(final Runnable timeoutHandler) {
        this.timeoutHandlers.add(timeoutHandler);
    }
    
    @Override
    public void addCompletionHandler(final Runnable runnable) {
        this.completionHandlers.add(runnable);
    }
    
    @Override
    public boolean isAsyncStarted() {
        return this.asyncContext != null && this.getRequest().isAsyncStarted();
    }
    
    @Override
    public boolean isAsyncComplete() {
        return this.asyncCompleted.get();
    }
    
    @Override
    public void startAsync() {
        Assert.state(this.getRequest().isAsyncSupported(), "Async support must be enabled on a servlet and for all filters involved in async request processing. This is done in Java code using the Servlet API or by adding \"<async-supported>true</async-supported>\" to servlet and filter declarations in web.xml.");
        Assert.state(!this.isAsyncComplete(), "Async processing has already completed");
        if (this.isAsyncStarted()) {
            return;
        }
        (this.asyncContext = this.getRequest().startAsync((ServletRequest)this.getRequest(), (ServletResponse)this.getResponse())).addListener((AsyncListener)this);
        if (this.timeout != null) {
            this.asyncContext.setTimeout((long)this.timeout);
        }
    }
    
    @Override
    public void dispatch() {
        Assert.notNull(this.asyncContext, "Cannot dispatch without an AsyncContext");
        this.asyncContext.dispatch();
    }
    
    public void onStartAsync(final AsyncEvent event) throws IOException {
    }
    
    public void onError(final AsyncEvent event) throws IOException {
    }
    
    public void onTimeout(final AsyncEvent event) throws IOException {
        for (final Runnable handler : this.timeoutHandlers) {
            handler.run();
        }
    }
    
    public void onComplete(final AsyncEvent event) throws IOException {
        for (final Runnable handler : this.completionHandlers) {
            handler.run();
        }
        this.asyncContext = null;
        this.asyncCompleted.set(true);
    }
}
