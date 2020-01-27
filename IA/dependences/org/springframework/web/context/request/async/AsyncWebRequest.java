// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import org.springframework.web.context.request.NativeWebRequest;

public interface AsyncWebRequest extends NativeWebRequest
{
    void setTimeout(final Long p0);
    
    void addTimeoutHandler(final Runnable p0);
    
    void addCompletionHandler(final Runnable p0);
    
    void startAsync();
    
    boolean isAsyncStarted();
    
    void dispatch();
    
    boolean isAsyncComplete();
}
