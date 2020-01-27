// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.server;

public interface ServerHttpAsyncRequestControl
{
    void start();
    
    void start(final long p0);
    
    boolean isStarted();
    
    void complete();
    
    boolean isCompleted();
}
